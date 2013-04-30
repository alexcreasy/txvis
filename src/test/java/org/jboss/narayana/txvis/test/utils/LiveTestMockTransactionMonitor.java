package org.jboss.narayana.txvis.test.utils;

import org.apache.commons.io.input.Tailer;
import org.jboss.narayana.txvis.Configuration;
import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.logprocessing.LogParser;
import org.jboss.narayana.txvis.logprocessing.LogParserFactory;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 30/04/2013
 * Time: 15:47
 */
public class LiveTestMockTransactionMonitor {

    private File logFile;
    private Tailer tailer;
    private LiveTestMockLogParser logParser;
    private ExecutorService executor;

    public LiveTestMockTransactionMonitor() {
        DAOFactory.initialize();
        LogParserFactory.initialize(Arrays.asList(Configuration.getHandlers()));
        logParser =  new LiveTestMockLogParser(LogParserFactory.getInstance());
        this.logFile = new File(Configuration.LOGFILE_PATH);
        this.tailer = new Tailer(logFile,logParser, Configuration.LOGFILE_POLL_INTERVAL, true);
    }

    public void start() {
        this.executor = Executors.newSingleThreadExecutor();
        executor.execute(tailer);
    }

    public void stop() {
        executor.shutdown();
        this.tailer.stop();
    }

    public boolean isReady() {
        return logParser.hasStarted();
    }
}
