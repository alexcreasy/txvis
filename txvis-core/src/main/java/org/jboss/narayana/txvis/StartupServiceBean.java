package org.jboss.narayana.txvis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 31/05/2013
 * Time: 11:33
 */
@Singleton
@Startup
@DependsOn("LogMonitorBean")
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class StartupServiceBean {

    @EJB
    private LogMonitorBean logMonitor;


    @PostConstruct
    protected void setup() {
        logMonitor.setFile(new File(Configuration.LOGFILE_PATH));
        logMonitor.start();
    }

    @PreDestroy
    protected void tearDown() {
        logMonitor.stop();
    }
}