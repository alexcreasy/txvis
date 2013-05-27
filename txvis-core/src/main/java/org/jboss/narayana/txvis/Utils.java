package org.jboss.narayana.txvis;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.logparsing.handlers.Handler;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/05/2013
 * Time: 23:47
 */
public final class Utils {

    private Utils() {}
    private static final Logger logger = Logger.getLogger(Utils.class.getName());


    public static Timestamp parseTimestamp(String dateTime) {
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

        if (logger.isTraceEnabled())
            logger.trace(MessageFormat.format("Utils.parseTimeStamp / hour={0}, minute={1}, second={2}, millis={3},",
                    hour, minute, second, millis));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, millis);

        return new Timestamp(c.getTimeInMillis());
    }
}
