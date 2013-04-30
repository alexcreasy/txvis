package org.jboss.narayana.txvis.test;

import com.arjuna.ats.jta.TransactionManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.TransactionMonitor;
import org.jboss.narayana.txvis.dataaccess.*;
import org.jboss.narayana.txvis.test.utils.DummyXAResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.transaction.RollbackException;
import java.io.File;
import java.util.UUID;

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

                .setManifest(new StringAsset(ManifestMF));

        return archive;
    }

    private static final int NO_OF_TX = 1;
    private static final int NO_OF_PARTICIPANTS = 3;
    private static final int INTRO_DELAY = 500;
    private static final int OUTRO_DELAY = 3000;


   @Test
    public void clientDrivenCommitTest() throws Exception {
        TransactionMonitor txmon = new TransactionMonitor();

        setupTxMonitor(txmon, Status.COMMIT);

        Assert.assertEquals("Incorrect number of transactions parsed", NO_OF_TX, DAOFactory.transactionInstance().totalTx());

        for (Transaction tx : DAOFactory.transactionInstance().getAll()) {
            Assert.assertEquals("Did not parse the correct number of participants: txID="
                    + tx.getTxId(), NO_OF_PARTICIPANTS, tx.totalParticipants());

            Assert.assertEquals("Incorrect final transactions status: txID=" + tx.getTxId(),
                    Status.COMMIT, tx.getStatus());
        }
    }

    //@Test
    public void clientDrivenRollbackTest() throws Exception {
        TransactionMonitor transactionMonitor = new TransactionMonitor();

        try {
            transactionMonitor.start();
            Thread.sleep(INTRO_DELAY);

            for (int i = 0; i < NO_OF_TX; i++)
                createTx(NO_OF_PARTICIPANTS, Status.ROLLBACK_CLIENT);

            Thread.sleep(OUTRO_DELAY);

            Assert.assertEquals("Incorrect number of transactions parsed", NO_OF_TX,
                    DAOFactory.transactionInstance().totalTx());

            for (Transaction tx : DAOFactory.transactionInstance().getAll()) {
                Assert.assertEquals("Did not parse the correct number of participants txID="
                        + tx.getTxId(), NO_OF_PARTICIPANTS, tx.totalParticipants());

                Assert.assertEquals("Incorrect final transactions status for txID=" + tx.getTxId(),
                        Status.ROLLBACK_CLIENT, tx.getStatus());
            }
        } finally {
            transactionMonitor.stop();
        }
    }

    //@Test
    public void resourceDrivenRollbackTest() throws Exception {
        TransactionMonitor transactionMonitor = new TransactionMonitor();

        try {
            transactionMonitor.start();
            Thread.sleep(INTRO_DELAY);

            for (int i = 0; i < NO_OF_TX; i++)
                createTx(NO_OF_PARTICIPANTS, Status.ROLLBACK_RESOURCE);

            Thread.sleep(OUTRO_DELAY);
        }
        finally {
            transactionMonitor.stop();
        }
        Assert.assertEquals("Incorrect number of transactions parsed", NO_OF_TX,
                DAOFactory.transactionInstance().totalTx());

        for (Transaction tx : DAOFactory.transactionInstance().getAll()) {
            Assert.assertEquals("Did not parse the correct number of participants txID="
                    + tx.getTxId(), NO_OF_PARTICIPANTS, tx.totalParticipants());

            Assert.assertEquals("Incorrect final transactions status for txID=" + tx.getTxId(),
                    Status.ROLLBACK_RESOURCE, tx.getStatus());

            int abortCounter = 0;
            for (ParticipantRecord p : DAOFactory.transactionInstance().get(tx.getTxId()).getParticipants()) {
                if (Vote.ABORT.equals(p.getVote()))
                    abortCounter++;
            }

            Assert.assertEquals("Incorrect number of participant resources report having voted to abort for txID="
                    + tx.getTxId(), 1, abortCounter);
        }
    }




    private void setupTxMonitor(TransactionMonitor txmon, Status outcome) throws Exception {
        setupTxMonitor(txmon, INTRO_DELAY, OUTRO_DELAY, NO_OF_TX, NO_OF_PARTICIPANTS, outcome);
    }

    private void setupTxMonitor(TransactionMonitor txmon, int introSleepDelay, int outroSleepDelay,
                                int noOfTx, int noOfParticipantsPerTx, Status outcome) throws Exception {
        try {
            txmon.start();
            Thread.sleep(introSleepDelay);
            createTx(noOfTx, noOfParticipantsPerTx, outcome);
            Thread.sleep(outroSleepDelay);
        }
        finally {
            txmon.stop();
        }

    }

    private void createTx(int noOfTx, int noOfParticipantsPerTx, Status outcome) throws Exception {
        for (int i = 0; i < noOfTx; i++)
            createTx(noOfParticipantsPerTx, outcome);
    }


    private void createTx(int noOfParticipantsPerTx, Status outcome) throws Exception {
        TransactionManager.transactionManager().begin();

        if (outcome.equals(Status.ROLLBACK_RESOURCE)) {
            TransactionManager.transactionManager().getTransaction().enlistResource(
                    new DummyXAResource(UUID.randomUUID().toString(), false));
            noOfParticipantsPerTx--;
        }

        for (int i = 0; i < noOfParticipantsPerTx; i++)
            TransactionManager.transactionManager().getTransaction().enlistResource(
                    new DummyXAResource(UUID.randomUUID().toString()));

        if (outcome.equals(Status.ROLLBACK_CLIENT))
                TransactionManager.transactionManager().rollback();
        else
            try {
                TransactionManager.transactionManager().commit();
            }
            catch (RollbackException e) {}
    }
}
