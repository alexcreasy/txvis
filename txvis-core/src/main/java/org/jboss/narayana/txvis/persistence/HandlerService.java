package org.jboss.narayana.txvis.persistence;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.interceptors.LoggingInterceptor;
import org.jboss.narayana.txvis.interceptors.TransactionInterceptor;
import org.jboss.narayana.txvis.persistence.dao.ParticipantRecordDAO;
import org.jboss.narayana.txvis.persistence.dao.ResourceManagerDAO;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
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
@Interceptors({LoggingInterceptor.class})
public class HandlerService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @PersistenceUnit
    private EntityManagerFactory emf;

    @EJB
    private TransactionDAO transactionDAO;

    @EJB
    private ResourceManagerDAO resourceManagerDAO;

    @EJB
    private ParticipantRecordDAO participantRecordDAO;

    private Map<String, String> threadToTxMap = new HashMap<>();
    private Map<String, Long> threadToRecMap = new HashMap<>();

    /**
     *
     * @param txuid
     * @param timestamp
     * @param threadId
     */
    public void beginTx(String txuid, Timestamp timestamp, String threadId) {
        beginTx(txuid, timestamp);
        threadToTxMap.put(threadId, txuid);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void beginTx(String txuid, Timestamp timestamp) {

        final String nodeid = arjPropertyManager.getCoreEnvironmentBean().getNodeIdentifier();

        if (logger.isTraceEnabled())
            logger.trace("beginTx called from node: "+nodeid);

        final EntityManager em = emf.createEntityManager();

        try
        {
            em.getTransaction().begin();

            Transaction tx = transactionDAO.retrieve(txuid);
            if (tx == null) {
                if (logger.isTraceEnabled())
                    logger.trace("beginTx called new parent transaction, nodeid="+nodeid);

                // txuid has not been seen before by log parser -> create tx record.
                tx = new Transaction(txuid, timestamp);
                tx.setNodeId(arjPropertyManager.getCoreEnvironmentBean().getNodeIdentifier());
                transactionDAO.create(tx);
            } else {
                if (logger.isTraceEnabled())
                    logger.trace("beginTx called new subordinate transaction, nodeid="+nodeid);

                // If transaction has already been created we have a JTS transaction, if it originates from,
                // the same node it is a local transaction, from a different node and we have a distributed transaction
                if (!nodeid.equals(tx.getNodeId())) {
                    tx.setDistributed(true);
                    transactionDAO.update(tx);
                }
            }
            em.getTransaction().commit();
        }
        catch(Exception e)
        {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();

            logger.warn("BeginTx: Transaction Rolled Back");
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
    public void prepareTx(String txuid, Timestamp timestamp) {

        final Transaction tx = transactionDAO.retrieve(txuid);

        if (tx == null)
            throw new IllegalStateException("HandlerService.prepareTx(), Transaction not found: " + txuid);

        tx.prepare(timestamp);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void commitTx2Phase(String txuid, Timestamp timestamp) {

        final Transaction tx = transactionDAO.retrieve(txuid);

        if (tx == null)
            throw new IllegalStateException("HandlerService.commitTx2Phase(), Transaction not found: " + txuid);

        if (tx.getStatus().equals(Status.IN_FLIGHT)) {
            tx.setStatus(Status.COMMIT, timestamp);
            transactionDAO.update(tx);
        }
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void commitTx1Phase(String txuid, Timestamp timestamp) {

        final Transaction tx = transactionDAO.retrieve(txuid);

        if (tx  == null)
            throw new IllegalStateException("HandlerService.commitTx1Phase(), Transaction not found: " + txuid);

        tx.setStatus(Status.COMMIT, timestamp);
        tx.setOnePhase(true);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void topLevelAbortTx(String txuid, Timestamp timestamp) {

        final Transaction tx = transactionDAO.retrieve(txuid);

        if (tx  == null)
            throw new IllegalStateException("HandlerService.topLevelAbortTx(), Transaction not found: " + txuid);


        tx.setStatus(Status.ROLLBACK_CLIENT, timestamp);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void resourceDrivenAbortTx(String txuid, Timestamp timestamp) {

        final Transaction tx = transactionDAO.retrieve(txuid);

        if (tx == null)
            throw new IllegalStateException("HandlerService.resourceDrivenAbortTx(), Transaction not found: " + txuid);

        tx.setStatus(Status.ROLLBACK_RESOURCE, timestamp);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param branchId
     * @param timestamp
     */
    public void resourcePreparedJTS(String branchId, Timestamp timestamp) {
        final ParticipantRecord rec = participantRecordDAO.retrieveByBranchId(branchId);
        rec.setVote(Vote.COMMIT);
        participantRecordDAO.update(rec);
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

        rec.setVote(Vote.COMMIT);
        participantRecordDAO.update(rec);
    }


    public void resourceFailedToPrepare(String branchId, String xaException, Timestamp timestamp) {
        final ParticipantRecord rec = participantRecordDAO.retrieveByBranchId(branchId);
        resourceFailedToPrepare(rec.getTransaction().getTxuid(), rec.getResourceManager().getJndiName(),
                xaException, timestamp);
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param timestamp
     */
    public void resourceFailedToPrepare(String txuid, String rmJndiName, String xaExceptionType, Timestamp timestamp) {

        final ParticipantRecord rec = participantRecordDAO.retrieve(txuid, rmJndiName);

        if (rec == null)
            throw new IllegalStateException("Unable to retrieve ParticipantRecord for tx: "+txuid+", rm: "+rmJndiName);

        rec.setVote(Vote.ABORT);
        rec.setXaException(xaExceptionType);
        participantRecordDAO.update(rec);
    }


    public void cleanup(String txuid, Timestamp timestamp) {

    }

    /**
     *
     * @param threadId
     * @param rmJndiName
     * @param rmProductName
     * @param rmProductVersion
     * @param timestamp
     */
    public void enlistResourceManagerByThreadID(String threadId, String rmJndiName, String rmProductName,
                                      String rmProductVersion, Timestamp timestamp) {

        threadToRecMap.put(threadId, enlistResourceManager(threadToTxMap.get(threadId), rmJndiName, rmProductName,
                rmProductVersion, timestamp).getId());
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



        final EntityManager em = emf.createEntityManager();
        try
        {

            em.getTransaction().begin();


            ParticipantRecord rec = participantRecordDAO.retrieve(txuid, rmJndiName);

            if (rec != null) {
                // If the RM has already been enlisted in this transaction before, increment the counter
                rec.incrementNoTimesEnlisted();
            }
            else {
                ResourceManager rm = resourceManagerDAO.retrieve(rmJndiName);
                if (rm == null) {
                    // Create the RM is it hasn't been enlisted before.
                    rm = new ResourceManager(rmJndiName, rmProductName, rmProductVersion);
                    resourceManagerDAO.create(rm);
                }

                // Enlist the RM as a Participant of this transaction
                rec = new ParticipantRecord(transactionDAO.retrieve(txuid), rm, timestamp);
            }

            rec = participantRecordDAO.update(rec);

            em.getTransaction().commit();

            return rec;

        }
        catch (Throwable t)
        {
            em.getTransaction().rollback();
            throw t;
        }
        finally
        {
            em.close();
        }
    }

    /**
     *
     * @param threadId
     * @param branchId
     */
    public void registerBranchId(String threadId, String branchId) {

        final ParticipantRecord rec = participantRecordDAO.retrieve(threadToRecMap.remove(threadId));

        rec.setBranchid(branchId);
        participantRecordDAO.update(rec);
    }

    /**
     *
     * @param threadId
     * @param txuid
     */
    public void resumeTransaction(String threadId, String txuid) {
        threadToTxMap.put(threadId, txuid);
    }


}