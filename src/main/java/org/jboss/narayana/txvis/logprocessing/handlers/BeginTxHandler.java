package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.dataaccess.TransactionDAO;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 23:50
 */
public final class BeginTxHandler extends AbstractHandler {

    /**
     * RegEx pattern for detecting creation of a transaction
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Thread ID
     * 2: The Transaction ID
     */
    public static final String REGEX = "\\((" + THREAD_ID +
            ")\\)\\sBasicAction::Begin\\(\\)\\sfor\\saction-id\\s(" + TX_ID + ")";

    private TransactionDAO transactionDAO = DAOFactory.transaction();

    public BeginTxHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        transactionDAO.create(matcher.group(2));
        ThreadDirectory.INSTANCE.put(matcher.group(1), matcher.group(2));
    }
}
