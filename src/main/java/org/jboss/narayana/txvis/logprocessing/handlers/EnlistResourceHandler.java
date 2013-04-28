package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DAOFactory;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class EnlistResourceHandler extends AbstractHandler {

    // 21:27:27,594 TRACE [com.arjuna.ats.jta] (pool-1-thread-12) XAResourceRecord.XAResourceRecord ( < formatId=131077, gtrid_length=29, bqual_length=36, tx_uid=0:ffff05974e0a:4b1fa4d1:517c4836:c4e, node_name=1, branch_uid=0:ffff05974e0a:4b1fa4d1:517c4836:c4f, subordinatenodename=null, eis_name=unknown eis name >, org.jboss.narayana.txvis.simple.DummyXAResource@6cc06295 )

    /**
     * RegEx pattern for detecting a participant enlist
     *
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Transaction ID
     * 2: The Participant ID
     *
     * XAResourceRecord.XAResourceRecord.*tx_uid= ,.*eis name >,\s([^\s]+)\s\)
     */
    public static final String REGEX = "XAResourceRecord.XAResourceRecord.*tx_uid=(" + TX_ID + "),.*eis name >,\\s([^\\s]+)\\s\\)";

    public EnlistResourceHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        DAOFactory.transaction().get(matcher.group(1)).addParticipant(DAOFactory.participant().get(matcher.group(2)));
    }

}
