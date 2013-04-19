package org.jboss.narayana.txvis.input;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.*;
import org.jboss.narayana.txvis.data.Participant;
import org.jboss.narayana.txvis.data.ParticipantDAO;
import org.jboss.narayana.txvis.data.Transaction;
import org.jboss.narayana.txvis.data.TransactionDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 18/04/2013
 * Time: 11:06
 */
public class JBossLogParser implements TailerListener {

    private static final Logger logger = Logger.getLogger("org.jboss.narayana.txvis");

    private final Map<String, String> threadList = new HashMap<String, String>();
    private final TransactionDAO txDAO;
    private final ParticipantDAO participantDAO;


    public JBossLogParser(TransactionDAO txDAO, ParticipantDAO participantDAO) {
        if (txDAO == null)
            throw new NullPointerException("null txDAO param");

        if (participantDAO == null)
            throw new NullPointerException("nul participantDAO param");

        this.txDAO = txDAO;
        this.participantDAO = participantDAO;
    }

    public void handle(String line) {
        logger.info("Read line: " + line);
        // MATCH BEGIN TRANSACTION
        Matcher matcher = Patterns.TX_BEGIN.matcher(line);

        if (matcher.find()) {
            logger.info("Matched line to pattern " + Patterns.TX_BEGIN);
            txDAO.create(matcher.group(2));
            logger.info("txDAO.create( " + matcher.group(2) + " )");
            threadList.put(matcher.group(1), matcher.group(2));
            logger.info("threadList.put( " + matcher.group(1) + " , " + matcher.group(2) + " )");
        }

        // MATCH ENLIST PARTICIPANT
        matcher = Patterns.TX_ENLIST.matcher(line);

        if (matcher.find()) {
            logger.info("Matched line to pattern " + Patterns.TX_BEGIN);
            String txID = threadList.get(matcher.group(1));
            if (txID == null)
                logger.error("Thread: " + matcher.group(1)
                        + " does not have an associated transaction");

            Transaction tx = txDAO.get(txID);
            Participant participant = participantDAO.getParticipant(matcher.group(2));

            tx.addParticipant(participant);
        }
        logger.info("Did not parse line");
    }

    public void init(Tailer tailer) {}
    public void fileNotFound() {
        logger.fatal("COULDN'T FIND THE RUDDY FILE!");
    }
    public void fileRotated() {}

    public void handle(Exception ex) {
        logger.fatal("\n\n\n\n\n EXCEPTION WITH LOG TAILER", ex);
    }

}