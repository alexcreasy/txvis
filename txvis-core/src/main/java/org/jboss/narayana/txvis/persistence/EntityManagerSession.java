package org.jboss.narayana.txvis.persistence;

import org.jboss.narayana.txvis.interceptors.LoggingInterceptor;

import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Singleton bean provides thread-safe session based access to an EntityManager. This
 * is useful when not using container manager transactions (CMT) (txvis is unable to use
 * CMT as they are not available when using RESOURCE_LOCAL rather than JTA transactions.
 * Txvis is unable to use JTA transactions as the tool is monitoring transactions produced by
 * JBoss AS,
 *
 *
 * The following code snippet is an example of how this class should be used.
 * <code>
 *     @EJB
 *     private EntityManagerSession entityManagerSession;
 *     ...
 *     public void someMethod() {
 *
 *         EntityManager em = entityManagerSession.getEntityManager();
 *
 *         // null check is necessary to see if we're the top layer of the call
 *         if (em == null)
 *             em = entityManagerSession.createEntityManager();
 *
 *        // This part is **ESSENTIAL**
 *        try {
 *
 *            // Do something.
 *
 *        } finally {
 *            entityManagerSession.closeSession();
 *        }
 *     }
 * </code>
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/07/2013
 * Time: 13:05
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors(LoggingInterceptor.class)
public class EntityManagerSession {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();

    public EntityManager getEntityManager() {
        return threadLocal.get();
    }

    public EntityManager createEntityManager() throws IllegalStateException {

        EntityManager currentEM = threadLocal.get();

        if (currentEM != null && currentEM.isOpen())
            throw new IllegalStateException("Cannot call createEntityManager when a session is in progress on this thread");

        threadLocal.set(emf.createEntityManager());
        return threadLocal.get();
    }

    public void closeEntityManager() {
        EntityManager currentEM = threadLocal.get();

        if (currentEM != null) {
            if (currentEM.isOpen())
                currentEM.close();
            threadLocal.remove();
        }
    }


}
