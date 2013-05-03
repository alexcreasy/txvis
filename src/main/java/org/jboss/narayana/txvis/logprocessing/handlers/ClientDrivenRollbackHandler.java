package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.dataaccess.Status;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 29/04/2013
 * Time: 09:58
 */
public class ClientDrivenRollbackHandler extends AbstractHandler {

    /**
     * RegEx pattern for parsing a client driven rollback
     *
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Transaction ID
     */
    public static final String REGEX = "BasicAction::Abort\\(\\)\\sfor\\saction-id\\s(" + TX_ID + ")";

    public ClientDrivenRollbackHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        DAOFactory.getInstance().retrieve(matcher.group(1)).setStatus(Status.ROLLBACK_CLIENT);
    }
}
