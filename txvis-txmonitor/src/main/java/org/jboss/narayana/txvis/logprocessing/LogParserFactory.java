package org.jboss.narayana.txvis.logprocessing;

import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.logprocessing.handlers.Handler;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 26/04/2013
 * Time: 14:26
 */
public class LogParserFactory {

    private static final Logger logger = Logger.getLogger("org.jboss.narayana.txvis");
    private static List<Class> handlerClasses;

    /**
     *
     * @return a LogParser
     * @throws IllegalStateException
     */
    public static LogParser getInstance() throws IllegalStateException {
        if (handlerClasses == null)
            throw new IllegalStateException("LogParserFactory has not been initialized");

        LogParser logParser = new LogParser();

        for (Class c : handlerClasses) {
            try {
                logParser.addHandler((Handler) c.newInstance());

                if (logger.isInfoEnabled())
                    logger.info("Successfully loaded log handler: " + c);
            }
            catch (InstantiationException e) {
                logger.fatal("Unable to load log handler: " + c, e);
                throw new IllegalStateException(e);
            }
            catch (IllegalAccessException e) {
                logger.fatal("Unable to load log handler: " + c, e);
                throw new IllegalStateException(e);
            }
            catch (ClassCastException e) {
                logger.fatal("Unable to load log handler: " + c + " is not an instance of" + Handler.class, e);
                throw new IllegalStateException(c + " is not an instance of " + Handler.class, e);
            }
        }
        return logParser;
    }

    /**
     *
     * @param handlers
     * @throws IllegalStateException
     */
    public static void initialize(Collection<String> handlers) throws IllegalStateException {
        handlerClasses = new LinkedList<Class>();

        for (String s : handlers) {
            try {
                handlerClasses.add(Class.forName(s));
            }
            catch (Throwable t) {
                logger.fatal("Unable to load log handler class: " + s, t);
                throw new IllegalStateException("Unable to load log handler class: " + s, t);
            }
        }
    }
}
