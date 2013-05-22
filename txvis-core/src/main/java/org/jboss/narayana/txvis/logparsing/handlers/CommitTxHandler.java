package org.jboss.narayana.txvis.logparsing.handlers;

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
    public static final String REGEX = "FileSystemStore.remove_committed\\((" + TX_ID + "),";

    public CommitTxHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        dao.setOutcome(matcher.group(1), Status.COMMIT);
    }


}
