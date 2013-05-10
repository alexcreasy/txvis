package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
import org.jboss.narayana.txvis.dataaccess.DataAccessObject;
import org.jboss.narayana.txvis.logprocessing.LogParser;
import org.jboss.narayana.txvis.logprocessing.LogParserFactory;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 01:50
 */
@Stateful
public class LogProcessor {

    @EJB
    private DataAccessObject dao;

    private File logFile;
    private Tailer tailer;
    private LogParser logParser;
    private ExecutorService executor;

    public LogProcessor() {
        this.logFile = new File(Configuration.LOGFILE_PATH);
    }

    public void setLogFile(File logFile) throws NullPointerException {
        if (logFile == null)
            throw new NullPointerException("Null logFile passed to LogProcessor.setLogFile");
        this.logFile = logFile;
    }

    public void start() {
        this.logParser = LogParserFactory.getInstance(dao);
        this.tailer = new Tailer(logFile, logParser,
                Configuration.LOGFILE_POLL_INTERVAL, true);
        this.executor = Executors.newSingleThreadExecutor();
        executor.execute(tailer);
    }

    public void stop() {
        executor.shutdown();
        this.tailer.stop();
    }
}