package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
import org.jboss.narayana.txvis.logparsing.LogParser;
import org.jboss.narayana.txvis.persistence.DataAccessObject;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 13/05/2013
 * Time: 18:37
 */
@Local
public interface LogProcessor {
    @Asynchronous
    void startLogging();

    @PostConstruct
    void setup();

    @PreDestroy
    void stop();

    DataAccessObject getDao();

    void setDao(DataAccessObject dao);

    File getLogFile();

    void setLogFile(File logFile);

    Tailer getTailer();

    void setTailer(Tailer tailer);

    LogParser getLogParser();

    void setLogParser(LogParser logParser);
}
