package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.Utils;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class XAResourceRecordHandler extends AbstractHandler {

    public static final String REGEX =
            PATTERN_TIMESTAMP + ".*?XAResourceRecord\\.(?<RECORDACTION>XAResourceRecord|topLevelPrepare).+?tx_uid="
            + PATTERN_TXID + ".*?(?<XARESOURCERECORDID>XAResourceRecord@[a-f0-9]+)";


    public XAResourceRecordHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {

        switch (matcher.group("RECORDACTION")) {
            case "XAResourceRecord":
                dao.createParticipantRecord(matcher.group(TXID),
                        matcher.group("XARESOURCERECORDID"),
                        Utils.parseTimestamp(matcher.group(TIMESTAMP)));
                break;
            case "topLevelPrepare":
                dao.setParticipantVote(matcher.group(TXID),
                        matcher.group("XARESOURCERECORDID"), Vote.COMMIT);
                break;
        }
    }
}
