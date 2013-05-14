package org.jboss.narayana.txvis;

import org.jboss.narayana.txvis.logparsing.handlers.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 15:56
 */
public final class Configuration {

    public static final String LOGFILE_PATH =
            System.getProperty("jboss.server.log.dir") + "/server.log";
            //"/Users/alex/Documents/workspace/jboss-as/build/target/jboss-as-8.0.0.Alpha1-SNAPSHOT/standalone2/log/server.log";

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
