package org.jboss.narayana.txvis.logprocessing;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.log4j.Logger;
import org.jboss.narayana.txvis.logprocessing.handlers.Handler;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 16:49
 */
public final class LogParser implements TailerListener {

    private static final Logger logger = Logger.getLogger("org.jboss.narayana.txvis");
    private final List<Handler> handlers = new LinkedList<Handler>();
    private Tailer tailer;

    public void addHandler(Handler lineHandler) throws NullPointerException {
        if (lineHandler == null)
            throw new NullPointerException();
        handlers.add(lineHandler);
    }

    public void handle(String line) {
        for (Handler handler : handlers) {
            Matcher matcher = handler.getPattern().matcher(line);
            if (matcher.find()) {

                if (logger.isDebugEnabled())
                    logger.debug(logFormat(handler, matcher));

                handler.handle(matcher, line);
                break;
            }
        }
    }

    public void init(Tailer tailer) {
        this.tailer = tailer;
    }

    public void fileNotFound() {
        logger.fatal("Log file not found: " + tailer.getFile());
    }

    public void fileRotated() {}

    public void handle(Exception ex) {
        logger.error("Exception thrown while parsing logfile", ex);
    }




    private String logFormat(Handler handler, Matcher matcher) {
        StringBuilder sb =
                new StringBuilder("Parser match: handler=").append(handler.getClass().getName());

        for (int i = 1; i <= matcher.groupCount(); i++)
            sb.append(", matcher.group(").append(i).append(")=").append(matcher.group(i));

        return sb.toString();
    }
}