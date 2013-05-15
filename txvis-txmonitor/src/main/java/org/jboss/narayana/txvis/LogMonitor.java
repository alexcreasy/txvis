package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
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
public class LogMonitor {

    @Resource
    private SessionContext sessionContext;

    @EJB
    private DataAccessObject dao;

    private File logFile;
    private Tailer tailer = null;
    private LogParser logParser;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void startLogging() {
        tailer.run();
    }

    @PostConstruct
    public void setup() {
        this.logFile = new File(Configuration.LOGFILE_PATH);
        this.logParser = LogParserFactory.getInstance(dao);
        tailer = new Tailer(logFile, logParser, Configuration.LOGFILE_POLL_INTERVAL, true);
        sessionContext.getBusinessObject(LogMonitor.class).startLogging();
    }

    @PreDestroy
    public void stop() {
        tailer.stop();
    }
}