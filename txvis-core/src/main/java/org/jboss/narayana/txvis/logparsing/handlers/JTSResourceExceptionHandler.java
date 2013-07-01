package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/06/2013
 * Time: 16:57
 */
public class JTSResourceExceptionHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "XAResourceRecord\\.(?<RECTYPE>prepare|commit|rollback)\\sexception\\sXAException\\." +
            "(?<XAEXCEPTION>[A-Z_]+).*?resource uid="+PATTERN_RMUID;

    public JTSResourceExceptionHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        switch (matcher.group("RECTYPE")) {
            case "prepare":
                service.resourceFailedToPrepareJTS(matcher.group(RMUID), matcher.group("XAEXCEPTION"),
                        parseTimestamp(matcher.group(TIMESTAMP)));
                break;
        }
    }
}
