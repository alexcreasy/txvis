package org.jboss.narayana.txvis.persistence.dao;

import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.ejb.Local;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 07/05/2013
 * Time: 22:35
 */
@Local
public interface GenericDAO extends Serializable {

    <E> void create(E entity);

    <E, K> E retrieve(Class<E> entityClass, K primaryKey);

    @SuppressWarnings("unchecked")
    <E> List<E> retrieveAll(Class<E> entityClass);

    @SuppressWarnings("unchecked")
    <E, V> E retrieveSingleByField(Class<E> entityClass, String field, V value);

    <E, V> List<E> retrieveMultipleByField(Class<E> entityClass, String field, V value);

    <E> E update(E entity);

    <E> void delete(E entity);

    <E> void deleteAll(Class<E> entityClass);

    void deleteAll();
}
