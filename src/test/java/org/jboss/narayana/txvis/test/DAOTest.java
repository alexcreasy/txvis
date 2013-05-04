package org.jboss.narayana.txvis.test;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.dataaccess.*;
import org.jboss.narayana.txvis.test.utils.UniqueIdGenerator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;

import static junit.framework.Assert.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 03/05/2013
 * Time: 17:31
 */
@RunWith(Arquillian.class)
public class DAOTest {

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

    UniqueIdGenerator idGen;

    @Before
    public void setup() throws Exception {
        DAOFactory.initialize();
        idGen = new UniqueIdGenerator();
    }

    @After
    public void tearDown() throws Exception {
        DAOFactory.shutdown();
    }


    @Test
    public void createRetrieveTest() throws Exception {
        final String txID = idGen.getUniqueTxId();
        DAOFactory.getInstance().create(txID);
        assertNotNull(DAOFactory.getInstance().retrieve(txID));
    }

    @Test
    public void enlistGetEnlistedTest() throws Exception {
        final String txID = idGen.getUniqueTxId();
        final String ptID1 = idGen.getUniqueResourceId();
        final String ptID2 = idGen.getUniqueResourceId();

        DAOFactory.getInstance().create(txID);
        DAOFactory.getInstance().enlistParticipant(txID, ptID1);
        DAOFactory.getInstance().enlistParticipant(txID, ptID2);
        assertNotNull(DAOFactory.getInstance().getEnlistedParticipant(txID, ptID1));
        assertNotNull(DAOFactory.getInstance().getEnlistedParticipant(txID, ptID2));

        assertEquals(DAOFactory.getInstance().getEnlistedParticipant(txID, ptID1).getResourceId(), ptID1);
        assertEquals(DAOFactory.getInstance().getEnlistedParticipant(txID, ptID2).getResourceId(), ptID2);

        assertEquals(2, DAOFactory.getInstance().retrieve(txID).getParticipants().size());
    }

    @Test
    public void setOutcomeTest() throws Exception {
        final String txID = idGen.getUniqueTxId();
        DAOFactory.getInstance().create(txID);

        DAOFactory.getInstance().setOutcome(txID, Status.COMMIT);
        assertEquals("Retrieved transaction did not report correct status", Status.COMMIT,
                DAOFactory.getInstance().retrieve(txID).getStatus());

        DAOFactory.getInstance().setOutcome(txID, Status.ROLLBACK_CLIENT);
        assertEquals("Retrieved transaction did not report correct status", Status.ROLLBACK_CLIENT,
                DAOFactory.getInstance().retrieve(txID).getStatus());

        DAOFactory.getInstance().setOutcome(txID, Status.ROLLBACK_RESOURCE);
        assertEquals("Retrieved transaction did not report correct status",Status.ROLLBACK_RESOURCE,
                DAOFactory.getInstance().retrieve(txID).getStatus());
    }

    @Test
    public void setParticipantVoteTest() throws Exception {
        final String txID = idGen.getUniqueTxId();
        final String ptID1 = idGen.getUniqueResourceId();
        final String ptID2 = idGen.getUniqueResourceId();

        DAOFactory.getInstance().create(txID);
        DAOFactory.getInstance().enlistParticipant(txID, ptID1);
        DAOFactory.getInstance().enlistParticipant(txID, ptID2);

        DAOFactory.getInstance().setParticipantVote(txID, ptID1, Vote.COMMIT);
        DAOFactory.getInstance().setParticipantVote(txID, ptID2, Vote.ABORT);

        assertEquals("Participant did not report correct vote", Vote.COMMIT,
                DAOFactory.getInstance().getEnlistedParticipant(txID, ptID1).getVote());

        assertEquals("Participant did not report correct vote", Vote.ABORT,
                DAOFactory.getInstance().getEnlistedParticipant(txID, ptID2).getVote());
    }
}
