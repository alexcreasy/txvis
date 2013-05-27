package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.Utils;
import org.jboss.narayana.txvis.persistence.entities.Event;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.EventType;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 23:50
 */
public final class BeginTxHandler extends AbstractHandler {

    /**
     * RegEx pattern for detecting creation of a transaction
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Transaction ID
     */
    public static final String REGEX =
            TIMESTAMP_PATTEN + ".*?BasicAction::Begin\\(\\)\\sfor\\saction-id\\s" + TX_ID_PATTERN;

    public BeginTxHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        Transaction t = new Transaction(matcher.group(TX_ID_GROUPNAME));
        t.addEvent(new Event(t, Utils.parseTimestamp(matcher.group(TIMESTAMP_GROUPNAME)), EventType.BEGIN));
        dao.create(t);
    }
}
