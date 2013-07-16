package org.jboss.narayana.txvis.persistence.dao;

import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.ejb.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
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

    @PersistenceUnit
    private EntityManagerFactory emf;

    public void create(Transaction tx) throws NullPointerException {
        dao.create(tx);
    }

    public Transaction retrieve(Long primaryKeyId) throws NullPointerException {
        return dao.retrieve(Transaction.class, primaryKeyId);
    }

    public Transaction retrieve(String nodeid, String txuid) {
        return dao.querySingle(Transaction.class, "FROM Transaction t WHERE t.jbossNodeid='"+nodeid+"' AND t.txuid='"+txuid+"'");
    }

    public List<Transaction> retrieveAllWithTxUID(String txuid) {
        return dao.queryMultiple(Transaction.class, "FROM Transaction t WHERE t.txuid='"+txuid+"'");
    }

    public List<Transaction> retrieveAll() {
        return dao.queryMultiple(Transaction.class, "FROM Transaction t ORDER BY t.startTime");
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

    public List<Transaction> retrieveAllWithStatus(Status status) {
        return dao.queryMultiple(Transaction.class, "FROM " + Transaction.class.getSimpleName() + " e WHERE status='" + status + "'");
    }

}
