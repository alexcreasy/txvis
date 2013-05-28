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

    /**
     * RegEx for parsing an resource abort vote
     *
     * Group 0: Whole matched part of string
     * Group 1: Transaction ID
     */
    public static final String REGEX = PATTERN_TIMESTAMP +
            ".*?BasicAction::phase2Abort\\(\\)\\sfor\\saction-id\\s" + PATTERN_TXID;

    public ResourceDrivenRollbackHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
       Transaction t = dao.retrieveTransactionByTxUID(matcher.group(TXID));
       t.setStatus(Status.ROLLBACK_RESOURCE);
       t.addEvent(new Event(EventType.ABORT, Utils.parseTimestamp(matcher.group(TIMESTAMP))));
       dao.update(t);
    }
}
