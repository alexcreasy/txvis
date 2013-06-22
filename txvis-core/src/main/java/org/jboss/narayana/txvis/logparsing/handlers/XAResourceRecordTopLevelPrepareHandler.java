package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 17:45
 */
public class XAResourceRecordTopLevelPrepareHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "XAResourceRecord.topLevelPrepare.*?jndiName=(?<JNDINAME>java:[\\w/]+).*?" +
            "tx_uid="+ PATTERN_TXUID;

    /**
     *
     */
    public XAResourceRecordTopLevelPrepareHandler() {
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
