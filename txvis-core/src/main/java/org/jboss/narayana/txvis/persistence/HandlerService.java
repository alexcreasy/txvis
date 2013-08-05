package org.jboss.narayana.txvis.persistence;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.Configuration;
import org.jboss.narayana.txvis.interceptors.LoggingInterceptor;
import org.jboss.narayana.txvis.persistence.entities.*;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.ejb.*;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.persistence.*;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
/**
 *
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/06/2013
 * Time: 12:20
 */
@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors(LoggingInterceptor.class)
public class HandlerService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final String nodeid = System.getProperty(Configuration.NODEID_SYS_PROP_NAME);

    private final Map<String, RequestRecord.CompositePK> threadReqMap = new HashMap<>();

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager em;


    /*
     * These methods provide the logic for handling log lines output by
     * com.arjuna.ats.arjuna.coordinator.BasicAction
     *
     */

    public void begin(String txuid, Timestamp timestamp, String threadId) {
        em.getTransaction().begin();
        Transaction tx = new Transaction(txuid, nodeid, timestamp);
        em.persist(tx);
        em.getTransaction().commit();


        // Check to see if this is a subordinate transaction
        if (threadReqMap.containsKey(threadId)) {
            RequestRecord.CompositePK requestKey = threadReqMap.remove(threadId);
            RequestRecord rec = handleRequest(requestKey.getRequestId(), requestKey.getIor(), txuid);

            // if rec == null we don't have enough information yet to establish the transaction hierarchy
            if (rec != null) {
                em.getTransaction().begin();

                createHierarchy(rec.getNodeid(), nodeid, txuid);
                em.remove(rec);

                em.getTransaction().commit();
            }
        }
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void prepare(String txuid, Timestamp timestamp) {
        em.getTransaction().begin();
        setStatus(txuid, Status.PREPARE, timestamp);
        em.getTransaction().commit();
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void phase2Commit(String txuid, Timestamp timestamp) {
        em.getTransaction().begin();
        setStatus(txuid, Status.COMMIT, timestamp);
        em.getTransaction().commit();
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void onePhaseCommit(String txuid, Timestamp timestamp) {
        em.getTransaction().begin();
        setStatus(txuid, Status.ONE_PHASE_COMMIT, timestamp);
        em.getTransaction().commit();
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void abort(String txuid, Timestamp timestamp) {
        em.getTransaction().begin();
        setStatus(txuid, Status.PHASE_ONE_ABORT, timestamp);
        em.getTransaction().commit();
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void phase2Abort(String txuid, Timestamp timestamp) {
        em.getTransaction().begin();
        setStatus(txuid, Status.PHASE_TWO_ABORT, timestamp);
        em.getTransaction().commit();
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void txPrepareFailed(String txuid, Timestamp timestamp) {
        em.getTransaction().begin();
        Transaction tx = findTransaction(this.nodeid, txuid);
        tx.addEvent(new Event(EventType.PREPARE_FAILED, this.nodeid, timestamp));
        em.getTransaction().commit();

    }


    private void setStatus(String txuid, Status status, Timestamp timestamp){
        Transaction tx;
        try {
            tx = em.createNamedQuery("Transaction.findNatural", Transaction.class)
                    .setParameter("nodeid", this.nodeid).setParameter("txuid", txuid).getSingleResult();
        }
        catch (NoResultException e) {
            logger.warn("HandlerService.setStatus: Could not retrieve Transaction entity with nodeid=`"+nodeid +
                    "`, txuid=`"+txuid+"`");
            return;
        }

        tx.setStatus(status, timestamp);
    }



    /*
     * The below methods deal with establishing and modelling a transaction hierarchy for distributed
     * transactions.
     */

    public void associateThreadWithRequestId(String threadId, Long requestId, String ior) {
        threadReqMap.put(threadId, new RequestRecord.CompositePK(requestId, ior));
    }

    private RequestRecord handleRequest(Long requestid, String ior) {
        return handleRequest(requestid, ior, null);
    }

    private RequestRecord handleRequest(Long requestid, String ior, String txuid) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.handleRequest( `{0}`, `{1}`, `{2}` )", requestid, ior, txuid));

        RequestRecord rec = em.find(RequestRecord.class, new RequestRecord.CompositePK(requestid, ior));

        if (rec == null) {
            try {

                if (logger.isTraceEnabled())
                    logger.trace("HandlerService.handleRequest - create request record");

                em.getTransaction().begin();

                em.persist(new RequestRecord(requestid, nodeid, ior, txuid));

                // Flush needs to be called to force a PersistenceException if creating the new record has
                // violated a primary key constraint, otherwise the JPA spec is unclear on which exception
                // will be thrown, Hibernate will usually throw a RollbackException which is less specific.
                em.flush();

                em.getTransaction().commit();
            }
            catch (PersistenceException pe) {

                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();

                if (logger.isTraceEnabled())
                    logger.trace("HandlerService.handleRequest - record already exists, retrieve");

                // Race condition: Another node has created a record in the meantime, retrieve a fresh copy.
                rec = em.find(RequestRecord.class, new RequestRecord.CompositePK(requestid, ior));

                if (rec == null)
                    // If we still can't find a record something else caused the PersistenceException!
                    logger.error("Unable to retrieve RequestRecord after PersistenceException", pe);
            }
        }
        return rec;
    }

    public void checkIfParent(String nodeid, Long requestId, String ior) {
        RequestRecord rec = handleRequest(requestId, ior);

        // rec == null => we just created a new record, therefore we don't have enough information to create
        // the hierarchy yet.
        if (rec != null) {
            em.getTransaction().begin();

            createHierarchy(nodeid, rec.getNodeid(), rec.getTxuid());
            em.remove(rec);

            em.getTransaction().commit();
        }
    }

    private void createHierarchy(String parentNodeId, String subordinateNodeId, String txuid) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.createHierarchy( `{0}`, `{1}`, `{2}` )",
                    parentNodeId, subordinateNodeId, txuid));

        Transaction subordinate = findTransaction(subordinateNodeId, txuid);
        Transaction parent = findTransaction(parentNodeId, txuid);
        subordinate.setParent(parent);
    }



    /*
     * The below methods deal with Transaction Participants
     */

    /**
     *
     * @param rmuid
     * @param timestamp
     */
    public void resourcePreparedJTS(String rmuid, Timestamp timestamp) {
        try {
            em.getTransaction().begin();

            ParticipantRecord rec = null;
            try {
                rec = em.createNamedQuery("ParticipantRecord.findByRmuid", ParticipantRecord.class)
                        .setParameter("rmuid", rmuid).getSingleResult();
            }
            catch (NoResultException e) {
                if (logger.isTraceEnabled())
                    logger.trace("Unable to find ParticipantRecord");
                em.getTransaction().rollback();
            }

            rec.getTransaction().addEvent(new Event(EventType.PREPARE, rec.getResourceManager().getJndiName(), timestamp));
            rec.setPrepareCalled(true);

            em.getTransaction().commit();
        }
        catch (RollbackException e) {
            if (logger.isTraceEnabled())
                logger.trace("Transaction Rolled Back");
        }
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param timestamp
     */
    public void resourcePreparedJTA(String txuid, String rmJndiName, Timestamp timestamp) {
        try {
            em.getTransaction().begin();

            final ParticipantRecord rec = findParticipantRecord(nodeid, txuid, rmJndiName);

            if (rec == null) {
                if (logger.isTraceEnabled())
                    logger.trace("Unable to find ParticipantRecord");
                em.getTransaction().rollback();
            }

            rec.setVote(Vote.COMMIT, timestamp);

            em.getTransaction().commit();
        }
        catch (RollbackException e) {
            if (logger.isTraceEnabled())
                logger.trace("Transaction Rolled Back");
        }
    }

    public void resourceFailedToPrepareJTS(String rmuid, String xaException, Timestamp timestamp) {
        try {
            em.getTransaction().begin();

            ParticipantRecord rec = null;
            try {
                rec = em.createNamedQuery("ParticipantRecord.findByRmuid", ParticipantRecord.class)
                        .setParameter("rmuid", rmuid).getSingleResult();
            }
            catch (NoResultException e) {
                if (logger.isTraceEnabled())
                    logger.trace("Unable to find ParticipantRecord");
                em.getTransaction().rollback();
            }

            rec.setVote(Vote.ABORT, timestamp);
            rec.setXaException(xaException);

            em.getTransaction().commit();
        }
        catch (RollbackException e) {
            if (logger.isTraceEnabled())
                logger.trace("Transaction Rolled Back");
        }
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param timestamp
     */
    public void resourceFailedToPrepareJTA(String txuid, String rmJndiName, String xaExceptionType, Timestamp timestamp) {
        try {
            em.getTransaction().begin();

            final ParticipantRecord rec = findParticipantRecord(nodeid, txuid, rmJndiName);

            if (rec == null) {
                em.getTransaction().rollback();
                if (logger.isTraceEnabled())
                    logger.trace("Unable to find ParticipantRecord");
            }

            rec.setVote(Vote.ABORT, timestamp);
            rec.setXaException(xaExceptionType);

            em.getTransaction().commit();
        }
        catch (RollbackException e) {
            if (logger.isTraceEnabled())
                logger.trace("Transaction Rolledback");
        }
    }

    public void resourceCommitOrAbortJTS(String rmuid, EventType eventType, Timestamp timestamp) {
        try {
            em.getTransaction().begin();

            ParticipantRecord rec = null;
            try {
                rec = em.createNamedQuery("ParticipantRecord.findByRmuid", ParticipantRecord.class)
                        .setParameter("rmuid", rmuid).getSingleResult();
            }
            catch (NoResultException e) {
                if (logger.isTraceEnabled())
                    logger.trace("Unable to find ParticipantRecord");
                em.getTransaction().rollback();
            }

            if (rec.isPrepareCalled() && rec.getXaException() == null)
                rec.setVote(Vote.COMMIT, timestamp);

            switch (eventType) {
                case COMMIT:
                    rec.getTransaction().addEvent(new Event(EventType.COMMIT, rec.getResourceManager().getJndiName(),
                            timestamp));
                    break;
                case ABORT:
                    rec.getTransaction().addEvent(new Event(EventType.ABORT, rec.getResourceManager().getJndiName(),
                            timestamp));
                    break;
            }

            em.getTransaction().commit();
        }
        catch (RollbackException e) {
            if (logger.isTraceEnabled())
                logger.trace("Transaction Rolled Back");
        }
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param rmProductName
     * @param rmProductVersion
     * @param timestamp
     */
    public void enlistResourceManagerJTS(String txuid, String rmuid, String rmJndiName, String rmProductName,
                                         String rmProductVersion, Timestamp timestamp) {
        try {
            em.getTransaction().begin();
            final ResourceManager rm = retrieveOrCreateResourceManager(rmJndiName, rmProductName, rmProductVersion);
            final Transaction tx = findTransaction(nodeid, txuid);

            // Error condition which usually only occurs when the tool is deployed mid transaction
            if (tx == null)
                em.getTransaction().rollback();

            final ParticipantRecord rec = new ParticipantRecord(tx, rm, timestamp);
            rec.setRmuid(rmuid);
            em.persist(rec);

            em.getTransaction().commit();
        }
        catch (RollbackException e) {
            if (logger.isTraceEnabled())
                logger.trace("Unable to find transaction");
        }
    }


    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param rmProductName
     * @param rmProductVersion
     * @param timestamp
     */
    public void enlistResourceManagerJTA(String txuid, String rmJndiName, String rmProductName,
                                                      String rmProductVersion, Timestamp timestamp) {
        try {
            em.getTransaction().begin();

            final ResourceManager rm = retrieveOrCreateResourceManager(rmJndiName, rmProductName, rmProductVersion);
            final Transaction tx = findTransaction(nodeid, txuid);

            // Error condition which usually only occurs when the tool is deployed mid transaction
            if (tx == null)
                em.getTransaction().rollback();

            final ParticipantRecord rec = new ParticipantRecord(tx, rm, timestamp);

            em.persist(rec);

            em.getTransaction().commit();
        }
        catch (RollbackException e) {
            if (logger.isTraceEnabled())
                logger.trace("Unable to find transaction");
        }
    }

    /**
     *
     * @param txuid
     */
    public void cleanup(String txuid) {

        final Transaction tx = findTransaction(nodeid, txuid);

        if (tx != null && tx.getParticipantRecords().size() == 0 && tx.getStatus().equals(Status.IN_FLIGHT)) {
            em.getTransaction().begin();
            em.remove(tx);
            em.getTransaction().commit();
            logger.info("Cleaned up phantom transaction: "+txuid);
        }
    }




    /*
     * TODO: Temporary place for some data access methods, these should be refactored out of this class.
     * Requires completion and integration of EntityManagerSession.
     *
     */


    private Transaction findTransaction(String nodeid, String txuid) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.findTransaction( `{0}`, `{1}` )", nodeid, txuid));

        try {
            return em.createNamedQuery("Transaction.findNatural", Transaction.class)
                    .setParameter("nodeid", nodeid).setParameter("txuid", txuid).getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    private ParticipantRecord findParticipantRecord(String nodeid, String txuid, String rmJndiName) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.findParticipantRecord( `{0}`, `{1}`, `{3}` )",
                    nodeid, txuid, rmJndiName));

        try {
            return em.createNamedQuery("ParticipantRecord.findNatural", ParticipantRecord.class)
                    .setParameter("nodeid", nodeid).setParameter("txuid", txuid).setParameter("jndiName", rmJndiName)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    private ResourceManager retrieveOrCreateResourceManager(String jndiName, String productName, String productVersion) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.retrieveOrCreateResourceManager(( `{0}`, `{1}`, `{3}` )",
                    jndiName, productName, productVersion));

        ResourceManager rm = em.find(ResourceManager.class, jndiName);

        if (rm == null) {
            rm = new ResourceManager(jndiName, productName, productVersion);
            em.persist(rm);
        }

        return rm;
    }

    @AroundInvoke
    private Object intercept(InvocationContext ctx) throws Exception {
        if (em == null || !em.isOpen())
            this.em = emf.createEntityManager();

        Object o = null;

        try {
            o = ctx.proceed();
        }

        catch (Throwable t) {
            logger.warn("HandlerService.intercept: Unhandled Exception", t);
        }
        finally {
            em.close();
        }
        return o;
    }
}