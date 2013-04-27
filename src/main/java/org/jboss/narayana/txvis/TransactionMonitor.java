package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.dataaccess.ParticipantDAO;
import org.jboss.narayana.txvis.dataaccess.TransactionDAO;
import org.jboss.narayana.txvis.logprocessing.LogParserFactory;

import java.io.File;
import java.util.Arrays;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 01:50
 */
public class TransactionMonitor {

    private File logFile;
    private Tailer tailer;
    private TransactionDAO txDAO;
    private ParticipantDAO ptDAO;
    private Thread tailerThread;

    public TransactionMonitor() {
        DAOFactory.initialize();
        LogParserFactory.initialize(Arrays.asList(Configuration.getHandlers()));

        this.logFile = new File(Configuration.LOGFILE_PATH);
        this.tailer = new Tailer(logFile, LogParserFactory.getInstance(), Configuration.LOGFILE_POLL_INTERVAL, true);
        this.tailerThread = new Thread(this.tailer);
    }

    public void start() {
        this.tailerThread.start();
    }

    public void stop() {
        this.tailer.stop();
    }
}