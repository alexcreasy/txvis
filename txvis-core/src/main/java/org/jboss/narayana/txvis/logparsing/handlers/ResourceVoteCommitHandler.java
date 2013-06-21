package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.logparsing.AbstractHandler;
import org.jboss.narayana.txvis.logparsing.JbossAS8Handler;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 17:45
 */
public class ResourceVoteCommitHandler extends JbossAS8Handler {
    /**
     *
     */
    public static final String REGEX = "XAResourceRecord.topLevelPrepare.*?jndiName=(?<JNDINAME>java:[\\w/]+).*?" +
            "tx_uid="+ PATTERN_TXUID;

    /**
     *
     */
    public ResourceVoteCommitHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.resourceVoteCommit(matcher.group(TXUID), matcher.group("JNDINAME"), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
