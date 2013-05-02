package org.jboss.narayana.txvis.dataaccess;

import org.jboss.narayana.txvis.logprocessing.handlers.AbstractHandler;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 01/05/2013
 * Time: 13:17
 */
public class TransactionDAOJPAImpl implements TransactionDAO {

    private final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("org.jboss.narayana.txvis");

    private final EntityManager em =
            entityManagerFactory.createEntityManager();

    @Override
    public void create(String transactionID) throws IllegalArgumentException, NullPointerException {
        if (!validateTxId(transactionID))
            throw new IllegalArgumentException("Illegal transactionID");

        em.getTransaction().begin();
        em.persist(new Transaction(transactionID));
        em.getTransaction().commit();
    }

    @Override
    public Transaction get(String transactionID) {
        final String s = "FROM " + Transaction.class.getSimpleName() + " e WHERE e.transactionID=:transactionID";
        Query q = em.createQuery(s);
        q.setParameter("transactionID", transactionID);

        Transaction result = null;
        try {
            result = (Transaction) q.getSingleResult();
        }
        catch (EntityNotFoundException enfe) {
            throw new RuntimeException("Entity not found", enfe);
        }
        catch (NonUniqueResultException nure) {
            throw new IllegalStateException("Multiple Transactions with equal resourceID exist ", nure);
        }
        catch (NoResultException nre) {}

        return result;
    }

    @Override
    public Collection<Transaction> getAll() {
        final String s = "SELECT e FROM "
                + Transaction.class.getSimpleName()
                + " e";
        Query q = em.createQuery(s);

        return q.getResultList();
    }


    @Override
    public void enlistParticipantResource(String transactionID, String resourceID)
            throws IllegalArgumentException, NullPointerException {
        if (!validateTxId(transactionID))
            throw new IllegalArgumentException("Illegal transactionID");
        if (resourceID.trim().isEmpty())
            throw new IllegalArgumentException("Empty resourceID");

        em.getTransaction().begin();
        em.persist(new ParticipantRecord(get(transactionID), DAOFactory.resourceInstance().get(resourceID)));
        em.getTransaction().commit();
    }

    @Override
    public ParticipantRecord getEnlistedParticipantResource(String transactionID, String resourceID)
            throws IllegalArgumentException, NullPointerException {

        final String s = "FROM " + ParticipantRecord.class.getSimpleName()
                + " e WHERE e.transaction.transactionID=:transactionID AND e.resource.resourceID=:resourceID";
        Query q = em.createQuery(s);
        q.setParameter("transactionID", transactionID);
        q.setParameter("resourceID", resourceID);

        return (ParticipantRecord) q.getSingleResult();
    }

    @Override
    public int totalTx() {
        return getAll().size();
    }


    private boolean validateTxId(String txId) throws NullPointerException {
        return txId.matches(AbstractHandler.TX_ID);
    }

    @Override
    public void deconstruct() {
        em.close();
    }
}
