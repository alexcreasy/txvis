package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/06/2013
 * Time: 18:55
 */
public class ArjunaTransactionImpleHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "ArjunaTransactionImple::get_status\\sfor\\s"+PATTERN_TXUID+
            "\\sreturning\\sCosTransactions::StatusCommitted ";
            //"ArjunaTransactionImple::get_status for 0:ffff0597491d:-93ade6e:51cf13af:3b returning CosTransactions::StatusCommitted";

    public ArjunaTransactionImpleHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        service.commitTx2Phase(matcher.group(TXUID), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
