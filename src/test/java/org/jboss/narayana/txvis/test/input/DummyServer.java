package org.jboss.narayana.txvis.test.input;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 18/04/2013
 * Time: 17:12
 */
public class DummyServer implements Runnable {

    private File file;
    private long timeInMilis;
    private static String[] messages;
    private long linesWritten = 0;
    Random rand = new Random();

    static {
        messages = new String[8];
        messages[0] = "09:20:38,554 TRACE [com.arjuna.ats.arjuna] (pool-1-thread-1) BasicAction::Begin() for action-id 0:ffffac118223:61d0e901:515016c7:13";
        messages[1] = "0m09:20:38,558 TRACE [com.arjuna.ats.jta] (pool-1-thread-1) TransactionImple.enlistResource ( org.jboss.narayana.txvis.simple.DummyXAResource@47b24153 )";
        messages[2] = "0m09:20:38,563 TRACE [com.arjuna.ats.jta] (pool-1-thread-1) TransactionImple.enlistResource ( org.jboss.narayana.txvis.simple.DummyXAResource@1bb557c8 )";
        messages[3] = "09:20:38,565 TRACE [com.arjuna.ats.arjuna] (pool-1-thread-1) BasicAction::prepare () for action-id 0:ffffac118223:61d0e901:515016c7:13";
        messages[4] = "09:20:38,566 TRACE [com.arjuna.ats.jta] (pool-1-thread-1) XAResourceRecord.topLevelPrepare for XAResourceRecord < resource:org.jboss.narayana.txvis.simple.DummyXAResource@47b24153, txid:< formatId=131077, gtrid_length=29, bqual_length=36, tx_uid=0:ffffac118223:61d0e901:515016c7:13, node_name=1, branch_uid=0:ffffac118223:61d0e901:515016c7:14, subordinatenodename=null, eis_name=unknown eis name >, heuristic: TwoPhaseOutcome.FINISH_OK com.arjuna.ats.internal.jta.resources.arjunacore.XAResourceRecord@6e09388c >";
        messages[5] = "09:20:38,567 TRACE [com.arjuna.ats.jta] (pool-1-thread-1) XAResourceRecord.topLevelPrepare for XAResourceRecord < resource:org.jboss.narayana.txvis.simple.DummyXAResource@1bb557c8, txid:< formatId=131077, gtrid_length=29, bqual_length=36, tx_uid=0:ffffac118223:61d0e901:515016c7:13, node_name=1, branch_uid=0:ffffac118223:61d0e901:515016c7:18, subordinatenodename=null, eis_name=unknown eis name >, heuristic: TwoPhaseOutcome.FINISH_OK com.arjuna.ats.internal.jta.resources.arjunacore.XAResourceRecord@1f5fa7c4 >";
        messages[6] = "09:20:38,573 TRACE [com.arjuna.ats.arjuna] (pool-1-thread-1) BasicAction::doCommit (XAResourceRecord < resource:org.jboss.narayana.txvis.simple.DummyXAResource@47b24153, txid:< formatId=131077, gtrid_length=29, bqual_length=36, tx_uid=0:ffffac118223:61d0e901:515016c7:13, node_name=1, branch_uid=0:ffffac118223:61d0e901:515016c7:14, subordinatenodename=null, eis_name=unknown eis name >, heuristic: TwoPhaseOutcome.FINISH_OK com.arjuna.ats.internal.jta.resources.arjunacore.XAResourceRecord@6e09388c >)";
        messages[7] = "09:20:38,574 TRACE [com.arjuna.ats.jta] (pool-1-thread-1) XAResourceRecord.topLevelCommit for XAResourceRecord < resource:org.jboss.narayana.txvis.simple.DummyXAResource@47b24153, txid:< formatId=131077, gtrid_length=29, bqual_length=36, tx_uid=0:ffffac118223:61d0e901:515016c7:13, node_name=1, branch_uid=0:ffffac118223:61d0e901:515016c7:14, subordinatenodename=null, eis_name=unknown eis name >, heuristic: TwoPhaseOutcome.FINISH_OK com.arjuna.ats.internal.jta.resources.arjunacore.XAResourceRecord@6e09388c >";
    }

    public DummyServer(File file, long timeInMilis) {

        this.file = file;
        this.timeInMilis = timeInMilis;

    }

    @Override
    public void run() {

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            long endTime = System.currentTimeMillis() + this.timeInMilis;

            while(endTime > System.currentTimeMillis()) {
                bw.write(randomMessage());
                bw.newLine();
                linesWritten++;
                bw.flush();
                Thread.sleep(300);
            }
            linesWritten++;
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {}
    }

    public long getLinesWritten() {
        return linesWritten;
    }

    private String randomMessage() {
        return messages[rand.nextInt(messages.length)];
    }
}
