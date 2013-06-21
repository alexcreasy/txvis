package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.logparsing.AbstractHandler;

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
    public static final String REGEX = "prepare\\son.*?tx_uid="+ PATTERN_TXUID +".*?" +
            "jndiName=(?<JNDINAME>java:[\\w/]+).*?failed";

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
        service.resourceVoteAbort(matcher.group(TXUID), matcher.group("JNDINAME"), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
