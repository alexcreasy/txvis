package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.logparsing.AbstractHandler;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 21/06/2013
 * Time: 15:47
 */
public abstract class JbossAS8AbstractHandler extends AbstractHandler {

    /**
     * The back reference group name used to retrieve the log4j
     * timestamp TIMESTAMP_PATTERN - retrieve the timestamp by
     * calling <code>matcher.group(TIMESTAMP)</code>
     */
    public static final String TIMESTAMP = "TIMESTAMP";
    /**
     *
     */
    public static final String LOG_LEVEL = "LOGLEVEL";

    /**
     *
     */
    public static final String LOG_CLASS = "LOGCLASS";

    /**
     *
     */
    public static final String THREAD_ID = "THREADID";


    /*
     ***********************************************************************************
     * The following private constants are used to form BASE_REGEX_PREFIX which is     *
     * prefixed to the implementation class' regex when using the one argument         *
     * constructor, or two argument with second argument - dontPrefix = true. These    *
     * are tightly coupled to JBoss' log4j output format and will need to be updated   *
     * should the format ever change.                                                  *
     ***********************************************************************************
     */

    /*
     *
     */
    private static final String PATTERN_TIMESTAMP = "(?<"+TIMESTAMP+">\\d{2}:\\d{2}:\\d{2},\\d{3})";
    /*
     *
     */
    private static final String PATTERN_LOG_LEVEL = "(?<"+LOG_LEVEL+">TRACE|DEBUG|INFO|WARN|ERROR|FATAL)";
    /*
     *
     */
    private static final String PATTERN_LOG_CLASS = "\\[(?<"+LOG_CLASS+">[^\\]]+)\\]";

    /*
     *
     */
    private static final String PATTERN_THREAD_ID = "\\((?<"+THREAD_ID+">[^\\)]+)\\)\\s";
    /*
     *
     */
    private static final String BASE_REGEX_PREFIX = "^"+PATTERN_TIMESTAMP+"\\s+"+PATTERN_LOG_LEVEL+"\\s+"+PATTERN_LOG_CLASS +
            "\\s+"+PATTERN_THREAD_ID+".*?";

    /*
     ***********************************************************************************
     */

    public JbossAS8AbstractHandler(String regex) {
        this(regex, false);
    }

    public JbossAS8AbstractHandler(String regex, boolean dontPrefix) {
        super(dontPrefix ? regex : (BASE_REGEX_PREFIX + regex));
    }

    /**
     * Parses a String time value in <code>HH:mm:ss,SSS</code> format.
     *
     * @param time a String containing the time to parse.
     * @return a <code>java.sql.Timestamp</code> representation of the given time.
     * @throws NullPointerException if time is null.
     * @throws IllegalArgumentException if time is not in the format <code>HH:mm:ss,SSS</code>
     */
    protected final Timestamp parseTimestamp(String time) throws NullPointerException, IllegalArgumentException {
        try {
            return new Timestamp(new SimpleDateFormat("HH:mm:ss,SSS").parse(time).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Illegal time format, must be HH:mm:ss,SSS");
        }
    }
}
