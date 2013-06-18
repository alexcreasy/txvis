package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 17:45
 */
public class ResourceVoteCommitHandler extends AbstractHandler {
    // ^(\d{2}:\d{2}:\d{2},\d{3}).*?XAResourceRecord\.(XAResourceRecord|topLevelPrepare).*?tx_uid=((?:-?[0-9a-f]+:){4}-?[0-9a-f]+).*?productName=(.*?)\sproductVersion=(.*?)\sjndiName=(java:[\w/]+)

    //16:58:59,070 TRACE [com.arjuna.ats.jta] (default task-6) XAResourceRecord.topLevelPrepare for XAResourceRecord < resource:XAResourceWrapperImpl@1d703d8e[xaResource=null pad=false overrideRmValue=false productName=Dummy Product productVersion=1.0.0 jndiName=java:jboss/fakejndiname/fakeresource2], txid:< formatId=131077, gtrid_length=29, bqual_length=36, tx_uid=0:ffff0a0d4635:-1cf5996c:51ab6b94:23, node_name=1, branch_uid=0:ffff0a0d4635:-1cf5996c:51ab6b94:2a, subordinatenodename=null, eis_name=unknown eis name >, heuristic: TwoPhaseOutcome.FINISH_OK com.arjuna.ats.internal.jta.resources.arjunacore.XAResourceRecord@344001c1 >
    public static final String REGEX =
            PATTERN_TIMESTAMP
            + ".*?XAResourceRecord.topLevelPrepare.*?jndiName=(?<JNDINAME>java:[\\w/]+).*?tx_uid="
            + PATTERN_TXID;

    public ResourceVoteCommitHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        service.resourceVoteCommit(matcher.group(TXID), matcher.group("JNDINAME"),
                parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
