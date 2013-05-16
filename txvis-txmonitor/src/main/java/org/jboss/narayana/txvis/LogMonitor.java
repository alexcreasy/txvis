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
@Lock(LockType.READ)
public class LogMonitor {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private boolean running;

    @Resource
    private SessionContext sessionContext;

    @EJB
    private DataAccessObject dao;

    private File logFile;
    private Tailer tailer;
    private LogParser logParser;

    @Asynchronous
    public void start() {
        if (!running) {
            if (logger.isInfoEnabled())
                logger.info("Begin tailing logfile");
            tailer = new Tailer(logFile, logParser, Configuration.LOGFILE_POLL_INTERVAL, true);
            running = true;
            tailer.run();
        }
    }

    @PreDestroy
    public void stop() {
        if (running) {
            tailer.stop();
            if (logger.isInfoEnabled())
                logger.info("Stopped tailing logfile");
            running = false;
        }
    }

    @PostConstruct
    private void setup() {
        if (logger.isInfoEnabled())
            logger.info("Initialising LogMonitor");
        logFile = new File(Configuration.LOGFILE_PATH);
        logParser = LogParserFactory.getInstance(dao);
        sessionContext.getBusinessObject(LogMonitor.class).start();
    }
}