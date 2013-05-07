package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DAOFactory;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class EnlistResourceHandler extends AbstractHandler {

    /**
     * RegEx pattern for parsing a participant resource enlist
     *
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Transaction ID
     * 2: The Resource ID
     */
    public static final String REGEX = "XAResourceRecord.XAResourceRecord.*tx_uid=(" + TX_ID + "),.*eis name >,\\s([^\\s]+)\\s\\)";

    public EnlistResourceHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        DAOFactory.getInstance().enlistParticipant(matcher.group(1), matcher.group(2));
    }

}
