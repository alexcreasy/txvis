package org.jboss.narayana.txvis.input;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 18/04/2013
 * Time: 11:03
 */
public class LogFileReader implements Runnable {

    private static final Logger logger = Logger.getLogger(LogFileReader.class.getName());

    private static final long POLL_INTERVAL = 500;

    private final File logFile;
    private final LogParser parser;
    private final boolean startAtBeginningOfFile;

    private boolean read;

    public LogFileReader(String logFile, LogParser logParser, boolean startAtBeginningOfFile)
            throws NullPointerException {

        if (logParser == null)
            throw new NullPointerException("Null logParser param");

        this.logFile = new File(logFile);
        this.parser = logParser;
        this.startAtBeginningOfFile = startAtBeginningOfFile;
    }

    public LogFileReader(File logFile, LogParser logParser, boolean startAtBeginningOfFile)
            throws NullPointerException {

        if (logFile == null)
            throw new NullPointerException("Null logFile param");

        if (logParser == null)
            throw new NullPointerException("Null logParser param");

        this.logFile = logFile;
        this.parser = logParser;
        this.startAtBeginningOfFile = startAtBeginningOfFile;
    }

    public void stop() {
        this.read = false;
    }

    public void run() {

        RandomAccessFile log;
        try {
            log = new RandomAccessFile(logFile, "r");
        } catch(FileNotFoundException e) {
            throw new RuntimeException("Unable to access log file: " + logFile.toString(), e);
        }

        long index = this.startAtBeginningOfFile ? 0 : this.logFile.length();
        long length = this.logFile.length();

        this.read = true;

        while (this.read) {
            try {
                if(length > index) {
                    log.seek(index);
                    String line = log.readLine();
                    while(line != null) {
                        this.parser.parseln(line);
                        line = log.readLine();
                    }
                    index = log.getFilePointer();
                }
                Thread.sleep(POLL_INTERVAL);
            } catch (IOException e) {
                logger.error("IOException while reading from log file", e);
            } catch (InterruptedException e) {
                logger.trace("LogFileReader thread interrupted", e);
            }
        }

        try {
            log.close();
        } catch (IOException e) {
            logger.warn("IOException while attempting to close log file", e);
        }
    }


    // Suppress default constructor
    private LogFileReader() {
        throw new IllegalStateException("Class invariant violation");
    }

}
