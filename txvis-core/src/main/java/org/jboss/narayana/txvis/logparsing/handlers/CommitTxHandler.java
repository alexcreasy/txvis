package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.Utils;
import org.jboss.narayana.txvis.persistence.entities.Event;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;

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
    public static final String REGEX = TIMESTAMP_PATTEN +
            ".*?FileSystemStore.remove_committed\\(" + TX_ID_PATTERN + ",";

    public CommitTxHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        Transaction t = dao.retrieveTransactionByTxUID(matcher.group(TX_ID_GROUPNAME));
        t.setStatus(Status.COMMIT);
        t.addEvent(new Event(EventType.COMMIT, Utils.parseTimestamp(matcher.group(TIMESTAMP_GROUPNAME))));
        dao.update(t);
    }


}
