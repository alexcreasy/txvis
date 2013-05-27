package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.Utils;
import org.jboss.narayana.txvis.persistence.entities.Event;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 17:37
 */
public class ResourceDrivenRollbackHandler extends AbstractHandler {

   // 20:28:03,915 TRACE [com.arjuna.ats.arjuna] (pool-1-thread-1) BasicAction::phase2Abort() for action-id 0:ffff05974e0a:7540f300:517c26a8:1c
    /**
     * RegEx for parsing an resource abort vote
     *
     * Group 0: Whole matched part of string
     * Group 1: Transaction ID
     */
    public static final String REGEX = TIMESTAMP_PATTEN +
            ".*?BasicAction::phase2Abort\\(\\)\\sfor\\saction-id\\s" + TX_ID_PATTERN;

    public ResourceDrivenRollbackHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
       //dao.setOutcome(matcher.group(TX_ID_GROUPNAME), Status.ROLLBACK_RESOURCE);
       Transaction t = dao.retrieveTransactionByTxUID(matcher.group(TX_ID_GROUPNAME));
       t.setStatus(Status.ROLLBACK_RESOURCE);
       t.addEvent(new Event(EventType.ABORT, Utils.parseTimestamp(matcher.group(TIMESTAMP_GROUPNAME))));
       dao.update(t);
    }
}
