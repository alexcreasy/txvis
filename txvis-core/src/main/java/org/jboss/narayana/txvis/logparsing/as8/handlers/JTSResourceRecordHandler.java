package org.jboss.narayana.txvis.logparsing.as8.handlers;

import org.jboss.narayana.txvis.persistence.enums.EventType;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 01/07/2013
 * Time: 22:25
 */
public class JTSResourceRecordHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "ExtendedResourceRecord::topLevel(?<ACTION>Prepare|Commit|Abort)\\(\\)\\sfor\\s"
            + PATTERN_RMUID;

    public JTSResourceRecordHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        switch (matcher.group("ACTION")) {
            case "Prepare":
                service.resourcePreparedJTS(matcher.group(RMUID), parseTimestamp(matcher.group(TIMESTAMP)));
                break;
            case "Commit":
                service.resourceCommitOrAbortJTS(matcher.group(RMUID), EventType.COMMIT, parseTimestamp(
                        matcher.group(TIMESTAMP)));
                break;
            case "Abort":
                service.resourceCommitOrAbortJTS(matcher.group(RMUID), EventType.ABORT, parseTimestamp(
                        matcher.group(TIMESTAMP)));
                break;
        }
    }
}
