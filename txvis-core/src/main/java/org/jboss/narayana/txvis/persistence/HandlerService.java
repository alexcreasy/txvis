package org.jboss.narayana.txvis.persistence;

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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import java.sql.Timestamp;
import java.util.Random;

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

    private static final int MAX_RETRIES = 5;
    private static final int BACKOFF_MULTIPLIER = 15;
    private static final int RAND_FACTOR = 250;

    private final Random rand = new Random();

    @PersistenceUnit
    private EntityManagerFactory emf;

    @EJB
    private TransactionDAO transactionDAO;

    @EJB
    private ResourceManagerDAO resourceManagerDAO;

    @EJB
    private ParticipantRecordDAO participantRecordDAO;

    /**
     *
     * @param txuid
     * @param timestamp
     */
    @Interceptors(TransactionInterceptor.class)
    public void beginTx(String txuid, Timestamp timestamp) {

        final String nodeid = System.getProperty("jboss.node.name");
        //arjPropertyManager.getCoreEnvironmentBean().getNodeIdentifier();

        Transaction tx = transactionDAO.retrieve(txuid);
        if (tx == null) {

            // txuid has not been seen before by log parser -> create tx record.
            tx = new Transaction(txuid, timestamp);
            tx.setNodeId(nodeid);
            try {
                transactionDAO.create(tx);
            }
            catch (PersistenceException e) {
                //FIXME hack to stop PK constraint violation
                if (!nodeid.equals(tx.getNodeId())) {
                    tx.setDistributed(true);
                    transactionDAO.update(tx);
                }
            }
        }
        else {

            // If transaction has already been created we have a JTS transaction, if it originates from,
            // the same node it is a local transaction, from a different node and we have a distributed transaction
            if (!nodeid.equals(tx.getNodeId())) {
                tx.setDistributed(true);
                transactionDAO.update(tx);
            }
        }
    }

    public void isDistributed(String txuid) {
        Transaction tx = transactionDAO.retrieve(txuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (tx == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tx = transactionDAO.retrieve(txuid);
            tries++;
        }

        if (tx == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }
        tx.setDistributed(true);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void prepareTx(String txuid, Timestamp timestamp) {

        Transaction tx = transactionDAO.retrieve(txuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (tx == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tx = transactionDAO.retrieve(txuid);
            tries++;
        }

        if (tx == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }

        tx.prepare(timestamp);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void commitTx2Phase(String txuid, Timestamp timestamp) {

        Transaction tx = transactionDAO.retrieve(txuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (tx == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tx = transactionDAO.retrieve(txuid);
            tries++;
        }

        if (tx == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }

        if (tx.getStatus().equals(Status.IN_FLIGHT))
            tx.setStatus(Status.COMMIT, timestamp);
            transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void commitTx1Phase(String txuid, Timestamp timestamp) {

        Transaction tx = transactionDAO.retrieve(txuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (tx == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tx = transactionDAO.retrieve(txuid);
            tries++;
        }

        if (tx == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }

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

        Transaction tx = transactionDAO.retrieve(txuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (tx == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tx = transactionDAO.retrieve(txuid);
            tries++;
        }

        if (tx == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }


        tx.setStatus(Status.ROLLBACK_CLIENT, timestamp);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void resourceDrivenAbortTx(String txuid, Timestamp timestamp) {

        Transaction tx = transactionDAO.retrieve(txuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (tx == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tx = transactionDAO.retrieve(txuid);
            tries++;
        }

        if (tx == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }

        tx.setStatus(Status.ROLLBACK_RESOURCE, timestamp);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param rmuid
     * @param timestamp
     */
    @Interceptors(TransactionInterceptor.class)
    public void resourcePreparedJTS(String rmuid, Timestamp timestamp) {
        ParticipantRecord rec = participantRecordDAO.retrieveByUID(rmuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (rec == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            rec = participantRecordDAO.retrieveByUID(rmuid);
            tries++;
        }

        if (rec  == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }

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

    @Interceptors(TransactionInterceptor.class)
    public void resourceFailedToPrepareJTS(String rmuid, String xaException, Timestamp timestamp) {
        ParticipantRecord rec = participantRecordDAO.retrieveByUID(rmuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (rec == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            rec = participantRecordDAO.retrieveByUID(rmuid);
            tries++;
        }

        if (rec == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }

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

        if (rec == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }

        rec.setVote(Vote.ABORT);
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
    @Interceptors(TransactionInterceptor.class)
    public void enlistResourceManagerByUID(String txuid, String rmuid, String rmJndiName, String rmProductName,
                                           String rmProductVersion, Timestamp timestamp) {
        Transaction tx =  transactionDAO.retrieve(txuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (tx == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tx = transactionDAO.retrieve(txuid);
            tries++;
        }

        if (tx == null) {
            logger.warn("Could not retrieve, exitting after back off");
            return;
        }

        final ParticipantRecord rec = enlistResourceManager(txuid, rmJndiName, rmProductName, rmProductVersion, timestamp);
        rec.setRmuid(rmuid);
        participantRecordDAO.update(rec);
    }

    @Interceptors(TransactionInterceptor.class)
    public void cleanup(String txuid) {
        Transaction tx =  transactionDAO.retrieve(txuid);

        //FIXME back off logic needs to be replaced by shadow tabling
        int tries = 0;
        while (tx == null && tries < MAX_RETRIES) {
            try {
                Thread.sleep(rand.nextInt(RAND_FACTOR) + BACKOFF_MULTIPLIER  * tries);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tx = transactionDAO.retrieve(txuid);
            tries++;
        }


        if (tx != null && tx.getParticipantRecords().size() == 0 && tx.getStatus().equals(Status.IN_FLIGHT)) {
            transactionDAO.delete(tx);
            logger.info("Cleaned up phantom transaction: "+txuid);
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
            rec = new ParticipantRecord(transactionDAO.retrieve(txuid), rm, timestamp);
        }

        rec = participantRecordDAO.update(rec);
        return rec;
    }
}