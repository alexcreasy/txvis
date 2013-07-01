package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/06/2013
 * Time: 16:57
 */
public class JTSXAResourceRecordHandler extends JbossAS8AbstractHandler {
    /*
    XAResourceRecord.XAResourceRecord ( < 131072, 35, 36, 0000000000-1-1-8417-126-38-7936695181-5210414007783698286698249,
            353535353535353535353434-4952-91-3-447110486116-1713949353542421181041171211041178435 > )
     */
    private static final String REGEX = "XAResourceRecord\\.(?<RECTYPE>XAResourceRecord|prepare|commit|rollback).*?" +
            "(?<BRANCHID>[\\d-]+)\\s>";

    public JTSXAResourceRecordHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        switch (matcher.group("RECTYPE")) {
            case "XAResourceRecord":
                service.registerBranchId(matcher.group(THREAD_ID), matcher.group("BRANCHID"));
                break;
            case "prepare":
                service.resourcePreparedJTS(matcher.group("BRANCHID"), parseTimestamp(matcher.group(TIMESTAMP)));
                break;
            case "rollback":
                service.resourceFailedToPrepareJTS(matcher.group("BRANCHID"), "", parseTimestamp(matcher.group(TIMESTAMP)));
                break;
        }
    }
}
