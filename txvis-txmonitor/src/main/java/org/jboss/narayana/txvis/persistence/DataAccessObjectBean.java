package org.jboss.narayana.txvis.persistence;

import org.jboss.narayana.txvis.logparsing.handlers.AbstractHandler;
import org.jboss.narayana.txvis.persistence.entities.Participant;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 15:57
 */
@Stateless
public class DataAccessObjectBean implements DataAccessObject {

    @PersistenceContext(unitName = "org.jboss.narayana.txvis")
    private EntityManager em;

    @Override
    public Transaction create(String transactionId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");

        Transaction t = new Transaction(transactionId);
        em.persist(t);
        return t;
    }

    @Override
    public Transaction retrieve(String transactionId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");

        final String s = "FROM " + Transaction.class.getSimpleName() + " e WHERE e.transactionId=:transactionId";

        return (Transaction) em.createQuery(s).setParameter("transactionId", transactionId).getSingleResult();
    }

    @Override
    public void delete(String transactionId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");

        em.remove(em.merge(retrieve(transactionId)));
    }

    @Override
    public void deleteAll() {
        for (Transaction t : retrieveAll())
            em.remove(em.merge(t));
    }

    @Override
    public Collection<Transaction> retrieveAll() {
        final String s = "FROM " + Transaction.class.getSimpleName() + " e";

        return em.createQuery(s).getResultList();
    }

    @Override
    public void enlistParticipant(String transactionId, String resourceId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceID");

            Transaction t = retrieve(transactionId);
            t.addParticipant(new Participant(t, resourceId));
            em.merge(t);
    }

    @Override
    public Participant getEnlistedParticipant(String transactionId, String resourceId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceID");

        final String s = "FROM " + Participant.class.getSimpleName()
                + " e WHERE e.transaction.transactionId=:transactionId AND e.resourceId=:resourceId";

            return (Participant) em.createQuery(s).setParameter("transactionId", transactionId)
                    .setParameter("resourceId", resourceId).getSingleResult();
    }

    @Override
    public void setOutcome(String transactionId, Status outcome) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (outcome == null)
            throw new NullPointerException("Null outcome");

            Transaction t = retrieve(transactionId);
            t.setStatus(outcome);
            em.merge(t);
    }

    @Override
    public void setParticipantVote(String transactionId, String resourceId, Vote vote) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceID");
        if (vote == null)
            throw new NullPointerException("Null outcome");

        Participant p = getEnlistedParticipant(transactionId, resourceId);
        p.setVote(vote);
        em.merge(p);
    }

    private boolean validateTxId(String txId) throws NullPointerException {
        return txId.matches(AbstractHandler.TX_ID);
    }

}
