package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.logparsing.LogParser;
import org.jboss.narayana.txvis.logparsing.LogParserFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 01:50
 */
@Singleton
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Lock(LockType.READ)
public class LogMonitorServiceBean {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Resource
    private SessionContext sessionContext;

    private File logFile;
    private Tailer tailer;
    private LogParser logParser;

    /**
     *
     */
    @Asynchronous
    public void start() {
        if (tailer == null) {
            try {
                tailer = new Tailer(logFile, logParser, Configuration.LOGFILE_POLL_INTERVAL, true);
                tailer.run();

            } catch (Exception e) {
                logger.fatal("Unhandled exception, stopping logfile monitor", e);
                sessionContext.getBusinessObject(LogMonitorServiceBean.class).stop();
            }
        }
    }

    /**
     *
     */
    public void stop() {
        if (tailer != null) {
            tailer.stop();
            tailer = null;
        }
    }

    /**
     *
     * @param file
     */
    public void setFile(File file) throws NullPointerException, IllegalStateException {
        if (file == null)
            throw new NullPointerException("Method called with null parameter: file");

        // Check that the log monitor is not currently running before
        // changing file name
        if (tailer != null)
            throw new IllegalStateException("Cannot call setFile while LogMonitor is running");

        logFile = file;
        logParser = LogParserFactory.getInstance();
    }

    /**
     *
     * @return
     */
    public boolean isRunning() {
        return tailer != null;
    }

}