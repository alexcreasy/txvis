package org.jboss.narayana.txvis.persistence;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.Configuration;
import org.jboss.narayana.txvis.interceptors.LoggingInterceptor;
import org.jboss.narayana.txvis.persistence.dao.ParticipantRecordDAO;
import org.jboss.narayana.txvis.persistence.dao.ResourceManagerDAO;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.RequestRecord;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
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

    @EJB
    private TransactionDAO transactionDAO;

    @EJB
    private ResourceManagerDAO resourceManagerDAO;

    @EJB
    private ParticipantRecordDAO participantRecordDAO;

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
        setStatus(txuid, Status.PREPARE, timestamp);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void phase2Commit(String txuid, Timestamp timestamp) {
        setStatus(txuid, Status.COMMIT, timestamp);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void onePhaseCommit(String txuid, Timestamp timestamp) {
        setStatus(txuid, Status.ONE_PHASE_COMMIT, timestamp);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void abort(String txuid, Timestamp timestamp) {
        setStatus(txuid, Status.PHASE_ONE_ABORT, timestamp);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void phase2Abort(String txuid, Timestamp timestamp) {
        setStatus(txuid, Status.PHASE_TWO_ABORT, timestamp);
    }


    private void setStatus(String txuid, Status status, Timestamp timestamp){
        em.getTransaction().begin();

        Transaction tx;
        try {
            tx = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", this.nodeid).setParameter("txuid", txuid).getSingleResult();
        }
        catch (NoResultException e) {
            logger.warn("HandlerService.setStatus: Could not retrieve Transaction entity with nodeid=`"+nodeid +
                    "`, txuid=`"+txuid+"`");
            return;
        }

        tx.setStatus(status, timestamp);
        em.getTransaction().commit();
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
        em.getTransaction().begin();

        ParticipantRecord rec;
        try {
            rec = em.createNamedQuery("ParticipantRecord.findByUID", ParticipantRecord.class).setParameter("rmuid", rmuid)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            logger.warn("HandlerService.resourcePreparedJTS: Could not retrieve ParticipantRecord for rmuid=`"+rmuid+"`");
            return;
        }

        rec.setResourceOutcome(Vote.COMMIT, timestamp);

        em.getTransaction().commit();
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param timestamp
     */
    public void resourcePrepared(String txuid, String rmJndiName, Timestamp timestamp) {

        final ParticipantRecord rec = participantRecordDAO.retrieve(txuid, rmJndiName);

        if (rec == null)
            throw new IllegalStateException("HandlerService.resourcePrepared(), ParticipantRecord not found: " + txuid);

        rec.setResourceOutcome(Vote.COMMIT, timestamp);
        participantRecordDAO.update(rec);
    }

    public void resourceFailedToPrepareJTS(String rmuid, String xaException, Timestamp timestamp) {
        em.getTransaction().begin();

        ParticipantRecord rec;
        try {
            rec = em.createNamedQuery("ParticipantRecord.findByUID", ParticipantRecord.class)
                    .setParameter("rmuid", rmuid).getSingleResult();
        }
        catch (NoResultException e) {
            logger.warn("HandlerService.resourceFailedToPrepareJTS: Could not retrieve ParticipantRecord for rmuid=`"
                    + rmuid+"`");
            return;
        }

        rec.setResourceOutcome(Vote.ABORT, timestamp);
        rec.setXaException(xaException);

        em.getTransaction().commit();
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param timestamp
     */
    public void resourceFailedToPrepare(String txuid, String rmJndiName, String xaExceptionType, Timestamp timestamp) {

        final ParticipantRecord rec = participantRecordDAO.retrieve(txuid, rmJndiName);

        if (rec == null) {
            logger.warn("Could not retrieve, exitting");
            return;
        }

        rec.setResourceOutcome(Vote.ABORT, timestamp);
        rec.setXaException(xaExceptionType);
        participantRecordDAO.update(rec);
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param rmProductName
     * @param rmProductVersion
     * @param timestamp
     */
    public void enlistResourceManagerByUID(String txuid, String rmuid, String rmJndiName, String rmProductName,
                                           String rmProductVersion, Timestamp timestamp) {
        Transaction tx;
        try {
            tx = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", this.nodeid).setParameter("txuid", txuid).getSingleResult();
        }
        catch (NoResultException e) {
            logger.warn("HandlerService.enlistResourceManagerByUID: Unable to retrieve Transaction");
            return;
        }

        ResourceManager rm;
        try
        {
            rm = em.createNamedQuery("ResourceManager.findByJndiName" , ResourceManager.class)
                    .setParameter("jndiName", rmJndiName).getSingleResult();
        }
        catch (NoResultException x)
        {
            rm = new ResourceManager(rmJndiName, rmProductName, rmProductVersion);
            em.persist(rm);
        }

        em.getTransaction().begin();
        final ParticipantRecord rec = new ParticipantRecord(tx, rm, timestamp);
        rec.setRmuid(rmuid);
        em.persist(rec);
        em.getTransaction().commit();
    }


    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param rmProductName
     * @param rmProductVersion
     * @param timestamp
     */
    public ParticipantRecord enlistResourceManager(String txuid, String rmJndiName, String rmProductName,
                                                   String rmProductVersion, Timestamp timestamp) {

        ParticipantRecord rec = participantRecordDAO.retrieve(txuid, rmJndiName);

        if (rec != null) {
            logger.error("ParticipantRecord already created: "+rec);
        }
        else {
            ResourceManager rm = resourceManagerDAO.retrieve(rmJndiName);
            if (rm == null) {
                // Create the RM is it hasn't been enlisted before.
                rm = new ResourceManager(rmJndiName, rmProductName, rmProductVersion);
                resourceManagerDAO.create(rm);
            }

            // Enlist the RM as a Participant of this transaction
            rec = new ParticipantRecord(transactionDAO.retrieve(nodeid, txuid), rm, timestamp);
        }

        rec = participantRecordDAO.update(rec);
        return rec;
    }

    /**
     *
     * @param txuid
     */
    public void cleanup(String txuid) {

        Transaction tx = null;
        try {
            tx = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", this.nodeid).setParameter("txuid", txuid).getSingleResult();
        }
        catch (NoResultException e) {
            logger.warn("HandlerService.cleanup: Could not retrieve Transaction for nodeid=`"+nodeid+"`, txuid=`"+txuid+"`");
        }

        if (tx != null && tx.getParticipantRecords().size() == 0 && tx.getStatus().equals(Status.IN_FLIGHT)) {
            em.getTransaction().begin();
            em.remove(tx);
            em.getTransaction().commit();
            logger.info("Cleaned up phantom transaction: "+txuid);
        }
    }


    private Transaction findTransaction(String nodeid, String txuid) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.findTransaction( `{0}`, `{1}` )", nodeid, txuid));

        try {
            return em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", nodeid).setParameter("txuid", txuid).getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        if (em == null || !em.isOpen())
            this.em = emf.createEntityManager();

        Object o = null;

        try {
            o = ctx.proceed();
        }
        finally {
            em.close();
        }
        return o;
    }
}