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
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DataAccessObjectBean implements DataAccessObject {

    private static final Logger logger = Logger.getLogger("org.jboss.narayana.txvis");

    private EntityManagerFactory emf;

    @PostConstruct
    @PostActivate
    private void setup() {
        emf = Persistence.createEntityManagerFactory("org.jboss.narayana.txvis");
    }

    @PreDestroy
    @PrePassivate
    private void tearDown() {
        emf.close();
    }


    @Override
    public Transaction create(String transactionId) {
        if (logger.isTraceEnabled())
            logger.trace(this.getClass().getSimpleName() + ".create, transactionId=" + transactionId);

        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");

        EntityManager em = null;
        Transaction t = null;
        try {
            em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                t = new Transaction(transactionId);
                em.persist(t);

                em.getTransaction().commit();
            } catch (Throwable throwable) {
                em.getTransaction().rollback();
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

        final String s = "FROM " + Transaction.class.getSimpleName() + " e WHERE e.transactionId=:transactionId";

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return (Transaction) em.createQuery(s).setParameter("transactionId", transactionId).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(String transactionId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                em.remove(em.merge(retrieve(transactionId)));

                em.getTransaction().commit();
            } catch (Throwable throwable) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteAll() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                for (Transaction t : retrieveAll())
                    em.remove(em.merge(t));

                em.getTransaction().commit();
            } catch (Throwable throwable) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }

    @Override
    public Collection<Transaction> retrieveAll() {
        final String s = "FROM " + Transaction.class.getSimpleName() + " e";

        EntityManager em = null;
        try {
            return em.createQuery(s).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void enlistParticipant(String transactionId, String resourceId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceID");

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                Transaction t = retrieve(transactionId);
                t.addParticipant(new Participant(t, resourceId));
                em.merge(t);

                em.getTransaction().commit();
            } catch (Throwable throwable) {
                em.getTransaction().rollback();
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

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
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

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                Transaction t = retrieve(transactionId);
                t.setStatus(outcome);
                em.merge(t);

                em.getTransaction().commit();
            } catch (Throwable throwable) {
                em.getTransaction().rollback();
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

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            try {
                em.getTransaction().begin();

                Participant p = getEnlistedParticipant(transactionId, resourceId);
                p.setVote(vote);
                em.merge(p);

                em.getTransaction().commit();
            } catch (Throwable throwable) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }

    private boolean validateTxId(String txId) throws NullPointerException {
        return txId.matches(AbstractHandler.TX_ID);
    }

}
