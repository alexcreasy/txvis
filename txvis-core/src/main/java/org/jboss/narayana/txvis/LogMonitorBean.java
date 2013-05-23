package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.logparsing.LogParser;
import org.jboss.narayana.txvis.logparsing.LogParserFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 01:50
 */
@Singleton
@Startup
@LocalBean
@DependsOn("DataAccessObjectBean")
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class LogMonitorBean {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Resource
    private SessionContext sessionContext;

    @EJB
    private DataAccessObject dao;

    private File logFile;
    private Tailer tailer;
    private LogParser logParser;


    public void startLogMonitoring() {
        if (tailer == null) {
            try {
                tailer = new Tailer(logFile, logParser, Configuration.LOGFILE_POLL_INTERVAL, true);
                Thread thread = new Thread(tailer);
                thread.setDaemon(true);
                thread.start();

            } catch (Exception e) {
                tailer.stop();
                logger.fatal("Unhandled exception, stopping logfile monitor", e);
            }
        }
    }

    @PreDestroy
    public void stop() {
        if (tailer != null) {
            tailer.stop();
            tailer = null;
        }
    }

    @PostConstruct
    private void setup() {
        if (logger.isInfoEnabled())
            logger.info("Initialising LogMonitor");
        logFile = new File(Configuration.LOGFILE_PATH);
        logParser = LogParserFactory.getInstance(dao);
        startLogMonitoring();
        //sessionContext.getBusinessObject(LogMonitor.class).startLogMonitoring();
    }
}