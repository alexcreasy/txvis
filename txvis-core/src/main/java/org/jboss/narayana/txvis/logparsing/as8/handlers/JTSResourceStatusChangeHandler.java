package org.jboss.narayana.txvis.logparsing.as8.handlers;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 08/08/2013
 * Time: 00:28
 */
public class JTSResourceStatusChangeHandler extends JbossAS8AbstractHandler {

    private static final String REGEX = "BasicAction::do(?<METHOD>Prepare|Commit|Abort)\\(\\)\\sresult\\sfor\\saction-id\\s\\("
            + PATTERN_TXUID+"\\)\\son\\srecord\\sid:\\s\\("+PATTERN_RMUID+"\\)\\sis\\s\\(TwoPhaseOutcome\\.(?<OUTCOME>[A-Z_]+)\\)"
            + "\\snode\\sid:\\s\\((?<NODE>[^\\)]+)\\)";


    //BasicAction::doCommit() result for action-id (0:ffff0a0d4129:-13cf8631:5202d6f8:1d) on record id:
    // (0:ffff0a0d4129:-13cf8631:5202d6f8:28) is (TwoPhaseOutcome.FINISH_OK) node id: (alpha)
    public JTSResourceStatusChangeHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {

    }
}
