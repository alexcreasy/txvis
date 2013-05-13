package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.logparsing.LogParser;
import org.jboss.narayana.txvis.logparsing.LogParserFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 01:50
 */
@Singleton
@Startup
public class LogProcessorImpl implements LogProcessor {

    @EJB
    private DataAccessObject dao;

    private File logFile;
    private Tailer tailer;
    private LogParser logParser;

    public LogProcessorImpl() {}

    @Override
    @Asynchronous
    public void startLogging() {
        this.tailer.run();
    }

    @Override
    @PostConstruct
    public void setup() {
        this.logFile = new File(Configuration.LOGFILE_PATH);
        this.logParser = LogParserFactory.getInstance(dao);
        this.tailer = new Tailer(logFile, logParser,
                Configuration.LOGFILE_POLL_INTERVAL, true);

        startLogging();
    }

    @Override
    @PreDestroy
    public void stop() {
        this.tailer.stop();
    }


    // GETTERS / SETTERS FOR CONTAINER

    @Override
    public DataAccessObject getDao() {
        return dao;
    }

    @Override
    public void setDao(DataAccessObject dao) {
        this.dao = dao;
    }

    @Override
    public File getLogFile() {
        return logFile;
    }

    @Override
    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    @Override
    public Tailer getTailer() {
        return tailer;
    }

    @Override
    public void setTailer(Tailer tailer) {
        this.tailer = tailer;
    }

    @Override
    public LogParser getLogParser() {
        return logParser;
    }

    @Override
    public void setLogParser(LogParser logParser) {
        this.logParser = logParser;
    }
}