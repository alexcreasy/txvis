package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.Utils;
import org.jboss.narayana.txvis.persistence.entities.Event;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;

import java.sql.Timestamp;
import java.util.regex.Matcher;

/**
 *
 * An example log line is as follows:
 * <code>>18:15:54,161 TRACE [com.arjuna.ats.arjuna] (default task-14) BasicAction::Begin() for action-id 0:ffffac1182da:579fdb56:51a46622:e77</code>
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/05/2013
 * Time: 19:34
 */
public class BasicActionHandler extends AbstractHandler {

    //


    public static final String REGEX =
            PATTERN_TIMESTAMP + ".*?BasicAction::(?<BASICACTION>Begin|Abort|phase2Abort|onePhaseCommit)\\(\\)\\sfor\\saction-id\\s" + PATTERN_TXID;

    public BasicActionHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        switch (matcher.group("BASICACTION")) {
            case "Begin":
                begin(matcher);
                break;
            case "Abort":
                abort(matcher);
                break;
            case "phase2Abort":
                phase2Abort(matcher);
                break;
            case "onePhaseCommit":
                onePhaseCommit(matcher);
                break;
        }
    }

    private void begin(Matcher matcher) {
        Timestamp timestamp = Utils.parseTimestamp(matcher.group(TIMESTAMP));
        Transaction t = new Transaction(matcher.group(TXID));
        t.addEvent(new Event(EventType.BEGIN, timestamp));
        t.setStartTime(timestamp);
        dao.create(t);

    }

    private void abort(Matcher matcher) {
        setStatus(matcher.group(TXID),Status.ROLLBACK_CLIENT, matcher.group(TIMESTAMP));
    }

    private void phase2Abort(Matcher matcher) {
        setStatus(matcher.group(TXID),Status.ROLLBACK_RESOURCE, matcher.group(TIMESTAMP));
    }

    private void onePhaseCommit(Matcher matcher) {
        setStatus(matcher.group(TXID),Status.COMMIT, matcher.group(TIMESTAMP));

    }

    private void setStatus(String txId, Status status, String rawTimestamp) {
        Timestamp timestamp = Utils.parseTimestamp(rawTimestamp);
        Transaction t = dao.retrieveTransactionByTxUID(txId);
        t.setStatus(status);
        EventType eventType = status.equals(Status.COMMIT) ? EventType.COMMIT : EventType.ABORT;
        t.addEvent(new Event(eventType, timestamp));
        t.setEndTime(timestamp);
        dao.update(t);
    }
}
