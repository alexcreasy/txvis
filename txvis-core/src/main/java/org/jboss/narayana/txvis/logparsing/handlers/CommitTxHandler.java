package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.Utils;
import org.jboss.narayana.txvis.persistence.entities.Event;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;

import java.sql.Timestamp;
import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 13:50
 */
public final class CommitTxHandler extends AbstractHandler {

    /**
     * RegEx pattern for parsing a successful commit log entry.
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Transaction ID
     */
    public static final String REGEX = PATTERN_TIMESTAMP +
            ".*?FileSystemStore.remove_committed\\(" + PATTERN_TXID + ",";

    public CommitTxHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        Timestamp timestamp = Utils.parseTimestamp(matcher.group(TIMESTAMP));
        Transaction t = dao.retrieveTransactionByTxUID(matcher.group(TXID));
        t.setStatus(Status.COMMIT);
        t.addEvent(new Event(EventType.COMMIT, timestamp));
        t.setEndTime(timestamp);
        dao.update(t);
    }


}
