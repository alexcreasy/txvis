package org.jboss.narayana.txvis;

import java.nio.file.NoSuchFileException;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 16:32
 */
public class Main {

    public static final String LOGFILE_PATH =
            "/Users/alex/Documents/workspace/jboss-as/build/target/jboss-as-8.0.0.Alpha1-SNAPSHOT/standalone/log/server.log";

    private static final TransactionDAO txBean = new TransactionDAO();
    private static final ParticipantDAO PARTICIPANT_DAO = new ParticipantDAO();
    private static LogParser parser;



    public static void main(String[] args) {

        try {
            parser = LogParser.getInstance(LOGFILE_PATH, txBean, PARTICIPANT_DAO);
        } catch (NoSuchFileException e) {
            e.printStackTrace();
            System.exit(1);
        }
        parser.run();
        System.out.println("\nRESULTS\n");
        txBean.printAll();
        System.out.println("\nTHREADS\n");
        parser.printThreadList();
    }
}
