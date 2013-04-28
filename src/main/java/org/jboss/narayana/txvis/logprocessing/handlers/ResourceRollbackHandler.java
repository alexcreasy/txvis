package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.dataaccess.Status;
import org.jboss.narayana.txvis.dataaccess.Vote;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 20:34
 */
public class ResourceRollbackHandler extends AbstractHandler {
    /*
     *
     * 20:28:03,903 WARN  [com.arjuna.ats.jta] (pool-1-thread-1) ARJUNA016041: prepare on < formatId=131077, gtrid_length=29, bqual_length=36, tx_uid=0:ffff05974e0a:7540f300:517c26a8:1c, node_name=1, branch_uid=0:ffff05974e0a:7540f300:517c26a8:20, subordinatenodename=null, eis_name=unknown eis name > (org.jboss.narayana.txvis.simple.DummyXAResource@16ce65f6) failed with exception XAException.XAER_RMERR: javax.transaction.xa.XAException
     *
     *
     *
     */

    /**
     *
     *
     * Group 0: Whole matched part of string
     * Group 1: Transaction ID
     * Group 2: Resource ID
     */
    public static final String REGEX = "tx_uid=(" + TX_ID + "),.*eis\\sname\\s>\\s\\(([^\\s^)]+)\\)\\sfailed\\swith\\sexception";

    public ResourceRollbackHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        DAOFactory.transaction().get(matcher.group(1)).setStatus(Status.ROLLBACK_RESOURCE);

    }
}
