package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.dataaccess.Status;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 17:37
 */
public class ResourceDrivenRollbackHandler extends AbstractHandler {

   // 20:28:03,915 TRACE [com.arjuna.ats.arjuna] (pool-1-thread-1) BasicAction::phase2Abort() for action-id 0:ffff05974e0a:7540f300:517c26a8:1c
    /**
     * RegEx for parsing an resource abort vote
     *
     * Group 0: Whole matched part of string
     * Group 1: Transaction ID
     */
    public static final String REGEX = "BasicAction::phase2Abort\\(\\)\\sfor\\saction-id\\s(" + TX_ID + ")";



    public ResourceDrivenRollbackHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        DAOFactory.getInstance().retrieve(matcher.group(1)).setStatus(Status.ROLLBACK_RESOURCE);
    }
}
