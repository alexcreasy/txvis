package org.jboss.narayana.txvis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 14/05/2013
 * Time: 14:36
 */
@Singleton
@Startup
@LocalBean
public class MonitorDaemon {

    @EJB
    private LogProcessor logProcessor;

    @PostConstruct
    public void bootstrap() {
        logProcessor.startLogging();
    }

    @PreDestroy
    public void tearDown() {
        logProcessor.stop();
    }
}
