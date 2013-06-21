package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.logparsing.AbstractHandler;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 13:50
 */
public final class CommitTxHandler extends AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "FileSystemStore.remove_committed\\("+ PATTERN_TXUID +",";

    /**
     *
     */
    public CommitTxHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.commitTx2Phase(matcher.group(TXUID), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
