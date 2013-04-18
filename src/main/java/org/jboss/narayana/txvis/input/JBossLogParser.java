package org.jboss.narayana.txvis.input;

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
public class JBossLogParser implements LogParser {

    private static final Logger logger = Logger.getLogger(JBossLogParser.class.getName());

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

    public void parseln(String line) {
        // MATCH BEGIN TRANSACTION
        Matcher matcher = Patterns.TX_BEGIN.matcher(line);

        if (matcher.find()) {
            txDAO.create(matcher.group(2));
            logger.trace("txDAO.create( " + matcher.group(2) + " )");
            threadList.put(matcher.group(1), matcher.group(2));
            logger.trace("threadList.put( " + matcher.group(1) + " , " + matcher.group(2) + " )");
        }

        // MATCH ENLIST PARTICIPANT
        matcher = Patterns.TX_ENLIST.matcher(line);

        if (matcher.find()) {
            String txID = threadList.get(matcher.group(1));
            if (txID == null)
                logger.error("Thread: " + matcher.group(1)
                        + " does not have an associated transaction");

            Transaction tx = txDAO.get(txID);
            Participant participant = participantDAO.getParticipant(matcher.group(2));

            tx.addParticipant(participant);
        }
    }
}