package org.jboss.narayana.txvis;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.jboss.narayana.txvis.data.ParticipantDAO;
import org.jboss.narayana.txvis.data.TransactionDAO;
import org.jboss.narayana.txvis.input.JBossLogParser;

import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 16:32
 */
public class Main {

    public static final String LOGFILE_PATH =
            "/Users/alex/Documents/workspace/jboss-as/build/target/jboss-as-8.0.0.Alpha1-SNAPSHOT/standalone/log/server.log";

    private static final TransactionDAO txDAO = new TransactionDAO();
    private static final ParticipantDAO ptDAO = new ParticipantDAO();

    public static void main(String[] args) throws Exception {

        File file = new File(LOGFILE_PATH);
        TailerListener parser = new JBossLogParser(txDAO, ptDAO);

        Tailer tailer = new Tailer(file, parser, 1000, false);

        Thread thread = new Thread(tailer);

        thread.start();
        Thread.sleep(10000);
        tailer.stop();

        System.out.println("\nRESULTS\n");
        txDAO.printAll();
    }
}
