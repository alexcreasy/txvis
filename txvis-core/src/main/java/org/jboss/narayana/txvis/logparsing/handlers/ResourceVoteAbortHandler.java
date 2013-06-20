package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 20:34
 */
public class ResourceVoteAbortHandler extends AbstractHandler {
    /**
     *
     */
    public static final String REGEX = PATTERN_TIMESTAMP + ".*?prepare\\son.*?tx_uid=" + PATTERN_TXID +
            ".*?jndiName=(?<JNDINAME>java:[\\w/]+).*?failed\\swith\\sexception";

    /**
     *
     */
    public ResourceVoteAbortHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.resourceVoteAbort(matcher.group(TXID), matcher.group("JNDINAME"), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
