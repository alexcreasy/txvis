package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.dataaccess.*;
import org.jboss.narayana.txvis.test.utils.LiveTestMockTransactionMonitor;
import org.jboss.narayana.txvis.test.utils.TransactionUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 04/05/2013
 * Time: 14:41
 */
@RunWith(Arquillian.class)
public class BasicIntegrationTest {

    @Deployment
    public static WebArchive createDeployment() {
        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";

        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve("commons-io:commons-io:2.4")
                .withTransitivity().asFile();

        WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.narayana.txvis")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")), "classes/META-INF/persistence.xml")
                .addAsLibraries(libs)
                .setManifest(new StringAsset(ManifestMF));

        return archive;
    }

    private static final int NO_OF_TX = 1;
    private static final int NO_OF_PARTICIPANTS = 2;
    private static final int INTRO_DELAY = 500;
    private static final int OUTRO_DELAY = 3000;
    private static final int READY_POLL_INTERVAL = 200;

    private LiveTestMockTransactionMonitor txmon;
    private DataAccessObject dao;
    private TransactionUtil txUtil;

    @Before
    public void setup() throws Exception {
        txmon = new LiveTestMockTransactionMonitor();
        dao = DAOFactory.getInstance();
        txUtil = new TransactionUtil();
        DAOFactory.getInstance().deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        DAOFactory.shutdown();
    }

    @Test
    public void clientDrivenCommitTest() throws Exception {
        testBootstrap(Status.COMMIT);

        assertEquals("Incorrect number of transaction parsed", NO_OF_TX, DAOFactory.getInstance().retrieveAll().size());

        for (Transaction t : DAOFactory.getInstance().retrieveAll()) {
            assertEquals("Transaction " + t.getTransactionId() + " did not report the correct status", Status.COMMIT,
                    DAOFactory.getInstance().retrieve(t.getTransactionId()).getStatus());
        }

        for (Transaction t : DAOFactory.getInstance().retrieveAll()) {
            for (Participant p : t.getParticipants())
                assertEquals("Participant " + p.getResourceId() + " did not report the correct vote", Vote.COMMIT,
                        p.getVote());
        }
    }

    @Test
    public void clientDrivenRollbackTest() throws Exception {
        testBootstrap(Status.ROLLBACK_CLIENT);

        assertEquals("Incorrect number of transaction parsed", NO_OF_TX, DAOFactory.getInstance().retrieveAll().size());

        for (Transaction t : DAOFactory.getInstance().retrieveAll()) {
            assertEquals("Transaction " + t.getTransactionId() + " did not report the correct status", Status.ROLLBACK_CLIENT,
                    DAOFactory.getInstance().retrieve(t.getTransactionId()).getStatus());
        }
    }

    @Test
    public void resourceDrivenRollbackTest() throws Exception {
        testBootstrap(Status.ROLLBACK_RESOURCE);

        assertEquals("Incorrect number of transaction parsed", NO_OF_TX, DAOFactory.getInstance().retrieveAll().size());

        for (Transaction t : DAOFactory.getInstance().retrieveAll()) {
            assertEquals("Transaction " + t.getTransactionId() + " did not report the correct status", Status.ROLLBACK_RESOURCE,
                    DAOFactory.getInstance().retrieve(t.getTransactionId()).getStatus());
        }

        for (Transaction t : DAOFactory.getInstance().retrieveAll()) {
            int abortVotes = 0;
            for (Participant p : t.getParticipants()) {
                if (p.getVote().equals(Vote.ABORT))
                    abortVotes++;
            }
            assertEquals("Participants of transaction: " + t.getTransactionId() +
                    " did not report correct number of votes: " + Vote.ABORT, 1, abortVotes);
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
            txUtil.createTx(noOfTx, noOfParticipantsPerTx, outcome);
            Thread.sleep(outroSleepDelay);
        }
        finally {
            txmon.stop();
        }

    }
}
