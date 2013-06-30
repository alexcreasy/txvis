package org.jboss.narayana.txvis;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBeanException;
import com.arjuna.ats.arjuna.common.arjPropertyManager;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import java.io.File;
import java.util.UUID;

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

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB
    private LogMonitorBean logMonitor;

    @PostConstruct
    protected void setup() {
        try {
            arjPropertyManager.getCoreEnvironmentBean().setNodeIdentifier(UUID.randomUUID().toString());
        }
        catch (CoreEnvironmentBeanException e) {
            logger.fatal("Unable to set unique node identifier, shutting down", e);
            throw new RuntimeException(e);
        }

        if (logger.isInfoEnabled()) {
            logger.info("TxVis: JBoss Transaction Visualization Tool");
            logger.info("Bootstrapping...");
            logger.info("Server Node Id: "+arjPropertyManager.getCoreEnvironmentBean().getNodeIdentifier());
            logger.info("Logfile: "+Configuration.LOGFILE_PATH);
        }
        logMonitor.setFile(new File(Configuration.LOGFILE_PATH));
        logMonitor.start();
    }

    @PreDestroy
    protected void tearDown() {
        logMonitor.stop();
    }
}
