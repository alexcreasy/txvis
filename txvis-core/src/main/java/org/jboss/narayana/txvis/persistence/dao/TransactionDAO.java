package org.jboss.narayana.txvis.persistence.dao;

import org.jboss.narayana.txvis.logparsing.handlers.AbstractHandler;
import org.jboss.narayana.txvis.persistence.EntityManagerServiceBean;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.ejb.*;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 20/06/2013
 * Time: 13:14
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TransactionDAO implements Serializable{

    @EJB
    private GenericDAO dao;

    @EJB
    private EntityManagerServiceBean emf;

    public void create(Transaction tx) throws NullPointerException {
        dao.create(tx);
    }

    public Transaction retrieve(String txuid) throws NullPointerException {
        return dao.retrieve(Transaction.class, txuid);
    }

    public List<Transaction> retrieveAll() {
        return dao.retrieveAll(Transaction.class);
    }

    public void update(Transaction tx) throws NullPointerException {
        dao.update(tx);
    }

    public void delete(Transaction tx) throws NullPointerException {
        dao.delete(tx);
    }

    public void deleteAll() {
        dao.deleteAll(Transaction.class);
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> retrieveAllWithStatus(Status status) {
        final EntityManager em = emf.createEntityManager();
        try {

            return em.createQuery("FROM "+Transaction.class.getSimpleName()+" e WHERE status=:status")
                    .setParameter("status", status).getResultList();

        } finally {
            em.close();
        }
    }

    private boolean validateTxId(String txId) throws NullPointerException {
        return txId.matches(AbstractHandler.PATTERN_TXID);
    }


}
