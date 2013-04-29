package org.jboss.narayana.txvis;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 15:56
 */
public final class Configuration {


    public static final String LOGFILE_PATH =
            "/Users/alex/Documents/workspace/jboss-as/build/target/jboss-as-8.0.0.Alpha1-SNAPSHOT/standalone/log/server.log";

    public static final int LOGFILE_POLL_INTERVAL = 500;

    public static final String TRANSACTION_DAO_IMPLEMENTATION_CLASS
            = "org.jboss.narayana.txvis.dataaccess.TransactionDAOInMemoryImpl";
    public static final String RESOURCE_DAO_IMPLEMENTATION_CLASS
            = "org.jboss.narayana.txvis.dataaccess.ResourceDAOInMemoryImpl";

    private static final String[] LOG_HANDLERS = new String[] {
            "org.jboss.narayana.txvis.logprocessing.handlers.BeginTxHandler",
            "org.jboss.narayana.txvis.logprocessing.handlers.EnlistResourceHandler",
            "org.jboss.narayana.txvis.logprocessing.handlers.CommitTxHandler",
            "org.jboss.narayana.txvis.logprocessing.handlers.ClientDrivenRollbackHandler",
            "org.jboss.narayana.txvis.logprocessing.handlers.ResourceVoteAbortHandler",
            "org.jboss.narayana.txvis.logprocessing.handlers.ResourceDrivenRollbackHandler",

    };








    public static String[] getHandlers() { return LOG_HANDLERS; }
}
