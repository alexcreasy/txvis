package org.jboss.narayana.txvis;

import org.jboss.narayana.txvis.data.ParticipantDAO;
import org.jboss.narayana.txvis.data.TransactionDAO;
import org.jboss.narayana.txvis.input.JBossLogParser;
import org.jboss.narayana.txvis.input.LogFileReader;
import org.jboss.narayana.txvis.input.LogParser;

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
    private static LogFileReader reader;
    private static LogParser parser;



    public static void main(String[] args) throws Exception {

        reader = new LogFileReader(LOGFILE_PATH, new JBossLogParser(txDAO, ptDAO), true);

        Thread thread = new Thread(reader);

        thread.start();
        Thread.sleep(10000);
        reader.stop();

        System.out.println("\nRESULTS\n");
        txDAO.printAll();
    }
}
