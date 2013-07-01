package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 01/07/2013
 * Time: 22:25
 */
public class JTSResourcePrepareHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "ExtendedResourceRecord::topLevelPrepare\\(\\)\\sfor\\s"+PATTERN_RMUID;

    public JTSResourcePrepareHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        service.resourcePreparedJTS(matcher.group(RMUID), parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
