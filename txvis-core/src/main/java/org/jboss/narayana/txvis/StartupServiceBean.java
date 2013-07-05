package org.jboss.narayana.txvis;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBeanException;
import com.arjuna.ats.arjuna.common.arjPropertyManager;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import java.io.File;
import java.util.Properties;
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

        // Get this jboss instances unique identifier. It is imperative for distributed JTS mode that we have an accessible
        // unique node identifier for each node. This is not essential in a centralised JTA setup where any non null value
        // will suffice.
        String nodeid = null;

        try {
            nodeid = arjPropertyManager.getCoreEnvironmentBean().getNodeIdentifier();
        }
        catch (Exception e) {
            logger.warn("Unable to retrieve Node Identifier from CoreEnvironmentBean");
        }

        if (nodeid == null || nodeid.equals("1"));
            nodeid = System.getProperty("jboss.tx.node.id");

        if (nodeid == null)
            nodeid = System.getProperty("jboss.node.id");

        System.setProperty(Configuration.NODEID_SYS_PROP_NAME, nodeid);


        if (logger.isInfoEnabled()) {
            logger.info("TxVis: JBoss Transaction Visualization Tool");
            logger.info("Bootstrapping...");
            logger.info("Server Node Id: "+System.getProperty(Configuration.NODEID_SYS_PROP_NAME));
            logger.info("Logfile: "+Configuration.LOGFILE_PATH);

            logger.info("System Properties");
            for (String propName : System.getProperties().stringPropertyNames())
                logger.info(propName + " = " + System.getProperty(propName));
            logger.info("");
        }
        logMonitor.setFile(new File(Configuration.LOGFILE_PATH));
        logMonitor.start();
    }

    @PreDestroy
    protected void tearDown() {
        if (logger.isInfoEnabled())
            logger.info("Txvis tool cease monitoring");

        logMonitor.stop();

        if (logger.isInfoEnabled())
            logger.info("Txvis tool shutting down");
    }
}