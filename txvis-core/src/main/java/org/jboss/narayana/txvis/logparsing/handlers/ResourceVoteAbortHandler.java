package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.enums.Vote;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 20:34
 */
public class ResourceVoteAbortHandler extends AbstractHandler {

    public static final String REGEX =
            PATTERN_TIMESTAMP + ".*?prepare\\son.*?tx_uid=" + PATTERN_TXID +
            ".*?jndiName=(?<JNDINAME>java:[\\w/]+).*?failed\\swith\\sexception";


    /**
     * 16:59:02,755 WARN  [com.arjuna.ats.jta] (default task-1) ARJUNA016041: prepare on < formatId=131077, gtrid_length=29,
     * bqual_length=36, tx_uid=0:ffff0a0d4635:-1cf5996c:51ab6b94:5f, node_name=1, branch_uid=0:ffff0a0d4635:-1cf5996c:51ab6b94:60,
     * subordinatenodename=null, eis_name=unknown eis name > (XAResourceWrapperImpl@1727c298[xaResource=null pad=false
     * overrideRmValue=false productName=Dummy Product productVersion=1.0.0 jndiName=java:jboss/fakejndiname/fakeresource0])
     * failed with exception XAException.XAER_RMERR: javax.transaction.xa.XAException
     *
     *
     *
     */

    public ResourceVoteAbortHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        service.resourceVoteAbort(matcher.group(TXID), matcher.group("JNDINAME"),
                parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
