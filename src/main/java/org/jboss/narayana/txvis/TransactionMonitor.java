package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.logprocessing.LogParserFactory;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 01:50
 */
public class TransactionMonitor {

    private File logFile;
    private Tailer tailer;
    private ExecutorService executor;

    public TransactionMonitor() {
        DAOFactory.initialize();
        LogParserFactory.initialize(ConfigurationManager.INSTANCE.getLogHandlers());

        this.logFile = new File(ConfigurationManager.INSTANCE.getLogfilePath());
        this.tailer = new Tailer(logFile, LogParserFactory.getInstance(),
                ConfigurationManager.INSTANCE.getLogfilePollInterval(), true);
    }

    public void start() {
        this.executor = Executors.newSingleThreadExecutor();
        executor.execute(tailer);
    }

    public void stop() {
        executor.shutdown();
        this.tailer.stop();
    }
}