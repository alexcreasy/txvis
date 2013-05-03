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

    public List<String> getLogHandlers() {
        return logHandlers == null ? Arrays.asList(Configuration.LOG_HANDLERS) : logHandlers;
    }

    public void setLogHandlers(List<String> logHandlers) {
        this.logHandlers = logHandlers;
    }
}
