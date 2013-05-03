package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.logprocessing.handlers.AbstractHandler;

import javax.persistence.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 15:57
 */
public class DataAccessObject {

    public void create(String transactionId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        EntityManager em = DAOFactory.getEntityManager();
        try {
            try {
                em.getTransaction().begin();
                em.persist(new Transaction(transactionId));
                em.getTransaction().commit();
            }
            catch (Exception e) {
                em.getTransaction().rollback();
                throw new RuntimeException("Unable to create transaction", e);
            }
        }
        finally {
            em.close();
        }
    }

    public Transaction retrieve(String transactionId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");

        EntityManager em = DAOFactory.getEntityManager();

        Transaction result = null;
        try {
            final String s = "FROM " + Transaction.class.getSimpleName() + " e WHERE e.transactionId=:transactionId";
            result = (Transaction) em.createQuery(s).setParameter("transactionId", transactionId).getSingleResult();
        }
        finally {
            em.close();
        }
        return result;
    }

    public void enlistParticipant(String transactionId, String resourceId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceID");

        EntityManager em = DAOFactory.getEntityManager();

        try {
            try {
                em.getTransaction().begin();
                Transaction t = retrieve(transactionId);
                t.addParticipant(new Participant(t, resourceId));
                em.merge(t);
                em.getTransaction().commit();
            }
            catch (Exception e) {
                em.getTransaction().rollback();
                throw new RuntimeException("Unable to enlist participant", e);
            }
        }
        finally {
            em.close();
        }
    }

    public Participant getEnlistedParticipant(String transactionId, String resourceId) {
        if (!validateTxId(transactionId))
            throw new IllegalArgumentException("Illegal transactionID");
        if (resourceId.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceID");

        final String s = "FROM " + Participant.class.getSimpleName()
                + " e WHERE e.transaction.transactionId=:transactionId AND e.resourceId=:resourceId";

        EntityManager em = DAOFactory.getEntityManager();

        try {
            return (Participant) em.createQuery(s).setParameter("transactionId", transactionId)
                    .setParameter("resourceId", resourceId).getSingleResult();
        }
        finally {
            em.close();
        }
    }

    private boolean validateTxId(String txId) throws NullPointerException {
        return txId.matches(AbstractHandler.TX_ID);
    }

}
