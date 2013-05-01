package org.jboss.narayana.txvis;

import java.util.Arrays;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 01/05/2013
 * Time: 10:29
 */
public enum ConfigurationManager {

    INSTANCE;

    private String logfilePath;
    private int logfilePollInterval = -1;
    private String transactionDaoImplementationClass;
    private String resourceDaoImplementationClass;
    private List<String> logHandlers;

    public String getLogfilePath() {
        return logfilePath == null ? Configuration.LOGFILE_PATH : logfilePath;
    }

    public void setLogfilePath(String logfilePath) {
        this.logfilePath = logfilePath;
    }

    public int getLogfilePollInterval() {
        return logfilePollInterval < 0 ? Configuration.LOGFILE_POLL_INTERVAL : logfilePollInterval;
    }

    public void setLogfilePollInterval(int logfilePollInterval) {
        this.logfilePollInterval = logfilePollInterval;
    }

    public String getTransactionDaoImplementationClass() {
        return transactionDaoImplementationClass == null
                ? Configuration.TRANSACTION_DAO_IMPLEMENTATION_CLASS
                : transactionDaoImplementationClass;
    }

    public void setTransactionDaoImplementationClass(String transactionDaoImplementationClass) {
        this.transactionDaoImplementationClass = transactionDaoImplementationClass;
    }

    public String getResourceDaoImplementationClass() {
        return resourceDaoImplementationClass == null
                ? Configuration.RESOURCE_DAO_IMPLEMENTATION_CLASS
                : resourceDaoImplementationClass;
    }

    public void setResourceDaoImplementationClass(String resourceDaoImplementationClass) {
        this.resourceDaoImplementationClass = resourceDaoImplementationClass;
    }

    public List<String> getLogHandlers() {
        return logHandlers == null ? Arrays.asList(Configuration.LOG_HANDLERS) : logHandlers;
    }

    public void setLogHandlers(List<String> logHandlers) {
        this.logHandlers = logHandlers;
    }
}
