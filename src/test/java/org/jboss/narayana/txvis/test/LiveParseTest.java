package org.jboss.narayana.txvis.test;

import com.arjuna.ats.jta.TransactionManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.data.ParticipantDAO;
import org.jboss.narayana.txvis.data.TransactionDAO;
import org.jboss.narayana.txvis.input.JBossLogParser;
import org.jboss.narayana.txvis.input.LogFileReader;
import org.jboss.narayana.txvis.simple.DummyXAResource;
import org.jboss.narayana.txvis.test.input.DummyLogParser;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 18/04/2013
 * Time: 14:56
 */
@RunWith(Arquillian.class)
public class LiveParseTest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackages(true, "org.jboss.narayana.txvis");

        archive.delete(ArchivePaths.create("META-INF/MANIFEST.MF"));

        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";


        archive.setManifest(new StringAsset(ManifestMF));

        return archive;
    }

    public static final String LOGFILE_PATH =
            "/Users/alex/Documents/workspace/jboss-as/build/target/jboss-as-8.0.0.Alpha1-SNAPSHOT/standalone/log/server.log";

    public void clientDrivenCommitTest() throws Exception {

        TransactionDAO txDAO = new TransactionDAO();
        ParticipantDAO ptDAO = new ParticipantDAO();
        LogFileReader reader = new LogFileReader(LOGFILE_PATH, new JBossLogParser(txDAO, ptDAO), false);

        Thread thread = new Thread(reader);
        thread.start();

        TransactionManager.transactionManager().begin();

        DummyXAResource dummyXAResource1 = new DummyXAResource("dummy1");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource1);
        DummyXAResource dummyXAResource2 = new DummyXAResource("dummy2");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource2);

        TransactionManager.transactionManager().commit();

        assertEquals("Incorrect number of transactions detected", 2, txDAO.totalTx());
        reader.stop();
    }

    @Test
    public void logTailTest() throws Exception {
        DummyLogParser parser = new DummyLogParser();
        LogFileReader reader = new LogFileReader(LOGFILE_PATH, parser, false);

        Thread thread = new Thread(reader);
        thread.start();

        TransactionManager.transactionManager().begin();

        DummyXAResource dummyXAResource1 = new DummyXAResource("dummy1");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource1);
        DummyXAResource dummyXAResource2 = new DummyXAResource("dummy2");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource2);

        TransactionManager.transactionManager().commit();

        assertTrue("Not tailing log file", parser.getSize() > 0);

        reader.stop();
    }
}
