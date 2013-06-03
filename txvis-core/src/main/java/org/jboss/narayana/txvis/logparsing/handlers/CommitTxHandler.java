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

    public static final String REGEX = PATTERN_TIMESTAMP +
            ".*?FileSystemStore.remove_committed\\(" + PATTERN_TXID + ",";

    public CommitTxHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        Transaction t = dao.retrieveTransactionByTxUID(matcher.group(TXID));
        t.setStatus(Status.COMMIT, Utils.parseTimestamp(matcher.group(TIMESTAMP)));
        dao.update(t);
    }


}
