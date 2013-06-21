package org.jboss.narayana.txvis.logparsing.handlers;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.HandlerService;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 27/04/2013
 * Time: 13:56
 */
public abstract class AbstractHandler implements Handler {

    protected static final Logger logger = Logger.getLogger(Handler.class.getName());

    /*
     * Constants can be used as building blocks for regular expressions in implementation classes.
     */

    /**
     * The back reference group name used to retrieve the log4j
     * timestamp TIMESTAMP_PATTERN - retrieve the timestamp by
     * calling <code>matcher.group(TIMESTAMP)</code>
     */
    public static final String TIMESTAMP = "TIMESTAMP";
    /**
     *
     */
    public static final String PATTERN_TIMESTAMP = "(?<"+TIMESTAMP+">\\d{2}:\\d{2}:\\d{2},\\d{3})";
    /**
     *
     */
    public static final String TXUID = "TXUID";
    /**
     *
     */
    public static final String PATTERN_TXUID = "(?<"+TXUID+">(?:-?[0-9a-f]+:){4}-?[0-9a-f]+)";
    /**
     *
     */
    public static final String LOG_LEVEL = "LOGLEVEL";
    /**
     *
     */
    public static final String PATTERN_LOG_LEVEL = "(?<"+LOG_LEVEL+">TRACE|DEBUG|INFO|WARN|ERROR|FATAL)";
    /**
     *
     */
    public static final String LOG_CLASS = "LOGCLASS";
    /**
     *
     */
    public static final String PATTERN_LOG_CLASS = "\\[(?<"+LOG_CLASS+">[^\\]]+)\\]";
    /**
     *
     */
    public static final String THREAD_ID = "THREADID";
    /**
     *
     */
    public static final String PATTERN_THREAD_ID = "\\((?<"+THREAD_ID+">[^\\)]+)\\)\\s";

    public static final String BASE_REGEX = "^"+PATTERN_TIMESTAMP+"\\s+"+PATTERN_LOG_LEVEL+"\\s+"+PATTERN_LOG_CLASS+"\\s+" +
            PATTERN_THREAD_ID+".*?";

    //
    private final Pattern pattern;
    //
    protected HandlerService service;

    /**
     *
     * @param regex
     * @throws PatternSyntaxException
     */
    public AbstractHandler(String regex) throws PatternSyntaxException {
        this(regex, false);
    }

    public AbstractHandler(String regex, boolean dontAppend) {
        this.pattern = Pattern.compile(dontAppend ? regex : BASE_REGEX + regex);
    }

    /**
     *
     * @return
     */
    @Override
    public final Pattern getPattern() {
        return this.pattern;
    }

    /**
     *
     * @param service
     * @throws NullPointerException
     */
    public void injectService(HandlerService service) throws NullPointerException {
        if (service == null)
            throw new NullPointerException("Method called with null parameter: service");
        this.service = service;
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