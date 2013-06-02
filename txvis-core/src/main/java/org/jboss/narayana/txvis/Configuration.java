package org.jboss.narayana.txvis;

import org.jboss.narayana.txvis.logparsing.handlers.*;

import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 15:56
 */
public final class Configuration {

    public static final String LOGFILE_NAME = "server.log";

    public static final String LOGFILE_PATH =
            System.getProperty("jboss.server.log.dir") + File.separator  + LOGFILE_NAME;

    public static final int LOGFILE_POLL_INTERVAL = 500;

    public static final Class[] LOG_HANDLERS = new Class[] {
            BasicActionHandler.class,
            XAResourceRecordHandler.class,
            ResourceVoteAbortHandler.class,
            ResourceVoteCommitHandler.class,
            CommitTxHandler.class,
            ReaperHandler.class,
    };

    public static final String PERSISTENCE_CONTEXT = "org.jboss.narayana.txvis";
}
