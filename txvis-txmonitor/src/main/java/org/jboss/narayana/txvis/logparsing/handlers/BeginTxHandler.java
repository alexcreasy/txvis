package org.jboss.narayana.txvis.logparsing.handlers;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/04/2013
 * Time: 23:50
 */
public final class BeginTxHandler extends AbstractHandler {

    /**
     * RegEx pattern for detecting creation of a transaction
     * RegEx Groups:
     * 0: The whole matched portion of the log entry
     * 1: The Transaction ID
     */
    public static final String REGEX = "BasicAction::Begin\\(\\)\\sfor\\saction-id\\s(" + TX_ID + ")";

    public BeginTxHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        dao.create(matcher.group(1));
    }
}
