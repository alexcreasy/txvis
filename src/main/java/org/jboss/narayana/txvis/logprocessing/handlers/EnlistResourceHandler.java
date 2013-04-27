package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DAOFactory;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 15:53
 */
public final class EnlistResourceHandler extends AbstractHandler {

    /**
     * RegEx pattern for detecting a participant enlist
     *
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Thread ID
     * 2: The Participant ID
     */
    public static final String REGEX = "\\((" + THREAD_ID +
            ")\\)\\sTransactionImple.enlistResource\\s\\(\\s([^\\s\\)]+)\\s\\)";

    public EnlistResourceHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        String txID = ThreadDirectory.INSTANCE.lookupTxID(matcher.group(1));
        if (txID == null)
            throw new IllegalStateException("Could not match Transaction to Thread");
        DAOFactory.transaction().get(txID).addParticipant(DAOFactory.participant().get(matcher.group(2)));
    }
}
