package org.jboss.narayana.txvis.persistence.dao;

import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.persistence.EntityManagerServiceBean;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.ejb.*;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/05/2013
 * Time: 00:41
 */
@Stateless
@DependsOn("EntityManagerServiceBean")
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TransactionDAOImpl {

    @EJB
    DataAccessObject dao;

    @EJB
    EntityManagerServiceBean emf;

    public Transaction retrieveTransactionByTxUID(String TxUID) {
        return dao.retrieveByField(Transaction.class, "transactionId", TxUID);
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> retrieveTransactionsWithStatus(Status status) {
        final String s = "FROM " + Transaction.class.getSimpleName() + " e WHERE status=:status";

        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(s).setParameter("status", status).getResultList();
        } finally {
            em.close();
        }
    }
}
