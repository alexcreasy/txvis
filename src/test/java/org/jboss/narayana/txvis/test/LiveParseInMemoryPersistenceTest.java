package org.jboss.narayana.txvis.test;

import com.arjuna.ats.jta.TransactionManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.ConfigurationManager;
import org.jboss.narayana.txvis.dataaccess.*;
import org.jboss.narayana.txvis.test.utils.DummyXAResource;
import org.jboss.narayana.txvis.test.utils.LiveTestMockTransactionMonitor;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.*;
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
public class LiveParseInMemoryPersistenceTest {

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

    private static final int NO_OF_TX = 2;
    private static final int NO_OF_PARTICIPANTS = 3;
    private static final int INTRO_DELAY = 500;
    private static final int OUTRO_DELAY = 3000;
    private static final int READY_POLL_INTERVAL = 200;

    private LiveTestMockTransactionMonitor txmon;
    private TransactionDAO transactionDAO;
    private ResourceDAO resourceDAO;

    @Before
    public void setup() throws Exception {
        ConfigurationManager.INSTANCE.setResourceDaoImplementationClass(
                "org.jboss.narayana.txvis.dataaccess.ResourceDAOInMemoryImpl");
        ConfigurationManager.INSTANCE.setTransactionDaoImplementationClass(
                "org.jboss.narayana.txvis.dataaccess.TransactionDAOInMemoryImpl");

        txmon = new LiveTestMockTransactionMonitor();
        transactionDAO = DAOFactory.transactionInstance();
        resourceDAO = DAOFactory.resourceInstance();
    }

    @After
    public void tearDown() throws Exception {
        DAOFactory.shutdown();
    }

    @Test
    public void clientDrivenCommitTest() throws Exception {
        testBootstrap(Status.COMMIT);

        Assert.assertEquals("Incorrect number of transactions parsed", NO_OF_TX, this.transactionDAO.totalTx());

        for (Transaction tx : this.transactionDAO.getAll()) {
            Assert.assertEquals("Did not parse the correct number of participants: txID="
                    + tx.getTransactionID(), NO_OF_PARTICIPANTS, tx.totalParticipants());

            Assert.assertEquals("Incorrect final transaction status: txID=" + tx.getTransactionID(),
                    Status.COMMIT, tx.getStatus());

            int commits = 0;
            for (ParticipantRecord p : tx.getParticipants()) {
                if (Vote.COMMIT.equals(p.getVote()))
                    commits++;
            }
            Assert.assertEquals("Incorrect number of participant resources report having voted to commit for txID="
                    + tx.getTransactionID(), NO_OF_PARTICIPANTS, commits);
        }
    }

    @Test
    public void clientDrivenRollbackTest() throws Exception {
        testBootstrap(Status.ROLLBACK_CLIENT);

        Assert.assertEquals("Incorrect number of transactions parsed", NO_OF_TX,
                transactionDAO.totalTx());

            for (Transaction tx : transactionDAO.getAll()) {
                Assert.assertEquals("Did not parse the correct number of participants txID="
                        + tx.getTransactionID(), NO_OF_PARTICIPANTS, tx.totalParticipants());

                Assert.assertEquals("Incorrect final transaction status for txID=" + tx.getTransactionID(),
                        Status.ROLLBACK_CLIENT, tx.getStatus());
            }
    }

    @Test
    public void resourceDrivenRollbackTest() throws Exception {
        testBootstrap(Status.ROLLBACK_RESOURCE);

        Assert.assertEquals("Incorrect number of transactions parsed", NO_OF_TX,
                transactionDAO.totalTx());

        for (Transaction tx : transactionDAO.getAll()) {
            Assert.assertEquals("Did not parse the correct number of participants txID="
                    + tx.getTransactionID(), NO_OF_PARTICIPANTS, tx.totalParticipants());

            Assert.assertEquals("Incorrect final transaction status for txID=" + tx.getTransactionID(),
                    Status.ROLLBACK_RESOURCE, tx.getStatus());

            int aborts = 0;
            for (ParticipantRecord p : tx.getParticipants()) {
                if (Vote.ABORT.equals(p.getVote()))
                    aborts++;
            }
            Assert.assertEquals("Incorrect number of participant resources report having voted to abort for txID="
                    + tx.getTransactionID(), 1, aborts);
        }
    }

    private void testBootstrap(Status outcome) throws Exception {
        testBootstrap(txmon, INTRO_DELAY, OUTRO_DELAY, NO_OF_TX, NO_OF_PARTICIPANTS, outcome);
    }

    private void testBootstrap(LiveTestMockTransactionMonitor txmon, int introSleepDelay, int outroSleepDelay,
                               int noOfTx, int noOfParticipantsPerTx, Status outcome) throws Exception {
        try {
            txmon.start();
            while(!txmon.isReady()) {
                System.out.println("Polling for test start");
                Thread.sleep(READY_POLL_INTERVAL);
            }
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
