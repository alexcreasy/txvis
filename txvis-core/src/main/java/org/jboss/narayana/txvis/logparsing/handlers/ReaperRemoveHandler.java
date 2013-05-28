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
 *
 * Example Log lines: <br/>
 * <code>11:58:34,955 TRACE [com.arjuna.ats.arjuna] (default task-14) TransactionReaper::remove (
 * BasicAction: 0:ffff05974e31:-60d4f33f:519c9c7d:d2 status: ActionStatus.COMMITTED )</code>
 * <br/>
 *
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/05/2013
 * Time: 17:20
 */
public class ReaperRemoveHandler extends AbstractHandler {

    public static final String REGEX =
            TIMESTAMP + ".*?TransactionReaper::remove.*?BasicAction:\\s" +
                    PATTERN_TXID + ".*?ActionStatus\\.(?<ACTIONSTATUS>[A-Z]+)";

    public ReaperRemoveHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        Timestamp timestamp = Utils.parseTimestamp(matcher.group(TIMESTAMP));

        switch(matcher.group("ACTIONSTATUS")) {
            case "COMMITTED":
                Transaction t = dao.retrieveTransactionByTxUID(matcher.group(TXID));
                t.setStatus(Status.COMMIT);
                t.addEvent(new Event(EventType.COMMIT, timestamp));
                t.setEndTime(timestamp);
                dao.update(t);
                break;
            case "ABORT":
                break;
            default:
                break;
        }
    }
}
