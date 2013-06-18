package org.jboss.narayana.txvis.persistence;

import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.ejb.Local;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 07/05/2013
 * Time: 22:35
 */
@Local
public interface DataAccessObject {

    <E> void create(E entity);

    <E, K> E retrieve(Class<E> entityClass, K primaryKey);

    @SuppressWarnings("unchecked")
    <E> List<E> retrieveAll(Class<E> entityClass);

    @SuppressWarnings("unchecked")
    <E, V> E retrieveByField(Class<E> entityClass, String field, V value);

    <E> void update(E entity);

    <E> void delete(E entity);

    <E> void deleteAll(Class<E> entityClass);

    Transaction retrieveTransactionByTxUID(String TxUID);

    @SuppressWarnings("unchecked")
    List<Transaction> retrieveTransactionsWithStatus(Status status);

    ResourceManager retrieveResourceManagerByJndiName(String jndiName);

    void createParticipantRecord(Transaction tx, ResourceManager rm, Timestamp timestamp);

    void createParticipantRecord(String transactionXID, ResourceManager rm, Timestamp timestamp);

    ParticipantRecord retrieveParticipantRecord(String txUID, String rmJndiName);

    void deleteAll();
}
