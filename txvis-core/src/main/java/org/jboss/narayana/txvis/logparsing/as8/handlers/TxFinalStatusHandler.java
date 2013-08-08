package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 08/08/2013
 * Time: 00:51
 */
public class TxFinalStatusHandler extends JbossAS8AbstractHandler {

    //private static final String REGEX = "BasicAction::End\\(\\)\\sresult\\sfor\\saction-id\\s\\("+PATTERN_TXUID+"\\)\\sis\\s\\("
    //        + "ActionStatus\\.(?<RESULT>[A-Z_]+)\\)\\snode\\sid:\\s\\((?<NODE>[^\\)]+)\\)";


    private static final String REGEX = "BasicAction::End\\(\\)\\sresult\\sfor\\saction-id\\s\\(" + PATTERN_TXUID
            + "\\)\\sis\\s\\(ActionStatus\\.(?<RESULT>[A-Z_]+)\\)\\snode\\sid:\\s\\((?<NODE>[^\\)]+)\\)";

    //BasicAction::End\(\) result for action-id \(\) is \(ActionStatus\.\) node id: \(\)

    public TxFinalStatusHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {
    }
}
