package org.jboss.narayana.txvis.logparsing.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/06/2013
 * Time: 19:50
 */
public class JTSResourcePrepareFailedHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "XAResource\\sprepare\\sfailed\\son\\sresource.*?(?<BRANCHID>[0-9-]+)\\s>";

    /*

    19:25:10,761 WARN  [com.arjuna.ats.jtax] (RequestProcessor-5) ARJUNA024015: XAResource prepare failed on resource
    XAResourceWrapperImpl@6234eeed[xaResource=null pad=false overrideRmValue=false productName=Dummy Product productVersion=1.0.0 j
    ndiName=java:jboss/20b646f8/fakeJndiName3] for transaction
    < 131072, 35, 36, 0000000000-1-1-8417-126-38-62124311181-51-4455000-6983698286698249,
    353535353535353535353434-4952-91-3-2715938146116-16-990353535-341181041171211041178435 > with: XAException.XAER_RMERR:
    javax.transaction.xa.XAException


     */



    public JTSResourcePrepareFailedHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
        service.resourceFailedToPrepareJTS(matcher.group("BRANCHID"), matcher.group("XAEXCEPTION"),
                parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
