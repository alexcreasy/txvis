package org.jboss.narayana.txvis;

import org.jboss.narayana.txvis.logparsing.*;
import org.jboss.narayana.txvis.logparsing.as8.*;
import org.jboss.narayana.txvis.logparsing.as8.handlers.*;

import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 15:56
 */
public final class Configuration {
    /**
     *
     */
    public static final String LOGFILE_NAME = "server.log";
    /**
     *
     */
    public static final String LOGFILE_PATH = System.getProperty("jboss.server.log.dir") + File.separator  + LOGFILE_NAME;
    /**
     *
     */
    public static final int LOGFILE_POLL_INTERVAL = 50;
    /**
     *
     */
    public static final Class[] LOG_HANDLERS = new Class[] {
            BasicActionHandler.class,
            XAResourceRecordHandler.class,
            ResourcePrepareFailedHandler.class,
            XAResourceRecordTopLevelPrepareHandler.class,
            JTSResourceEnlistHandler.class,
            JTSResourceExceptionHandler.class,
            JTSResourcePrepareHandler.class,
            JTSIsDistributedHandler.class,
            ArjunaTransactionImpleHandler.class,
    };

    /**
     *
     */
    public static final String HANDLER_SERVICE_JNDI_NAME = "java:module/HandlerService";
}
