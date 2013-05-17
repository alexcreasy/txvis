package org.jboss.narayana.txvis.logparsing;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.Configuration;
import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.logparsing.handlers.Handler;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 14:26
 */
public class LogParserFactory {

    private static final Logger logger = Logger.getLogger("org.jboss.narayana.txvis");

    public static LogParser getInstance(DataAccessObject dao) throws NullPointerException, IllegalStateException {

        if (Configuration.LOG_HANDLERS.length == 0)
            throw new IllegalStateException("Cannot instantiate LogParser: Configuration.LOG_HANDLERS is empty");
        if (dao == null)
            throw new NullPointerException("DataAccessObject required");

        final LogParser logParser = new LogParser();

        // Instantiate Handler classes listed in Configuration utility class and
        // add them to the the LogParser
        for (Class c : Configuration.LOG_HANDLERS) {
            try {
                Handler h = (Handler) c.newInstance();
                h.injectDAO(dao);
                logParser.addHandler(h);

                if (logger.isInfoEnabled())
                    logger.info("Successfully loaded log handler: " + c);

            } catch (InstantiationException e) {
                logger.fatal("Unable to load log handler: " + c, e);
                throw new IllegalStateException(e);

            } catch (IllegalAccessException e) {
                logger.fatal("Unable to load log handler: " + c, e);
                throw new IllegalStateException(e);

            } catch (ClassCastException e) {
                logger.fatal("Unable to load log handler: " + c + " is not an instance of" + Handler.class, e);
                throw new IllegalStateException(c + " is not an instance of " + Handler.class, e);
            }
        }
        return logParser;
    }
}