package org.jboss.narayana.txvis.persistence;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.logparsing.handlers.AbstractHandler;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;

import javax.ejb.*;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 15:57
 */
@Stateless
@DependsOn("EntityManagerServiceBean")
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DataAccessObjectBean implements DataAccessObject, Serializable {

    @EJB
    private EntityManagerServiceBean emf;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     *
     * @param entity
     * @param <E>
     */
    @Override
    public <E> void create(E entity) {

        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.create() entity=`{0}`", entity));

        final EntityManager em = emf.createEntityManager();
        final EntityTransaction etx = em.getTransaction();
        try {
            etx.begin();

            em.persist(entity);

            etx.commit();
        } catch (RuntimeException e) {
            if (etx != null && etx.isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
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

        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.retrieve() entityClass=`{0}`, primaryKey=`{1}`",
                    entityClass, primaryKey));

        final EntityManager em = emf.createEntityManager();
        try {
            return em.find(entityClass, primaryKey);
        } catch (NoResultException e) {

            logger.warn(MessageFormat.format(
                    "DataAccessObjectBean.retrieve: No result found for search: class=`{0}`, primaryKey=`{1}`",
                    entityClass, primaryKey));

            return null;
        } finally {
            em.close();
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
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.retrieveAll() entityClass=`{0}`", entityClass));

        final EntityManager em = emf.createEntityManager();
        try {

            return em.createQuery("FROM " + entityClass.getSimpleName() + " e").getResultList();

        } catch (NoResultException e) {

            if (logger.isTraceEnabled())
                logger.trace(MessageFormat.format("DataAccessObjectBean.retrieveAll: No result found for search: class=`{0}`",
                        entityClass));
            return null;
        } finally {
            em.close();
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
    public <E, V> E retrieveByField(Class<E> entityClass, String field, V value)
            throws NonUniqueResultException, NoSuchEntityException {

        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format(
                    "DataAccessObjectBean.retrieveByField() entityClass=`{0}`, field=`{1}`, value=`{2}`",
                    entityClass, field, value));

        final EntityManager em = emf.createEntityManager();
        try {

            return (E) em.createQuery("FROM "+entityClass.getSimpleName()+" e WHERE e."+field+"=:value")
                    .setParameter("value", value).getSingleResult();

        } catch (NoResultException e) {

            if (logger.isTraceEnabled())
                logger.trace(MessageFormat.format(
                        "DataAccessObjectBean.retrieveByField: No result found for search: " +
                                "class=`{0}`, field=`{1}`, value=`{2}`", entityClass, field, value));
            return null;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param entity
     * @param <E>
     */
    @Override
    public <E> void update(E entity) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.update() entity=`{0}`", entity));

        final EntityManager em = emf.createEntityManager();
        final EntityTransaction etx = em.getTransaction();
        try {
            etx.begin();

            em.merge(entity);

            etx.commit();
        } catch (RuntimeException e) {
            if (etx != null && etx.isActive())
                etx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param entity
     * @param <E>
     */
    @Override
    public <E> void delete(E entity) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.delete() entity=`{0}`", entity));

        final EntityManager em = emf.createEntityManager();
        final EntityTransaction etx = em.getTransaction();
        try {
            etx.begin();

            em.remove(em.merge(entity));

            etx.commit();
        } catch (RuntimeException e) {
            if (etx != null && etx.isActive())
                etx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param entityClass
     * @param <E>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E> void deleteAll(Class<E> entityClass) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.deleteAll() entityClass=`{0}`", entityClass));

        final EntityManager em = emf.createEntityManager();
        final EntityTransaction etx = em.getTransaction();
        try {
            etx.begin();

            for (E e : (Collection<E>) em.createQuery("FROM " + entityClass.getSimpleName() + " e").getResultList())
                em.remove(e);

            etx.commit();
        } catch (RuntimeException e) {
            if (etx != null && etx.isActive())
                etx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     *
     */
    @Override
    public void deleteAll() {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction etx = em.getTransaction();
        try {
            etx.begin();

            em.createQuery("DELETE FROM Event").executeUpdate();
            em.createQuery("DELETE FROM ParticipantRecord").executeUpdate();
            em.createQuery("DELETE FROM ResourceManager").executeUpdate();
            em.createQuery("DELETE FROM Transaction").executeUpdate();

            etx.commit();
        } catch (RuntimeException e) {
            if (etx != null && etx.isActive())
                etx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param txUID
     * @return
     * @throws NoResultException
     * @throws NoSuchEntityException
     * @throws NonUniqueResultException
     */
    @Override
    public Transaction retrieveTransactionByTxUID(String txUID) throws NoResultException,
            NoSuchEntityException, NonUniqueResultException {
        return retrieveByField(Transaction.class, "txuid", txUID);
    }

    /**
     *
     * @param jndiName
     * @return
     */
    @Override
    public ResourceManager retrieveResourceManagerByJndiName(String jndiName) {
        return retrieveByField(ResourceManager.class, "jndiName", jndiName);
    }

    /**
     *
     * @param status
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Transaction> retrieveTransactionsWithStatus(Status status) {
        final String s = "FROM " + Transaction.class.getSimpleName() + " e WHERE status=:status";
        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(s).setParameter("status", status).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param txuid
     * @param rm
     * @param timestamp
     */
    @Override
    public void createParticipantRecord(String txuid, ResourceManager rm, Timestamp timestamp) {
        createParticipantRecord(retrieveTransactionByTxUID(txuid), rm, timestamp);
    }

    /**
     *
     * @param tx
     * @param rm
     * @param timestamp
     */
    @Override
    public void createParticipantRecord(Transaction tx, ResourceManager rm, Timestamp timestamp) {
        if (tx == null)
            throw new NullPointerException("Method called with null parameter: tx");

        if (rm == null)
            throw new NullPointerException("Method called with null parameter: rm");

        if (timestamp == null)
            throw new NullPointerException("Method called with null parameter: timestamp");

        ParticipantRecord pr = new ParticipantRecord(tx, rm, timestamp);
        update(pr);
    }

    /**
     *
     * @param txUID
     * @param rmJndiName
     * @return
     */
    @Override
    public ParticipantRecord retrieveParticipantRecord(String txUID, String rmJndiName) {
        final String query = "FROM " + ParticipantRecord.class.getSimpleName() +
                " e WHERE e.transaction.txuid=:txUID AND e.resourceManager.jndiName=:jndiName";
        final EntityManager em = emf.createEntityManager();
        try {
            return (ParticipantRecord) em.createQuery(query).setParameter("txUID", txUID)
                    .setParameter("jndiName", rmJndiName).getSingleResult();
        } finally {
            em.close();
        }
    }

    private boolean validateTxId(String txId) throws NullPointerException {
        return txId.matches(AbstractHandler.PATTERN_TXID);
    }

}
