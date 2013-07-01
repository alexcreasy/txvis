package org.jboss.narayana.txvis.persistence.dao;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.interceptors.LoggingInterceptor;

import javax.ejb.*;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 15:57
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors(LoggingInterceptor.class)
public class GenericDAOBean implements GenericDAO {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private EntityManager em;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     *
     * @param entity
     * @param <E>
     */
    @Override
    public <E> void create(E entity) {

        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        em.persist(entity);

        if (notActive)
            em.getTransaction().commit();
    }

    /**
     *
     * @param entityClass
     * @param primaryKey
     * @param <E>
     * @param <K>
     * @return
     */
    @Override
    public <E, K> E retrieve(Class<E> entityClass, K primaryKey) {

        try {
            return em.find(entityClass, primaryKey);
        }
        catch (NoResultException e) {
            return null;
        }
    }

    /**
     *
     * @param entityClass
     * @param <E>
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> retrieveAll(Class<E> entityClass) {

        try {
            return em.createQuery("FROM " + entityClass.getSimpleName() + " e").getResultList();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    /**
     *
     * @param entityClass
     * @param field
     * @param value
     * @param <E>
     * @param <V>
     * @return
     * @throws NonUniqueResultException
     * @throws NoSuchEntityException
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E, V> E retrieveSingleByField(Class<E> entityClass, String field, V value)
            throws NonUniqueResultException, NoSuchEntityException {

        try {
            return (E) em.createQuery("FROM "+entityClass.getSimpleName()+" e WHERE e."+field+"=:value")
                    .setParameter("value", value).getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E, V> List<E> retrieveMultipleByField(Class<E> entityClass, String field, V value) {
        try {
            return em.createQuery("FROM "+entityClass.getSimpleName()+" e WHERE e."+field+"=:value")
                    .setParameter("value", value).getResultList();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    /**
     *
     * @param entity
     * @param <E>
     */
    @Override
    public <E> E update(E entity) {

        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        final E merged = em.merge(entity);

        if (notActive)
            em.getTransaction().commit();

        return merged;
    }

    /**
     *
     * @param entity
     * @param <E>
     */
    @Override
    public <E> void delete(E entity) {

        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        em.remove(em.merge(entity));

        if (notActive)
            em.getTransaction().commit();
    }

    /**
     *
     * @param entityClass
     * @param <E>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E> void deleteAll(Class<E> entityClass) {

        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        for (E e : (Collection<E>) em.createQuery("FROM "+entityClass.getSimpleName()+" e").getResultList())
            em.remove(e);

        if (notActive)
            em.getTransaction().commit();
    }

    /**
     *
     */
    @Override
    public void deleteAll() {
        final boolean notActive = !em.getTransaction().isActive();

        if (notActive)
            em.getTransaction().begin();

        em.createQuery("DELETE FROM Event").executeUpdate();
        em.createQuery("DELETE FROM ParticipantRecord").executeUpdate();
        em.createQuery("DELETE FROM ResourceManager").executeUpdate();
        em.createQuery("DELETE FROM Transaction").executeUpdate();

        if (notActive)
            em.getTransaction().commit();
    }

//    @AroundInvoke
//    public Object intercept(InvocationContext ctx) throws Exception {
//        if (em == null || !em.isOpen())
//            this.em = emf.createEntityManager();
//
//        final boolean notActive = !em.getTransaction().isActive();
//        try {
//            if (notActive)
//                em.getTransaction().begin();
//            Object result = ctx.proceed();
//
//            if (notActive && em.getTransaction().isActive())
//                em.getTransaction().commit();
//
//            return result;
//        }
//        catch (Exception e) {
//            if (em.getTransaction().isActive())
//                em.getTransaction().rollback();
//            throw e;
//        }
//        finally {
//            em.close();
//        }
//    }

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        if (em == null || !em.isOpen())
            this.em = emf.createEntityManager();

        try {
            return ctx.proceed();
        }
        finally {
            em.close();
        }
    }
}
