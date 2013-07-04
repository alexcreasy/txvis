package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 02/07/2013
 * Time: 02:45
 */
public class JTSIsDistributedHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "ArjunaTransactionImple::register_resource\\sfor\\s"+PATTERN_TXUID +
            "\\s-\\ssubtransaction\\saware\\sresource:\\sYES";

    public JTSIsDistributedHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        //service.isDistributed(matcher.group(TXUID));
    }
}
