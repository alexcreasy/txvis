package org.jboss.narayana.txvis.persistence;

import org.jboss.narayana.txvis.persistence.entities.Participant;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import javax.ejb.Local;
import javax.persistence.Entity;
import java.util.Collection;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 07/05/2013
 * Time: 22:35
 */
@Local
public interface DataAccessObject {
    Transaction create(String transactionId);

    Transaction retrieve(String transactionId);

    void delete(String transactionId);

    void deleteAll();

    Collection<Transaction> retrieveAll();

    void enlistParticipant(String transactionId, String resourceId);

    Participant getEnlistedParticipant(String transactionId, String resourceId);

    void setOutcome(String transactionId, Status outcome);

    void setParticipantVote(String transactionId, String resourceId, Vote vote);

    void update(Transaction transaction);

    <E, K> E retrieve(Class<E> entityClass, K primaryKey);

    @SuppressWarnings("unchecked")
    <E> List<E> retrieveAll(Class<E> entityClass);

    @SuppressWarnings("unchecked")
    <E, V> E retrieveByField(Class<E> entityClass, String field, V value);

    <E> void update(E entity);

    <E> void delete(E entity);


    <E> void deleteAll(Class<E> entityClass);

    <E> void create(E entity);

    Transaction retrieveTransactionByTxUID(String TxUID);
}
