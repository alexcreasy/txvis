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
import javax.interceptor.Interceptors;
import javax.persistence.*;
import java.sql.Timestamp;
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

    private final Map<String, CORBAid> threadReqMap = new HashMap<>();

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager em;

    @EJB
    private TransactionDAO transactionDAO;

    @EJB
    private ResourceManagerDAO resourceManagerDAO;

    @EJB
    private ParticipantRecordDAO participantRecordDAO;

    public void checkIfParent(String nodeid, Long requestId, String ior) {
        em = emf.createEntityManager();
        try
        {
            RequestRecord rec = null;

            try
            {
                rec = em.createNamedQuery("RequestRecord.findByRequestIdAndIOR", RequestRecord.class)
                        .setParameter("requestid", requestId).setParameter("ior", ior).getSingleResult();
            }
            catch (NoResultException e)
            {
                try
                {
                    if (logger.isTraceEnabled())
                        logger.trace("HandlerService.checkIfParent - create request record");

                    em.getTransaction().begin();

                        em.persist(new RequestRecord(requestId, nodeid, ior));
                        em.flush();

                    em.getTransaction().commit();
                    em.close();
                }
                catch (PersistenceException pe)
                {
                    if (logger.isTraceEnabled())
                        logger.trace("HandlerService.checkIfParent - request record already created, retrieve");

                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();

                    // Race condition: Another node has created a record in the meantime, retrieve a fresh copy.
                    try
                    {
                        rec = em.createNamedQuery("RequestRecord.findByRequestIdAndIOR", RequestRecord.class)
                                .setParameter("requestid", requestId).setParameter("ior", ior).getSingleResult();
                    }
                    catch (NoResultException nre)
                    {
                        // If we still can't find a record something else caused the PersistenceException!
                        logger.error("Unable to retrieve RequestRecord after PersistenceException", pe);
                    }
                }
            }

            // rec == null => we just created a new record, therefore we don't have enough information to create
            // the hierarchy yet.
            if (rec != null) {
                if (!em.isOpen())
                    em = emf.createEntityManager();

                em.getTransaction().begin();

                if (logger.isTraceEnabled())
                    logger.trace("HandlerService.checkIfParent - retrieve subordinate with node id: "+rec.getNodeid() +
                    ", txuid: "+rec.getTxuid());

                Transaction subordinate = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                        .setParameter("nodeid", rec.getNodeid()).setParameter("txuid", rec.getTxuid())
                        .getSingleResult();

                if (logger.isTraceEnabled())
                    logger.trace("HandlerService.checkIfParent - retrieve parent with node id: "+nodeid +
                            ", txuid: "+rec.getTxuid());

                Transaction parent = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                        .setParameter("nodeid", this.nodeid).setParameter("txuid", rec.getTxuid())
                        .getSingleResult();

                parent.addSubordinate(subordinate);

                em.flush();

                if (logger.isTraceEnabled())
                    logger.trace("HandlerService.checkIfParent - remove request record: "+rec);

                //em.remove(rec);

                em.getTransaction().commit();

                if (logger.isTraceEnabled()) {
                    logger.trace("Hierarchy detected: "+parent+" is a parent of "+subordinate);
                }
            }
        }
        finally
        {
            if (em.isOpen())
                em.close();
        }
    }

    private Transaction findTransaction(String nodeid, String txuid) {
        try {
            return em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", nodeid).setParameter("txuid", txuid).getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }


    /*
     * These methods provide the logic for handling log lines output by
     * com.arjuna.ats.arjuna.coordinator.BasicAction
     *
     */


    public void associateRequestId(String threadId, Long requestId, String ior) {
        threadReqMap.put(threadId, new CORBAid(requestId, ior, nodeid));
    }

    public void begin(String txuid, Timestamp timestamp, String threadId) {

        em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();
            Transaction tx = new Transaction(txuid, nodeid, timestamp);
            em.persist(tx);
            em.getTransaction().commit();


            if (threadReqMap.containsKey(threadId))
            {
                CORBAid corbaid = threadReqMap.remove(threadId);

                RequestRecord rec = null;
                try
                {
                    rec = em.createNamedQuery("RequestRecord.findByRequestIdAndIOR", RequestRecord.class)
                            .setParameter("requestid", corbaid.getRequestId()).setParameter("ior", corbaid.getIor())
                            .getSingleResult();
                }
                catch (NoResultException e)
                {
                    try
                    {
                        em.getTransaction().begin();
                        if (logger.isTraceEnabled())
                            logger.trace("HandlerService.begin - create request record");
                        em.persist(new RequestRecord(corbaid.getRequestId(), nodeid, corbaid.getIor(), txuid));
                        em.flush();
                        em.getTransaction().commit();
                    }
                    catch (PersistenceException pe)
                    {
                        if (logger.isTraceEnabled())
                            logger.trace("HandlerService.begin - record already exists, retrieve");

                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();

                        // Race condition: Another node has created a record in the meantime, retrieve a fresh copy.
                        try
                        {
                            rec = em.createNamedQuery("RequestRecord.findByRequestIdAndIOR", RequestRecord.class)
                                    .setParameter("requestid", corbaid.getRequestId()).setParameter("ior", corbaid.getIor())
                                    .getSingleResult();
                        }
                        catch (NoResultException nre)
                        {
                            // If we still can't find a record something else caused the PersistenceException!
                            logger.error("Unable to retrieve RequestRecord after PersistenceException", pe);
                        }
                    }
                }

                // rec == null => we just created a new record, therefore we don't have enough information to create
                // the hierarchy yet.
                if (rec != null)
                {
                    em.getTransaction().begin();
                    if (logger.isTraceEnabled())
                        logger.trace("HandlerService.begin - retrieve subordinate");

                    Transaction subordinate = em.merge(tx);

                    if (logger.isTraceEnabled())
                        logger.trace("HandlerService.begin - retrieve parent");
                    Transaction parent = findTransaction(rec.getNodeid(), txuid);

                    subordinate.setParent(parent);
                    em.flush();

                    if (logger.isTraceEnabled())
                        logger.trace("HandlerService.begin - remove record: "+rec);
                    //em.remove(rec);
                    em.getTransaction().commit();

                    if (logger.isTraceEnabled())
                        logger.trace("Hierarchy detected: "+tx+" is a subordinate of "+parent);
                }
            }
        }
        finally
        {
            em.close();
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
        em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            Transaction tx = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", this.nodeid).setParameter("txuid", txuid).getSingleResult();

            tx.setStatus(status, timestamp);
            em.flush();
            em.getTransaction().commit();
        }
        catch (NoResultException e)
        {
            logger.warn("HandlerService.setStatus: Could not retrieve Transaction entity with nodeid=`"+nodeid +
                    "`, txuid=`"+txuid+"`");
        }
        finally
        {
            em.close();
        }
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
        em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            ParticipantRecord rec = em.createNamedQuery("ParticipantRecord.findByUID", ParticipantRecord.class)
                    .setParameter("rmuid", rmuid).getSingleResult();

            rec.setResourceOutcome(Vote.COMMIT, timestamp);

            em.getTransaction().commit();
        }
        catch (NoResultException e)
        {
            logger.warn("HandlerService.resourcePreparedJTS: Could not retrieve ParticipantRecord for rmuid=`"+rmuid+"`");
        }
        finally
        {
            em.close();
        }
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

        em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            ParticipantRecord rec = em.createNamedQuery("ParticipantRecord.findByUID", ParticipantRecord.class)
                    .setParameter("rmuid", rmuid).getSingleResult();

            rec.setResourceOutcome(Vote.ABORT, timestamp);
            rec.setXaException(xaException);

            em.getTransaction().commit();
        }
        catch (NoResultException e)
        {
            logger.warn("HandlerService.resourceFailedToPrepareJTS: Could not retrieve ParticipantRecord for rmuid=`"
                    + rmuid+"`");
        }
        finally
        {
            em.close();
        }
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
            logger.warn("Could not retrieve, exitting after back off");
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

        em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            final Transaction tx = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", this.nodeid).setParameter("txuid", txuid).getSingleResult();

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

            final ParticipantRecord rec = new ParticipantRecord(tx, rm, timestamp);
            rec.setRmuid(rmuid);
            em.persist(rec);
            em.getTransaction().commit();
        }
        catch (NoResultException e)
        {
            logger.warn("HandlerService.enlistResourceManagerByUID: Unable to retrieve Transaction");
        }
        finally
        {
            em.close();
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
        em = emf.createEntityManager();
        try
        {
            Transaction tx = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", this.nodeid).setParameter("txuid", txuid).getSingleResult();

            if (tx != null && tx.getParticipantRecords().size() == 0 && tx.getStatus().equals(Status.IN_FLIGHT)) {
                em.getTransaction().begin();
                em.remove(tx);
                em.getTransaction().commit();
                logger.info("Cleaned up phantom transaction: "+txuid);
            }

        }
        catch (NoResultException e)
        {
            logger.warn("HandlerService.cleanup: Could not retrieve Transaction for nodeid=`"+nodeid+"`, txuid=`"+txuid+"`");
        }
        finally
        {
            em.close();
        }
    }

//    @AroundInvoke
//    public Object intercept(InvocationContext ctx) throws Exception {
//        if (em == null || !em.isOpen())
//            this.em = emf.createEntityManager();
//
//        Object o = null;
//
//        try {
//            o = ctx.proceed();
//        }
//        finally {
//            em.close();
//        }
//        return o;
//    }
}