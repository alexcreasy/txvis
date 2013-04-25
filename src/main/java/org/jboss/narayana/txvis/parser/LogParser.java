package org.jboss.narayana.txvis.parser;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.Status;
import org.jboss.narayana.txvis.data.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 18/04/2013
 * Time: 11:06
 */
public class LogParser implements TailerListener {

    private static final Logger logger = Logger.getLogger("org.jboss.narayana.txvis");

    private final Map<String, String> threadList = new HashMap<String, String>();

    private final TransactionDAO txDAO;
    private final ParticipantDAO participantDAO;

    public LogParser(TransactionDAO txDAO, ParticipantDAO participantDAO)
            throws IllegalArgumentException {
        if (txDAO == null)
            throw new IllegalArgumentException("null txDAO param");
        if (participantDAO == null)
            throw new IllegalArgumentException("null participantDAO param");

        this.txDAO = txDAO;
        this.participantDAO = participantDAO;
    }

    public void handle(String line) {
        Matcher matcher;

        // MATCH BEGIN TRANSACTION
        if ((matcher = Patterns.TX_BEGIN.matcher(line)).find()) {
            if (logger.isDebugEnabled())
                logger.debug("Parsed Tx Begin: txID=" + matcher.group(2) +
                        ", thread=" + matcher.group(1));

            txDAO.create(matcher.group(2));

            threadList.put(matcher.group(1), matcher.group(2));
        }
        // MATCH ENLIST PARTICIPANT
        else if ((matcher = Patterns.TX_ENLIST.matcher(line)).find()) {
            String txID = threadList.get(matcher.group(1));

            if (logger.isDebugEnabled())
                logger.debug("Parsed enlist: participantID=" + matcher.group(2) +
                        ", txID=" + txID + ", thread=" + matcher.group(1));

            if (txID == null)
                logger.error("Thread: " + matcher.group(1) +
                        " does not have an associated transaction");

            txDAO.get(txID).addParticipant(participantDAO.get(matcher.group(2)));
        }
        // MATCH COMMIT
        else if ((matcher = Patterns.TX_COMMIT.matcher(line)).find()) {
            if (logger.isDebugEnabled())
                logger.debug("Parsed commit: txID=" + matcher.group(2));
            txDAO.get(matcher.group(2)).setStatus(Status.COMMIT);
        }
        else {
            //logger.trace("Did not parse line: " + line);
        }
    }

    public void init(Tailer tailer) {}
    public void fileNotFound() {
        logger.fatal("couldn't find the log file");
    }
    public void fileRotated() {}

    public void handle(Exception ex) {
        logger.error("Exception thrown by log tailer", ex);
    }

}