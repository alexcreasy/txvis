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

    @Override
    public <E> void create(E entity) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.create() entity=`{0}`", entity));

        final EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            try {
                em.persist(entity);
                em.getTransaction().commit();

            } catch (Throwable throwable) {
                em.getTransaction().rollback();

                logger.warn(MessageFormat.format(
                        "An error occured while attempting to persist entity: {0}",
                        em.getClass().getSimpleName()), throwable);
            }
        } finally {
            em.close();
        }
    }

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

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> retrieveAll(Class<E> entityClass) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.retrieveAll() entityClass=`{0}`", entityClass));

        final String s = "FROM " + entityClass.getSimpleName() + " e";

        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(s).getResultList();

        } catch (NoResultException e) {
            logger.warn(MessageFormat.format(
                    "DataAccessObjectBean.retrieveAll: No result found for search: class=`{0}`",
                    entityClass));
            return null;

        } finally {
            em.close();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E, V> E retrieveByField(Class<E> entityClass, String field, V value)
            throws NonUniqueResultException, NoSuchEntityException {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format(
                    "DataAccessObjectBean.retrieveByField() entityClass=`{0}`, field=`{1}`, value=`{2}`",
                    entityClass, field, value));

        final String query = MessageFormat.format(
                "FROM {0} e WHERE e.{1}=:value", entityClass.getSimpleName(), field);

        final EntityManager em = emf.createEntityManager();
        try {
            return (E) em.createQuery(query).setParameter("value", value).getSingleResult();

        } catch (NoResultException e) {
            logger.warn(MessageFormat.format(
                    "DataAccessObjectBean.retrieveByField: No result found for search: class=`{0}`, field=`{1}`, value=`{2}`",
                    entityClass, field, value));
            return null;

        } finally {
            em.close();
        }
    }

    @Override
    public <E> void update(E entity) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.update() entity=`{0}`", entity));

        final EntityManager em = emf.createEntityManager();
        try {
            final boolean notActive = !em.getTransaction().isActive();

            if (notActive)
                em.getTransaction().begin();
            try {
                em.merge(entity);
                if (notActive)
                    em.getTransaction().commit();

            } catch (Throwable throwable) {
                    em.getTransaction().rollback();

                logger.warn(MessageFormat.format(
                        "An error occured while attempting to update entity: {0}",
                        entity.getClass().getSimpleName()), throwable);
            }
        } finally {
            em.close();
        }
    }

    @Override
    public <E> void delete(E entity) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.delete() entity=`{0}`", entity));

        final EntityManager em = emf.createEntityManager();
        try {
            final boolean notActive = !em.getTransaction().isActive();

            if (notActive)
                em.getTransaction().begin();
            try {
                em.remove(em.merge(entity));
                if (notActive)
                    em.getTransaction().commit();

            } catch (Throwable throwable) {
                    em.getTransaction().rollback();

                logger.warn(MessageFormat.format(
                        "An error occured while attempting to delete entity: {0}",
                        entity.getClass().getSimpleName()), throwable);
            }
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> void deleteAll(Class<E> entityClass) {
        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("DataAccessObjectBean.deleteAll() entityClass=`{0}`", entityClass));

        final EntityManager em = emf.createEntityManager();
        try {
            final boolean notActive = !em.getTransaction().isActive();

            if (notActive)
                em.getTransaction().begin();
            try {
                for (E e : (Collection<E>) em.createQuery("FROM " + entityClass.getSimpleName() + " e").getResultList())
                    em.remove(e);

                if (notActive)
                    em.getTransaction().commit();

            } catch (Throwable throwable) {
                if (notActive)
                    em.getTransaction().rollback();

                logger.warn(MessageFormat.format(
                        "An error occured while attempting to delete all entities: {0}",
                        entityClass.getSimpleName()), throwable);
            }

//            em.getTransaction().begin();
//            for (E e : retrieveAll(entityClass))
//                em.remove(em.merge(e));
//            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @Override
    public void deleteAll() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Event").executeUpdate();
            em.createQuery("DELETE FROM ParticipantRecord").executeUpdate();
            em.createQuery("DELETE FROM ResourceManager").executeUpdate();
            em.createQuery("DELETE FROM Transaction").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    @Override
    public Transaction retrieveTransactionByTxUID(String txUID) throws NoResultException,
            NoSuchEntityException, NonUniqueResultException {
        return retrieveByField(Transaction.class, "txuid", txUID);
    }

    @Override
    public ResourceManager retrieveResourceManagerByJndiName(String jndiName) {
        try {
            return retrieveByField(ResourceManager.class, "jndiName", jndiName);
        } catch (NoResultException e) {
            return null;
        }
    }

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

    @Override
    public void createParticipantRecord(String txuid, ResourceManager rm, Timestamp timestamp) {
        createParticipantRecord(retrieveTransactionByTxUID(txuid), rm, timestamp);
    }

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
