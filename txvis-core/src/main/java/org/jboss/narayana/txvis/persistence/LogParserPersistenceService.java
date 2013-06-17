package org.jboss.narayana.txvis.persistence;

import org.jboss.narayana.txvis.persistence.entities.Event;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 *
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/06/2013
 * Time: 12:20
 */
@Stateless
public class LogParserPersistenceService {

    @EJB
    private DataAccessObject dao;

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void createTx(String txuid, Timestamp timestamp) {
        final Transaction tx = new Transaction(txuid, timestamp);
        dao.create(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void prepareTx(String txuid, Timestamp timestamp) {
        final Transaction tx = dao.retrieveTransactionByTxUID(txuid);
        tx.addEvent(new Event(EventType.PREPARE, "N/A", timestamp));
        dao.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void commitTx(String txuid, Timestamp timestamp) {
        final Transaction tx = dao.retrieveTransactionByTxUID(txuid);
        tx.setStatus(Status.COMMIT, timestamp);
        dao.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void topLevelAbortTx(String txuid, Timestamp timestamp) {
        final Transaction tx = dao.retrieveTransactionByTxUID(txuid);
        tx.setStatus(Status.ROLLBACK_CLIENT, timestamp);
        dao.update(tx);
    }

    /**
     *
     * @param txuid
     * @param timestamp
     */
    public void resourceDrivenAbortTx(String txuid, Timestamp timestamp) {
        final Transaction tx = dao.retrieveTransactionByTxUID(txuid);
        tx.setStatus(Status.ROLLBACK_RESOURCE, timestamp);
        dao.update(tx);
    }

    /**
     *
     * @param txuid
     * @param rmJndiName
     * @param timestamp
     */
    public void resourceVoteCommit(String txuid, String rmJndiName, Timestamp timestamp) {
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
        ResourceManager rm = dao.retrieveResourceManagerByJndiName(rmJndiName);

        if (rm == null)
            rm = new ResourceManager(rmJndiName, rmProductName, rmProductVersion);

        dao.createParticipantRecord(txuid, rm, timestamp);
    }
}