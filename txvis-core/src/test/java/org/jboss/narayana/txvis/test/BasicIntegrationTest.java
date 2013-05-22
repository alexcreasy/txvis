package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.LogMonitorBean;
import org.jboss.narayana.txvis.persistence.*;
import org.jboss.narayana.txvis.persistence.entities.Participant;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;
import org.jboss.narayana.txvis.test.utils.TransactionUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.File;

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
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")), "classes/META-INF/persistence.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/txvis-test-ds.xml")), "txvis-test-ds.xml")
                .addAsLibraries(libs)
                .setManifest(new StringAsset(ManifestMF));

        return archive;
    }

    private static final int NO_OF_TX = 2;
    private static final int NO_OF_PARTICIPANTS = 3;
    private static final int INTRO_DELAY = 300;
    private static final int OUTRO_DELAY = 3000;


    @EJB
    private DataAccessObject dao;

    @EJB
    private LogMonitorBean mon;

    private TransactionUtil txUtil;
    
    @Before
    public void setup() throws Exception {
        txUtil = new TransactionUtil();
        dao.deleteAll();
    }

    @Test
    public void clientDrivenCommitTest() throws Exception {
        createAndLogTransactions(Status.COMMIT);

        assertEquals("Incorrect number of transaction parsed", NO_OF_TX, dao.retrieveAll().size());

        for (Transaction t : dao.retrieveAll()) {
            assertEquals("Transaction " + t.getTransactionId() + " did not report the correct status", Status.COMMIT,
                    dao.retrieve(t.getTransactionId()).getStatus());
        }

        for (Transaction t : dao.retrieveAll()) {
            for (Participant p : t.getParticipants())
                assertEquals("Participant " + p.getResourceId() + " did not report the correct vote", Vote.COMMIT,
                        p.getVote());
        }
    }

    @Test
    public void clientDrivenRollbackTest() throws Exception {
        createAndLogTransactions(Status.ROLLBACK_CLIENT);

        assertEquals("Incorrect number of transaction parsed", NO_OF_TX, dao.retrieveAll().size());

        for (Transaction t : dao.retrieveAll()) {
            assertEquals("Transaction " + t.getTransactionId() + " did not report the correct status", Status.ROLLBACK_CLIENT,
                    dao.retrieve(t.getTransactionId()).getStatus());
        }
    }

    @Test
    public void resourceDrivenRollbackTest() throws Exception {
        createAndLogTransactions(Status.ROLLBACK_RESOURCE);

        assertEquals("Incorrect number of transaction parsed", NO_OF_TX, dao.retrieveAll().size());

        for (Transaction t : dao.retrieveAll()) {
            assertEquals("Transaction " + t.getTransactionId() + " did not report the correct status",
                    Status.ROLLBACK_RESOURCE, dao.retrieve(t.getTransactionId()).getStatus());
        }

        for (Transaction t : dao.retrieveAll()) {
            int abortVotes = 0;
            for (Participant p : t.getParticipants()) {
                if (p.getVote().equals(Vote.ABORT))
                    abortVotes++;
            }
            assertEquals("Participants of transaction: " + t.getTransactionId() +
                    " did not report correct number of votes: " + Vote.ABORT, 1, abortVotes);
        }
    }

    private void createAndLogTransactions(Status outcome) throws Exception {
        createAndLogTransactions(INTRO_DELAY, OUTRO_DELAY, NO_OF_TX, NO_OF_PARTICIPANTS, outcome);
    }

    private void createAndLogTransactions(int introSleepDelay, int outroSleepDelay, int noOfTx,
                                          int noOfParticipantsPerTx, Status outcome) throws Exception {
        mon.start();
        try {
            Thread.sleep(introSleepDelay);
            txUtil.createTx(noOfTx, noOfParticipantsPerTx, outcome);
            Thread.sleep(outroSleepDelay);
        } finally {
            mon.stop();
        }
    }
}
