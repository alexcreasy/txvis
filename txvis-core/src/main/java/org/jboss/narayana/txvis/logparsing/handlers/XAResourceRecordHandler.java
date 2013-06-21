package org.jboss.narayana.txvis.logparsing.handlers;

import java.sql.Timestamp;
import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class XAResourceRecordHandler extends JbossAS8Handler {
    /**
     *
     */
    public static final String REGEX = "XAResourceRecord\\.(?<RECORDACTION>XAResourceRecord|topLevelPrepare).*?" +
            "tx_uid="+PATTERN_TXUID+".*?productName=(?<PRODUCTNAME>.*?)\\sproductVersion=(?<PRODUCTVERSION>.*?)\\s" +
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
        final String txuid = matcher.group(TXUID);
        final Timestamp timestamp = parseTimestamp(matcher.group(TIMESTAMP));
        final String jndiName = matcher.group("JNDINAME");
        final String productName = matcher.group("PRODUCTNAME");
        final String productVer = matcher.group("PRODUCTVERSION");

        switch (matcher.group("RECORDACTION")) {
            case "XAResourceRecord":
                service.enlistResourceManager(txuid, jndiName, productName, productVer, timestamp);
                break;
            case "topLevelPrepare":
                break;
        }
    }
}
