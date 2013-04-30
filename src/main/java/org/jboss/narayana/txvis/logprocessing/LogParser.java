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

    LogParser() {}

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
                    logger.debug("Parser match: handler=" + handler.getClass()); //+ ", logparser=" + this + ", line=" + line);
                handler.handle(matcher, line);
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
}