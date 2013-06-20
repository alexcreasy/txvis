package org.jboss.narayana.txvis.persistence;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.dao.GenericDAO;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.ejb.*;
import java.sql.Timestamp;
import java.text.MessageFormat;

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
public class HandlerService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB
    private GenericDAO dao;

    @EJB
    TransactionDAO transactionDAO;

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void createTx(String txuid, Timestamp timestamp) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.createTx(), txuid=`{0}`, timestamp=`{1}`", txuid, timestamp));

        final Transaction tx = new Transaction(txuid, timestamp);
        transactionDAO.create(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void prepareTx(String txuid, Timestamp timestamp) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.prepareTx(), txuid=`{0}`, timestamp=`{1}`", txuid, timestamp));

        final Transaction tx = transactionDAO.retrieve(txuid);
        tx.prepare(timestamp);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void commitTx2Phase(String txuid, Timestamp timestamp) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.commitTx2Phase(), txuid=`{0}`, timestamp=`{1}`",
                    txuid, timestamp));

        final Transaction tx = transactionDAO.retrieve(txuid);
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
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.commitTx1Phase(), txuid=`{0}`, timestamp=`{1}`",
                    txuid, timestamp));

        final Transaction t = transactionDAO.retrieve(txuid);
        t.setStatus(Status.COMMIT, timestamp);
        t.setOnePhase(true);
        transactionDAO.update(t);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void topLevelAbortTx(String txuid, Timestamp timestamp) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.topLevelAbortTx(), txuid=`{0}`, timestamp=`{1}`",
                    txuid, timestamp));

        final Transaction tx = transactionDAO.retrieve(txuid);
        tx.setStatus(Status.ROLLBACK_CLIENT, timestamp);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void resourceDrivenAbortTx(String txuid, Timestamp timestamp) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.resourceDrivenAbortTx(), txuid=`{0}`, timestamp=`{1}`",
                    txuid, timestamp));

        final Transaction tx = transactionDAO.retrieve(txuid);
        tx.setStatus(Status.ROLLBACK_RESOURCE, timestamp);
        transactionDAO.update(tx);
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param timestamp
     */
    public void resourceVoteCommit(String txuid, String rmJndiName, Timestamp timestamp) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.resourceVoteCommit(), txuid=`{0}`, timestamp=`{1}`",
                    txuid, timestamp));

        final ParticipantRecord rec = dao.retrieveParticipantRecord(txuid, rmJndiName);
        rec.setVote(Vote.COMMIT);
        dao.update(rec);
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param timestamp
     */
    public void resourceVoteAbort(String txuid, String rmJndiName, Timestamp timestamp) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.resourceVoteAbort(), txuid=`{0}`, timestamp=`{1}`",
                    txuid, timestamp));

        final ParticipantRecord rec = dao.retrieveParticipantRecord(txuid, rmJndiName);
        rec.setVote(Vote.ABORT);
        dao.update(rec);
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param rmProductName
     * @param rmProductVersion
     * @param timestamp
     */
    public void enlistResourceManager(String txuid, String rmJndiName, String rmProductName,
                                      String rmProductVersion, Timestamp timestamp) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("HandlerService.enlistResourceManager(), txuid=`{0}`, timestamp=`{1}`, " +
                    "rmJndiName=`{2}`, rmProductName=`{3}`, rmProductVersion=`{4}`",
                    txuid, timestamp, rmJndiName, rmProductName, rmProductVersion));

        ResourceManager rm = dao.retrieveResourceManagerByJndiName(rmJndiName);
        if (rm == null)
            rm = new ResourceManager(rmJndiName, rmProductName, rmProductVersion);
        dao.createParticipantRecord(txuid, rm, timestamp);
    }
}