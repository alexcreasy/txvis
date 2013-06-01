package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.persistence.enums.Vote;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 20:34
 */
public class ResourceVoteAbortHandler extends AbstractHandler {

    public static final String REGEX = "tx_uid=" + PATTERN_TXID
            + ",.*eis\\sname\\s>\\s\\(([^\\s^)]+)\\)\\sfailed\\swith\\sexception";

    public ResourceVoteAbortHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {

    }
}
