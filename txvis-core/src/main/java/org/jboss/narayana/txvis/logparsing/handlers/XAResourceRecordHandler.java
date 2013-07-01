package org.jboss.narayana.txvis.logparsing.handlers;

import java.sql.Timestamp;
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
    public static final String REGEX = "XAResourceRecord\\.XAResourceRecord.*?" + "tx_uid="+PATTERN_TXUID +
            ".*?productName=(?<PRODUCTNAME>.*?)\\sproductVersion=(?<PRODUCTVERSION>.*?)\\s" +
            "jndiName=(?<JNDINAME>java:[\\w/]+)";

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
            service.enlistResourceManager(matcher.group(TXUID),  matcher.group("JNDINAME"), matcher.group("PRODUCTNAME"),
                    matcher.group("PRODUCTNAME"), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
