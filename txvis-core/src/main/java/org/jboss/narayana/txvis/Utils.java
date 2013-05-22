package org.jboss.narayana.txvis;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 22/05/2013
 * Time: 23:47
 */
public final class Utils {

    private Utils() {}


    public static Timestamp parseTimestamp(String dateTime) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateTime.substring(0, 2)));
        c.set(Calendar.MINUTE, Integer.parseInt(dateTime.substring(3, 5)));
        c.set(Calendar.SECOND, Integer.parseInt(dateTime.substring(7, 9)));
        c.set(Calendar.MILLISECOND, Integer.parseInt(dateTime.substring(9)));

        return new Timestamp(c.getTimeInMillis());
    }
}
