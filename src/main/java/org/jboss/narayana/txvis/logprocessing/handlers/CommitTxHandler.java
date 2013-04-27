package org.jboss.narayana.txvis.logprocessing.handlers;

import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.dataaccess.Status;
import org.jboss.narayana.txvis.dataaccess.TransactionDAO;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 13:50
 */
public final class CommitTxHandler extends AbstractHandler {

    /**
     * RegEx pattern for detecting a successful commit.
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Thread ID
     * 2: The Transaction ID
     * (pool-1-thread-1) FileSystemStore.remove_committed(0:ffffac118223:61d0e901:515016c7:13,
     */
    public static final String REGEX = "\\((" + THREAD_ID
            + ")\\)\\sFileSystemStore.remove_committed\\((" + TX_ID + "),";

    private TransactionDAO transactionDAO = DAOFactory.transaction();

    public CommitTxHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        transactionDAO.get(matcher.group(2)).setStatus(Status.COMMIT);
    }


}
