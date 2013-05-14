package org.jboss.narayana.txvis;

import org.jboss.narayana.txvis.logparsing.handlers.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 15:56
 */
public final class Configuration {

    public static final String LOGFILE_NAME = "server.log";

    //TODO: Must modify using File class to make platform neutral
    public static final String LOGFILE_PATH = System.getProperty("jboss.server.log.dir") + "/"  + LOGFILE_NAME;

    public static final int LOGFILE_POLL_INTERVAL = 500;

    public static final Class[] LOG_HANDLERS = new Class[] {
            EnlistResourceHandler.class,
            ResourceVoteAbortHandler.class,
            ResourceVoteCommitHandler.class,
            BeginTxHandler.class,
            CommitTxHandler.class,
            ClientDrivenRollbackHandler.class,
            ResourceDrivenRollbackHandler.class,
    };
}
