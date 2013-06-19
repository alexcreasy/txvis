package org.jboss.narayana.txvis.logparsing.handlers;

import java.sql.Timestamp;
import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class XAResourceRecordHandler extends AbstractHandler {

    public static final String REGEX =
            PATTERN_TIMESTAMP +
            ".*?XAResourceRecord\\.(?<RECORDACTION>XAResourceRecord|topLevelPrepare).*?tx_uid="
            + PATTERN_TXID +
            ".*?productName=(?<PRODUCTNAME>.*?)\\sproductVersion=(?<PRODUCTVERSION>.*?)\\sjndiName=(?<JNDINAME>java:[\\w/]+)";


    public XAResourceRecordHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        final String txuid = matcher.group(TXID);
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
