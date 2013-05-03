package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.dataaccess.Vote;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 17:45
 */
public class ResourceVoteCommitHandler extends AbstractHandler {

    /**
     * RegEx for parsing a resource vote commit
     *
     * Group 0: Whole matched part of string
     * Group 1: Resource ID
     * Group 2: Transaction ID
     */
    public static final String REGEX = "XAResourceRecord.topLevelPrepare\\sfor\\sXAResourceRecord\\s<\\sresource:([^,]+).*tx_uid=(" + TX_ID + "),.*TwoPhaseOutcome.FINISH_OK";

    public ResourceVoteCommitHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        DAOFactory.getInstance().getEnlistedParticipant(
                matcher.group(2), matcher.group(1)).setVote(Vote.COMMIT);
    }
}
