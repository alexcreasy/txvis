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
public class ResourceDrivenRollbackHandler extends AbstractHandler {

    /**
     * RegEx for parsing a resource driven rollback
     *
     * Group 0: Whole matched part of string
     * Group 1: Transaction ID
     * Group 2: Resource ID
     */
    public static final String REGEX = "tx_uid=(" + TX_ID + "),.*eis\\sname\\s>\\s\\(([^\\s^)]+)\\)\\sfailed\\swith\\sexception";

    public ResourceDrivenRollbackHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        DAOFactory.transaction().get(matcher.group(1)).setStatus(Status.ROLLBACK_RESOURCE);

    }
}
