package org.jboss.narayana.txvis.test;

import com.arjuna.ats.jta.TransactionManager;
import org.apache.commons.io.input.Tailer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.Status;
import org.jboss.narayana.txvis.data.ParticipantDAO;
import org.jboss.narayana.txvis.data.Transaction;
import org.jboss.narayana.txvis.data.TransactionDAO;
import org.jboss.narayana.txvis.parser.LogParser;
import org.jboss.narayana.txvis.simple.DummyXAResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 18/04/2013
 * Time: 14:56
 */
@RunWith(Arquillian.class)
public class LiveParseTest {

    @Deployment
    public static WebArchive createDeployment() {
        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";

        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve("commons-io:commons-io:2.4")
                .withTransitivity().asFile();

        WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.narayana.txvis")
                .addAsLibraries(libs)
                .addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml")
                .addAsWebInfResource(new StringAsset("<web-app></web-app>"), "web.xml")
                .setManifest(new StringAsset(ManifestMF));

        return archive;
    }

    public static final String LOGFILE_PATH =
            "/Users/alex/Documents/workspace/jboss-as/build/target/jboss-as-8.0.0.Alpha1-SNAPSHOT/standalone/log/server.log";

    private static DummyXAResource dummyXAResource1 = new DummyXAResource("dummy1");

    private static DummyXAResource dummyXAResource2 = new DummyXAResource("dummy2");

    private static final int NO_OF_TX = 5;

    @Test
    public void liveParseTest() throws Exception {

//        Logger logger = Logger.getLogger("org.jboss.narayana.txvis");
//        logger.setLevel(Level.DEBUG);

        TransactionDAO txDAO = new TransactionDAO();
        ParticipantDAO ptDAO = new ParticipantDAO();

        Tailer tailer = null;
        try {
            tailer = new Tailer(new File(LOGFILE_PATH), new LogParser(txDAO, ptDAO), 500, true);
            Thread thread = new Thread(tailer);
            thread.start();
            Thread.sleep(100);

            for (int i = 0; i < NO_OF_TX; i++)
                createTx();

            Thread.sleep(5000);

            Assert.assertEquals("Incorrect number of transactions parsed", NO_OF_TX, txDAO.totalTx());

            for (Transaction tx : txDAO.getList()) {
                Assert.assertEquals("Did not parse the correct number of participants for Transaction: "
                        + tx.getTxId(), 2, tx.totalParticipants());

                Assert.assertEquals("Did not parse commits", Status.COMMIT, tx.getStatus());
            }
        } finally {
            tailer.stop();
        }
    }


    private void createTx() throws Exception {
        TransactionManager.transactionManager().begin();

        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource1);
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource2);

        TransactionManager.transactionManager().commit();
    }

}
