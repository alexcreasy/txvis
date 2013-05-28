package org.jboss.narayana.txvis.logparsing;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.Configuration;
import org.jboss.narayana.txvis.logparsing.handlers.AbstractHandler;
import org.jboss.narayana.txvis.persistence.DataAccessObject;
import org.jboss.narayana.txvis.logparsing.handlers.Handler;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 14:26
 */
public class LogParserFactory {

    private static final Logger logger = Logger.getLogger("org.jboss.narayana.txvis");

    public static LogParser getInstance(DataAccessObject dao)
            throws NullPointerException, IllegalStateException {

        if (Configuration.LOG_HANDLERS.length == 0)
            throw new IllegalStateException("Cannot instantiate LogParser: Configuration.LOG_HANDLERS is empty");

        if (dao == null)
            throw new NullPointerException("Method called with null parameter: dao");

        final LogParser logParser = new LogParser();

        // Instantiate Handler classes listed in Configuration utility class and
        // add them to the the LogParser
        for (Class c : Configuration.LOG_HANDLERS) {
            try {
                AbstractHandler h = (AbstractHandler) c.newInstance();
                h.injectDAO(dao);
                logParser.addHandler(h);

                if (logger.isInfoEnabled())
                    logger.info("Successfully loaded log handler: " + c);

            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                logger.fatal(MessageFormat.format("Unable to load log handler: {0}", c), e);
                throw new IllegalStateException(e);
            }
        }
        return logParser;
    }
}