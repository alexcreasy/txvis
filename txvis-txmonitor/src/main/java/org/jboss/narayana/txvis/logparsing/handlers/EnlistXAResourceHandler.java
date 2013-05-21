package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class EnlistXAResourceHandler extends AbstractHandler {

    /**
     * RegEx pattern for parsing a participant resource enlist
     *
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Transaction ID
     * 2: The Resource ID
     */

    public static final String REGEX = "XAResourceRecord\\.XAResourceRecord.+tx_uid=(" + TX_ID
            + "),.+eis name[^>]+>,\\s.*?(" + XA_RESOURCE_ID + ")";


    /**
     *  "XAResourceRecord\.XAResourceRecord.+tx_uid=((?:-?[0-9a-fA-F^:]+:){4}-?[0-9a-fA-F]+),.+eis name[^>]+>,\s.*((?:[a-zA-Z0-9]+\.)+[a-zA-Z0-9]+@[0-9a-f]+)"
     */
    public EnlistXAResourceHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        dao.enlistParticipant(matcher.group(1), matcher.group(2));
    }

}
