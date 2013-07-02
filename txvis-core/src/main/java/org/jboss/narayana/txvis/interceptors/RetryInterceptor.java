package org.jboss.narayana.txvis.interceptors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 02/07/2013
 * Time: 00:56
 */
public class RetryInterceptor implements Serializable {

    private static final int MAX_RETRIES = 3;
    private static final int BASE_WAIT_MILIS = 100;

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        Object o = null;

        try {
            o = ctx.proceed();
        }
        catch (NullPointerException e) {

            //FIXME back off logic needs to be replaced by shadow tabling
            int tries = 0;
            boolean success = false;
            while (!success && tries < MAX_RETRIES) {
                try {
                    Thread.sleep(BASE_WAIT_MILIS * tries);
                    ctx.getMethod().invoke(ctx.getTarget(), ctx.getParameters());
                    success = true;
                }
                catch (InterruptedException ie) {
                    throw new RuntimeException(e);
                }
                catch (NullPointerException n) {
                    // we'll try again!
                }
            }

            if (!success)
                throw new RuntimeException("Exceeded max number of retries for method: "+ctx.getMethod().getName());
        }
        return o;
    }
}
