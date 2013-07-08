package org.jboss.narayana.txvis.persistence;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.Configuration;
import org.jboss.narayana.txvis.interceptors.LoggingInterceptor;
import org.jboss.narayana.txvis.persistence.dao.InterpositionRecordDAO;
import org.jboss.narayana.txvis.persistence.dao.ParticipantRecordDAO;
import org.jboss.narayana.txvis.persistence.dao.ResourceManagerDAO;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.InterpositionRecord;
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
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors(LoggingInterceptor.class)
public class HandlerService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final String nodeid = System.getProperty(Configuration.NODEID_SYS_PROP_NAME);

    private final Map<String, Long> interposThreadMap = new HashMap<>();

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager em;

    @EJB
    private TransactionDAO transactionDAO;

    @EJB
    private ResourceManagerDAO resourceManagerDAO;

    @EJB
    private ParticipantRecordDAO participantRecordDAO;

    @EJB
    private InterpositionRecordDAO interpositionRecordDAO;



    public void receiveInterposition(String threadId, long requestId) {
        interposThreadMap.put(threadId, requestId);
    }

    public void sendInterposition(String nodeid, long requestId) {
//        InterpositionRecord rec = interpositionRecordDAO.retrieve(requestId);
//
//        if (rec != null) {
//            Transaction subordinate = transactionDAO.retrieve(rec.getNodeid(), rec.getTxuid());
//            Transaction parent = transactionDAO.retrieve(nodeId, rec.getTxuid());
//            parent.addSubordinate(subordinate);
//            transactionDAO.update(parent);
//            interpositionRecordDAO.delete(rec);
//
//            if (logger.isTraceEnabled())
//                logger.trace("Hierarchy detected: "+parent+" is a parent of "+subordinate);
//        }
//        else {
//            rec = new InterpositionRecord(nodeid, requestId);
//            interpositionRecordDAO.create(rec);
//        }

        em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            InterpositionRecord rec = null;
            try
            {
                rec = em.createNamedQuery("InterpositionRecord.findByRequestId", InterpositionRecord.class)
                        .setParameter("requestid", requestId).getSingleResult();
            }
            catch (NoResultException e)
            {
                // implicit rec = null
            }

            if (rec != null)
            {
                Transaction subordinate = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                        .setParameter("nodeid", rec.getNodeid()).setParameter("txuid", rec.getTxuid()).getSingleResult();

                Transaction parent = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                        .setParameter("nodeid", nodeid).setParameter("txuid", rec.getTxuid()).getSingleResult();

                parent.addSubordinate(subordinate);
                em.remove(rec);

                if (logger.isTraceEnabled())
                    logger.trace("Hierarchy detected: "+parent+" is a parent of "+subordinate);
            }
            else
                em.persist(new InterpositionRecord(nodeid, requestId));

            em.getTransaction().commit();
        }
        finally
        {
            em.close();
        }
    }


    /*
     * These methods provide the logic for handling log lines output by
     * com.arjuna.ats.arjuna.coordinator.BasicAction
     *
     */
    public void begin(String txuid, Timestamp timestamp, String threadId) {

//        Transaction tx = new Transaction(txuid, nodeid, timestamp);
//
//
//        if (interposThreadMap.containsKey(threadId)) {
//            Long requestId = interposThreadMap.remove(threadId);
//            InterpositionRecord rec = interpositionRecordDAO.retrieve(requestId);
//
//            if (rec == null) {
//                rec = new InterpositionRecord(nodeid, requestId);
//                rec.setTxuid(txuid);
//                interpositionRecordDAO.create(rec);
//            }
//            else {
//                Transaction parent = transactionDAO.retrieve(rec.getNodeid(), txuid);
//                parent.addSubordinate(tx);
//                interpositionRecordDAO.delete(rec);
//
//                if (logger.isTraceEnabled())
//                    logger.trace("Hierarchy detected: "+tx+" is a subordinate of"+parent);
//            }
//        }
//        transactionDAO.update(tx);


        em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            Transaction tx = new Transaction(txuid, nodeid, timestamp);
            em.persist(tx);

            if (interposThreadMap.containsKey(threadId))
            {
                Long requestId = interposThreadMap.remove(threadId);

                InterpositionRecord rec = null;
                try
                {
                    rec = em.createNamedQuery("InterpositionRecord.findByRequestId", InterpositionRecord.class)
                            .setParameter("requestid", requestId).getSingleResult();
                }
                catch (NoResultException e)
                {
                    // Implicit rec = null;
                }

                if (rec == null)
                {
                    rec = new InterpositionRecord(nodeid, requestId);
                    rec.setTxuid(txuid);
                    em.persist(rec);
                }
                else
                {
                    Transaction parent = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                            .setParameter("nodeid", rec.getNodeid()).setParameter("txuid", txuid).getSingleResult();
                    parent.addSubordinate(tx);
                    em.remove(rec);

                    if (logger.isTraceEnabled())
                        logger.trace("Hierarchy detected: "+tx+" is a subordinate of "+parent);
                }
            }

            em.getTransaction().commit();
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

//        Transaction tx = transactionDAO.retrieve(nodeid, txuid);
//
//        if (tx == null) {
//            logger.warn("Could not retrieve, exitting after back off");
//            return;
//        }
//
//        tx.setStatus(Status.PREPARE, timestamp);
//        transactionDAO.update(tx);

        setStatus(txuid, Status.PREPARE, timestamp);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void phase2Commit(String txuid, Timestamp timestamp) {

//        Transaction tx = transactionDAO.retrieve(nodeid, txuid);
//
//        if (tx == null) {
//            logger.warn("Could not retrieve, exitting after back off");
//            return;
//        }
//
//            tx.setStatus(Status.COMMIT, timestamp);
//            transactionDAO.update(tx);
        setStatus(txuid, Status.COMMIT, timestamp);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void onePhaseCommit(String txuid, Timestamp timestamp) {

//        Transaction tx = transactionDAO.retrieve(nodeid, txuid);
//
//        if (tx == null) {
//            logger.warn("Could not retrieve, exitting after back off");
//            return;
//        }
//
//        tx.setStatus(Status.ONE_PHASE_COMMIT, timestamp);
//        transactionDAO.update(tx);

        setStatus(txuid, Status.ONE_PHASE_COMMIT, timestamp);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void abort(String txuid, Timestamp timestamp) {

//        Transaction tx = transactionDAO.retrieve(nodeid, txuid);
//
//        if (tx == null) {
//            logger.warn("Could not retrieve, exitting after back off");
//            return;
//        }
//
//        tx.setStatus(Status.PHASE_ONE_ABORT, timestamp);
//        transactionDAO.update(tx);
        setStatus(txuid, Status.PHASE_ONE_ABORT, timestamp);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void phase2Abort(String txuid, Timestamp timestamp) {

//        Transaction tx = transactionDAO.retrieve(nodeid, txuid);
//
//        if (tx == null) {
//            logger.warn("Could not retrieve, exitting after back off");
//            return;
//        }
//
//        tx.setStatus(Status.PHASE_TWO_ABORT, timestamp);
//        transactionDAO.update(tx);

        setStatus(txuid, Status.PHASE_TWO_ABORT, timestamp);
    }


    private void setStatus(String txuid, Status status, Timestamp timestamp){
        em = emf.createEntityManager();
        try
        {

            Transaction tx = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", this.nodeid).setParameter("txuid", txuid).getSingleResult();

            tx.setStatus(status, timestamp);

            em.getTransaction().begin();
            em.merge(tx);
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

//        ParticipantRecord rec = participantRecordDAO.retrieveByUID(rmuid);
//
//        if (rec  == null) {
//            logger.warn("Could not retrieve, exitting after back off");
//            return;
//        }
//
//        rec.setResourceOutcome(Vote.COMMIT, timestamp);
//        participantRecordDAO.update(rec);
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
//        ParticipantRecord rec = participantRecordDAO.retrieveByUID(rmuid);
//
//        if (rec == null) {
//            logger.warn("Could not retrieve, exitting after back off");
//            return;
//        }
//
//        resourceFailedToPrepare(rec.getTransaction().getTxuid(), rec.getResourceManager().getJndiName(),
//                xaException, timestamp);

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

            em.merge(rec);

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
            em.getTransaction().begin();

            Transaction tx = em.createNamedQuery("Transaction.findByNodeidAndTxuid", Transaction.class)
                    .setParameter("nodeid", this.nodeid).setParameter("txuid", txuid).getSingleResult();

            if (tx != null && tx.getParticipantRecords().size() == 0 && tx.getStatus().equals(Status.IN_FLIGHT)) {
                em.remove(tx);
                logger.info("Cleaned up phantom transaction: "+txuid);
            }
            em.getTransaction().commit();
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