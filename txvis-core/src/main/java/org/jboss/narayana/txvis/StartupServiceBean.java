package org.jboss.narayana.txvis;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import org.apache.log4j.Logger;

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

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB
    private LogMonitorBean logMonitor;

    @PostConstruct
    protected void setup() {
        if (logger.isInfoEnabled()) {
            logger.info("-------------------------------------------------------");
            logger.info("      TxVis: JBoss Transaction Visualization Tool      ");
            logger.info("-------------------------------------------------------");
            logger.info("Server Node Id: "+arjPropertyManager.getCoreEnvironmentBean().getNodeIdentifier());
            logger.info("Logfile: "+Configuration.LOGFILE_PATH);
            logger.info("-------------------------------------------------------");
        }
        logMonitor.setFile(new File(Configuration.LOGFILE_PATH));
        logMonitor.start();
    }

    @PreDestroy
    protected void tearDown() {
        logMonitor.stop();
    }
}
