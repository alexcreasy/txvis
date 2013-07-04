package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class XAResourceRecordHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "XAResourceRecord\\.XAResourceRecord.*?"+"tx_uid="+PATTERN_TXUID+".*?" +
            PATTERN_XARESOURCEWRAPPERIMPL;

    /**
     *
     */
    public XAResourceRecordHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.enlistResourceManager(matcher.group(TXUID),  matcher.group(RM_JNDI_NAME), matcher.group(RM_PRODUCT_NAME),
                matcher.group(RM_PRODUCT_VERSION), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
