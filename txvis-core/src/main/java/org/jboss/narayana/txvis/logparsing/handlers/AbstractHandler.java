package org.jboss.narayana.txvis.logparsing.handlers;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.persistence.HandlerService;

import java.sql.Timestamp;
import java.text.MessageFormat;
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
     * Constants can be used as building blocks
     * for regular expressions in implementation
     * classes.
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
    public static final String PATTERN_TIMESTAMP =
            "^(?<" + TIMESTAMP + ">\\d{2}:\\d{2}:\\d{2},\\d{3})";

    /**
     *
     */
    public static final String TXID = "TXID";

    /**
     *
     */
    public static final String PATTERN_TXID =
            "(?<" + TXID + ">(?:-?[0-9a-f]+:){4}-?[0-9a-f]+)";
    /**
     *
     */
    public static final String RESOURCEID = "XARESOURCEID";

    /**
     *
     */
    public static final String PATTERN_RESOURCEID =
            "(?<" + RESOURCEID + ">(?:\\w+\\.)+\\w+@[0-9a-f]+)";


    public static final String BASE_REGEX =
            "(?<TIMESTAMP>\\d{1,2}:\\d{1,2}:\\d{1,2},\\d{1,3})\\s(?<LEVEL>TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\\s\\[(?<LOGCLASS>[^\\]]+)\\]\\s\\((?<THREAD>[^\\)]+)\\)\\s";
     /*
      ***********************************************************
      */


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
        this.pattern = Pattern.compile(regex);
    }

    /**
     *
     * @param regex
     * @param flags
     * @throws PatternSyntaxException
     * @throws IllegalArgumentException
     */
    public AbstractHandler(String regex, int flags)
            throws PatternSyntaxException, IllegalArgumentException {
        this.pattern = Pattern.compile(regex, flags);
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


    /*
     *
     *
     */
    protected Timestamp parseTimestamp(String dateTime) {
        int hour = -1;
        int minute = -1;
        int second = -1;
        int millis = -1;

        try {
            hour = Integer.parseInt(dateTime.substring(0, 2));
            minute = Integer.parseInt(dateTime.substring(3, 5));
            second = Integer.parseInt(dateTime.substring(6, 8));
            millis = Integer.parseInt(dateTime.substring(9));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "Utils.parseTimeStamp could not parse: {0}  / tokens: hour={1}, minute={2}, second={3}, millis={4}",
                    dateTime, hour, minute, second, millis), e);
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, millis);

        return new Timestamp(c.getTimeInMillis());
    }
}