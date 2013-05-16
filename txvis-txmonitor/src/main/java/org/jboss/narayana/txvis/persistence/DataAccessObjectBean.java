package org.jboss.narayana.txvis.persistence;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.logparsing.handlers.AbstractHandler;
import org.jboss.narayana.txvis.persistence.entities.Participant;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.persistence.*;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 15:57
 */
@Stateless
@DependsOn("EMFBean")
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DataAccessObjectBean implements DataAccessObject {

    @EJB
    private EMFBean emf;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public Transaction create(String transactionId) {
        if (logger.isTraceEnabled())
            logger.trace(this.getClass().getSimpleName() + ".create, transactionId=" + transactionId);

        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");

        final Transaction t = new Transaction(transactionId);
        final EntityManager em = emf.createEntityManager();
        try {
            final boolean notActive = !em.getTransaction().isActive();

            if (notActive)
                em.getTransaction().begin();
            try {
                em.persist(t);

                if (notActive)
                    em.getTransaction().commit();
            } catch (Throwable throwable) {

                if (notActive)
                    em.getTransaction().rollback();
                else
                    em.getTransaction().setRollbackOnly();

                logger.warn("An error occured while attempting to persist Transaction record: "
                        + transactionId, throwable);
            }
        } finally {
            em.close();
        }
        return t;
    }

    @Override
    public Transaction retrieve(String transactionId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");

        final String query = "FROM " + Transaction.class.getSimpleName() +
                " e WHERE e.transactionId=:transactionId";

        final EntityManager em = emf.createEntityManager();
        try {
            return (Transaction) em.createQuery(query).setParameter("transactionId",
                    transactionId).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(String transactionId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");

        final EntityManager em = emf.createEntityManager();
        try {
            final boolean notActive = !em.getTransaction().isActive();
            if (notActive)
                em.getTransaction().begin();
            try {
                em.remove(em.merge(retrieve(transactionId)));

                if (notActive)
                    em.getTransaction().commit();
            } catch (Throwable throwable) {

                if (notActive)
                    em.getTransaction().rollback();
                else
                    em.getTransaction().setRollbackOnly();

                logger.warn("An error occured while attempting to delete transaction record: "
                        + transactionId , throwable);
            }
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteAll() {
        final EntityManager em = emf.createEntityManager();
        try {
            final boolean notActive = !em.getTransaction().isActive();

            if (notActive)
                em.getTransaction().begin();
            try {
                for (Transaction t : retrieveAll())
                    em.remove(em.merge(t));

                if (notActive)
                    em.getTransaction().commit();
            } catch (Throwable throwable) {
                if (notActive)
                    em.getTransaction().rollback();
                else
                    em.getTransaction().setRollbackOnly();
                logger.warn("An error occured while attempting to delete all transaction record: ", throwable);
            }
        } finally {
            em.close();
        }
    }

    @Override
    public Collection<Transaction> retrieveAll() {
        final String s = "FROM " + Transaction.class.getSimpleName() + " e";

        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(s).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void enlistParticipant(String transactionId, String resourceId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionId");
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceId");

        final EntityManager em = emf.createEntityManager();
        try {
            final boolean notActive = !em.getTransaction().isActive();
            final Transaction t = retrieve(transactionId);
            t.addParticipant(new Participant(t, resourceId));

            if (notActive)
                em.getTransaction().begin();
            try {
                em.merge(t);

                if (notActive)
                    em.getTransaction().commit();

            } catch (Throwable throwable) {
                if (notActive)
                    em.getTransaction().rollback();
                else
                    em.getTransaction().setRollbackOnly();

                logger.warn("An error occured while attempting to enlist resource: "
                        + resourceId + " as participant in transaction: " + transactionId, throwable);
            }
        } finally {
            em.close();
        }
    }

    @Override
    public Participant getEnlistedParticipant(String transactionId, String resourceId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceID");

        final String s = "FROM " + Participant.class.getSimpleName()
                + " e WHERE e.transaction.transactionId=:transactionId AND e.resourceId=:resourceId";

        final EntityManager em = emf.createEntityManager();
        try {
            return (Participant) em.createQuery(s).setParameter("transactionId", transactionId)
                    .setParameter("resourceId", resourceId).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void setOutcome(String transactionId, Status outcome) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (outcome == null)
            throw new NullPointerException("Null outcome");

        final EntityManager em = emf.createEntityManager();
        try {
            final boolean notActive = !em.getTransaction().isActive();

            final Transaction t = retrieve(transactionId);
            t.setStatus(outcome);

            if (notActive)
                em.getTransaction().begin();
            try {
                em.merge(t);

                if (notActive)
                    em.getTransaction().commit();
            } catch (Throwable throwable) {
                if (notActive)
                    em.getTransaction().rollback();
                else
                    em.getTransaction().setRollbackOnly();

                logger.warn("An error occured while attempting to set transaction: "
                        + transactionId + " outcome to " + outcome, throwable);
            }
        } finally {
            em.close();
        }
    }

    @Override
    public void setParticipantVote(String transactionId, String resourceId, Vote vote) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceID");
        if (vote == null)
            throw new NullPointerException("Null outcome");

        final EntityManager em = emf.createEntityManager();
        try {
            final boolean notActive = !em.getTransaction().isActive();

            final Participant p = getEnlistedParticipant(transactionId, resourceId);
            p.setVote(vote);

            if (notActive)
                em.getTransaction().begin();
            try {
                em.merge(p);

                if (notActive)
                    em.getTransaction().commit();
            } catch (Throwable throwable) {
                if (notActive)
                    em.getTransaction().rollback();
                else
                    em.getTransaction().setRollbackOnly();

                logger.warn("An error occured while attempting to set participant: "
                        + resourceId + " in transaction: " + transactionId + " vote to: " + vote, throwable);
            }
        } finally {
            em.close();
        }
    }

    private boolean validateTxId(String txId) throws NullPointerException {
        return txId.matches(AbstractHandler.TX_ID);
    }

}
