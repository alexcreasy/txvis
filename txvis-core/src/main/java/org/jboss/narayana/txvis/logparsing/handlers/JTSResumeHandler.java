package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 23/06/2013
 * Time: 19:59
 */
public class JTSResumeHandler extends JbossAS8AbstractHandler {
    /**
     *
     */
    public static final String REGEX = "CurrentImple::resumeWrapper\\s\\(\\s"+PATTERN_TXUID;

    /**
     *
     */
    public JTSResumeHandler() {
        super(REGEX);
    }

    /**
     *
     * @param matcher
     * @param line
     */
    @Override
    public void handle(Matcher matcher, String line) {
        service.resumeTransaction(matcher.group(THREAD_ID), matcher.group(TXUID));
    }
}
