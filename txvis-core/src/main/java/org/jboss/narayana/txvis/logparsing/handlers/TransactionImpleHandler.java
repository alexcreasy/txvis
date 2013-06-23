package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 23/06/2013
 * Time: 18:22
 */
public class TransactionImpleHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "TransactionImple.enlistResource\\s\\(\\s" + PATTERN_XARESOURCEWRAPPERIMPL;

    /**
     *
     */
    public TransactionImpleHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.enlistResourceManagerByThreadID(matcher.group(THREAD_ID), matcher.group(RM_JNDI_NAME),
                matcher.group(RM_PRODUCT_NAME), matcher.group(RM_PRODUCT_VERSION), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
