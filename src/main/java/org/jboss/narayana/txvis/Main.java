package org.jboss.narayana.txvis;

import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 15/04/2013
 * Time: 16:32
 */
public class Main {

    public static final String LOGFILE_PATH =
            "/Users/alex/Documents/workspace/jboss-as/build/target/jboss-as-8.0.0.Alpha1-SNAPSHOT/standalone/log/server.log";

    public static void main(String[] args) {

        LogReader lp = null;

        try {
            lp = LogReader.getInstance(LOGFILE_PATH);
        } catch (NoSuchFileException e) {
            e.printStackTrace();
            System.exit(1);
        }

        List<Transaction> txList = lp.getTx();

        System.out.println("Found " + txList.size() + " transactions:");

        for (Transaction tx : txList) {
            System.out.println(tx);
        }
    }
}
