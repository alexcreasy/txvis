package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.persistence.enums.Vote;

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
    public static final String REGEX =
            "XAResourceRecord.topLevelPrepare\\sfor\\sXAResourceRecord\\s<\\sresource:([^,]+).*tx_uid="
            + TX_ID_PATTERN + ",.*TwoPhaseOutcome.FINISH_OK";

    public ResourceVoteCommitHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        dao.setParticipantVote(matcher.group(TX_ID), matcher.group(1), Vote.COMMIT);
    }
}
