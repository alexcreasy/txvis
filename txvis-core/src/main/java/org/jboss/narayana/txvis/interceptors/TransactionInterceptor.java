package org.jboss.narayana.txvis.interceptors;

import org.apache.log4j.Logger;

import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/06/2013
 * Time: 15:30
 */

public class TransactionInterceptor implements Serializable {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {

        final Logger logger = Logger.getLogger(ctx.getMethod().getDeclaringClass().getName());
        final EntityManager em = emf.createEntityManager();

        final boolean notActive = !em.getTransaction().isActive();
        Object result = null;
        try {
            if (notActive)
                em.getTransaction().begin();

            result = ctx.proceed();

            if (notActive)
                em.getTransaction().commit();


        }
        catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();

            logger.warn("TransactionInterceptor: Transaction rolled back", e);
        }
        finally {
            em.close();
        }
        return result;
    }
}
