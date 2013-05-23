package org.jboss.narayana.txvis.persistence;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 23/05/2013
 * Time: 17:13
 */
public class EntityManagerDecorator implements EntityManager {

    private final EntityManager em;

    public EntityManagerDecorator(EntityManager em) {
        this.em = em;
    }

    @Override
    public void persist(Object o) {
        em.persist(o);
    }

    @Override
    public <T> T merge(T t) {
        return em.merge(t);
    }

    @Override
    public void remove(Object o) {
        em.remove(o);
    }

    @Override
    public <T> T find(Class<T> tClass, Object o) {
        return em.find(tClass, o);
    }

    @Override
    public <T> T find(Class<T> tClass, Object o, Map<String, Object> stringObjectMap) {
        return em.find(tClass, o, stringObjectMap);
    }

    @Override
    public <T> T find(Class<T> tClass, Object o, LockModeType lockModeType) {
        return em.find(tClass, o, lockModeType);
    }

    @Override
    public <T> T find(Class<T> tClass, Object o, LockModeType lockModeType, Map<String, Object> stringObjectMap) {
        return em.find(tClass, o, lockModeType, stringObjectMap);
    }

    @Override
    public <T> T getReference(Class<T> tClass, Object o) {
        return em.getReference(tClass, o);
    }

    @Override
    public void flush() {
        em.flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushModeType) {
        em.setFlushMode(flushModeType);
    }

    @Override
    public FlushModeType getFlushMode() {
        return em.getFlushMode();
    }

    @Override
    public void lock(Object o, LockModeType lockModeType) {
        em.lock(o, lockModeType);
    }

    @Override
    public void lock(Object o, LockModeType lockModeType, Map<String, Object> stringObjectMap) {
        em.lock(o, lockModeType, stringObjectMap);
    }

    @Override
    public void refresh(Object o) {
        em.refresh(o);
    }

    @Override
    public void refresh(Object o, Map<String, Object> stringObjectMap) {
        em.refresh(o, stringObjectMap);
    }

    @Override
    public void refresh(Object o, LockModeType lockModeType) {
        em.refresh(o, lockModeType);
    }

    @Override
    public void refresh(Object o, LockModeType lockModeType, Map<String, Object> stringObjectMap) {
        em.refresh(o, lockModeType, stringObjectMap);
    }

    @Override
    public void clear() {
        em.clear();
    }

    @Override
    public void detach(Object o) {
        em.detach(o);
    }

    @Override
    public boolean contains(Object o) {
        return em.contains(o);
    }

    @Override
    public LockModeType getLockMode(Object o) {
        return em.getLockMode(o);
    }

    @Override
    public void setProperty(String s, Object o) {
        em.setProperty(s, o);
    }

    @Override
    public Map<String, Object> getProperties() {
        return em.getProperties();
    }

    @Override
    public Query createQuery(String s) {
        return em.createQuery(s);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> tCriteriaQuery) {
        return em.createQuery(tCriteriaQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String s, Class<T> tClass) {
        return em.createQuery(s, tClass);
    }

    @Override
    public Query createNamedQuery(String s) {
        return em.createNamedQuery(s);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String s, Class<T> tClass) {
        return em.createNamedQuery(s, tClass);
    }

    @Override
    public Query createNativeQuery(String s) {
        return em.createNativeQuery(s);
    }

    @Override
    public Query createNativeQuery(String s, Class aClass) {
        return em.createNativeQuery(s, aClass);
    }

    @Override
    public Query createNativeQuery(String s, String s2) {
        return em.createNativeQuery(s, s2);
    }

    @Override
    public void joinTransaction() {
        em.joinTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> tClass) {
        return em.unwrap(tClass);
    }

    @Override
    public Object getDelegate() {
        return em.getDelegate();
    }

    @Override
    public void close() {
        em.close();
    }

    @Override
    public boolean isOpen() {
        return em.isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return em.getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return em.getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return em.getMetamodel();
    }
}
