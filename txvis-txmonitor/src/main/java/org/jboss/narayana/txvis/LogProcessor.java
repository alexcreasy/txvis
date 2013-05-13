package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.logparsing.LogParser;
import org.jboss.narayana.txvis.logparsing.LogParserFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 01:50
 */
@Singleton
@Startup
public class LogProcessor {

    private DataAccessObject dao;

    private File logFile;
    private Tailer tailer;
    private LogParser logParser;
    private ExecutorService executor;

    public LogProcessor() {
        this.logFile = new File(Configuration.LOGFILE_PATH);
        this.executor = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    private void start() {

        System.err.println("\n\n\n\n START CALLED \n\n\n\n");

        this.logParser = LogParserFactory.getInstance(dao);

        this.tailer = new Tailer(logFile, logParser,
                Configuration.LOGFILE_POLL_INTERVAL, true);

        executor.execute(tailer);
    }

    @PreDestroy
    private void stop() {
        executor.shutdown();
        this.tailer.stop();
    }



    private static Map<String, LogProcessor> instances = new HashMap<String, LogProcessor>();

    public LogProcessor getInstance(File file, DataAccessObject dao) {

    }





}