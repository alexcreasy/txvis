package org.jboss.narayana.txvis.interceptors;

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

        final EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Object result = ctx.proceed();

            em.getTransaction().commit();

            return result;
        }
        catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        }
        finally {
            em.close();
        }
    }
}
