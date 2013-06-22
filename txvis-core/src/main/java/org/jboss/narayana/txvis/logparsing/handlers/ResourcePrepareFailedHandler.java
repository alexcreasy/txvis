package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 20:34
 */
public class ResourcePrepareFailedHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "prepare\\son.*?tx_uid="+ PATTERN_TXUID +".*?" +
            "jndiName=(?<JNDINAME>java:[\\w/]+).*?failed\\swith\\sexception\\sXAException\\.(?<XAEXCEPTION>[A-Z_]+)";

    /**
     *
     */
    public ResourcePrepareFailedHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.resourceFailedToPrepare(matcher.group(TXUID), matcher.group("JNDINAME"), matcher.group("XAEXCEPTION"),
                parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
