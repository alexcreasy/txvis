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
            PATTERN_TIMESTAMP + ".*?BasicAction::Begin\\(\\)\\sfor\\saction-id\\s" + PATTERN_TXID;

    public BeginTxHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        Transaction t = new Transaction(matcher.group(TXID));
        //t.addEvent(new Event(t, Utils.parseTimestamp(matcher.group(TIMESTAMP)), EventType.BEGIN));
        t.addEvent(new Event(EventType.BEGIN, Utils.parseTimestamp(matcher.group(TIMESTAMP))));
        dao.create(t);
    }
}
