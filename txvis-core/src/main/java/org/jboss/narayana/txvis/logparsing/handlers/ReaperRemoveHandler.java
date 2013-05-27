package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.Utils;
import org.jboss.narayana.txvis.persistence.entities.Event;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;

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
            TIMESTAMP + "TransactionReaper::remove.*?BasicAction:\\s" +
                    TX_ID_PATTERN + ".*?ActionStatus\\.(?<ACTIONSTATUS>[A-Z]+)";

    public ReaperRemoveHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {

        switch(matcher.group("ACTIONSTATUS")) {

            case "COMMITTED":
                Transaction t = dao.retrieveTransactionByTxUID(matcher.group(TX_ID));
                t.setStatus(Status.COMMIT);
                t.addEvent(new Event(EventType.COMMIT, Utils.parseTimestamp(matcher.group(TIMESTAMP))));
                dao.update(t);
                break;
            case "ABORT":
                break;
            default:
                break;
        }
    }
}
