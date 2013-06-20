package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.persistence.dao.GenericDAO;
import org.jboss.narayana.txvis.persistence.HandlerService;
import org.jboss.narayana.txvis.persistence.dao.ResourceManagerDAO;
import org.jboss.narayana.txvis.persistence.dao.TransactionDAO;
import org.jboss.narayana.txvis.persistence.entities.Event;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.EventType;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.persistence.enums.Vote;
import org.jboss.narayana.txvis.test.utils.UniqueIdGenerator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/06/2013
 * Time: 14:08
 */
@RunWith(Arquillian.class)
public class HandlerServiceTest {

    @Deployment
    public static WebArchive createDeployment() {
        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.narayana.txvis.persistence", "org.jboss.narayana.txvis.test.utils")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")), "classes/META-INF/persistence.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/txvis-test-ds.xml")), "txvis-test-ds.xml")
                .setManifest(new StringAsset(ManifestMF));
    }

    @EJB
    private GenericDAO dao;

    @EJB
    private TransactionDAO transactionDAO;

    @EJB
    private ResourceManagerDAO resourceManagerDAO;

    @EJB
    private HandlerService service;

    private UniqueIdGenerator idGen = new UniqueIdGenerator();
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());


    @Test
    public void createTxTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.beginTx(txuid, timestamp);

        final Transaction tx = transactionDAO.retrieve(txuid);

        assertNotNull("Transaction not created", tx);
        assertEquals("Incorrect txuid", txuid, tx.getTxuid());
        assertEquals("Incorrect startTime", timestamp, tx.getStartTime());
    }

    @Test
    public void prepareTxTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.beginTx(txuid, timestamp);
        service.prepareTx(txuid, timestamp);
        final Transaction tx = transactionDAO.retrieve(txuid);

        assertTrue("Could not find event record with type PREPARE", eventExists(tx.getEvents(), EventType.PREPARE));
    }

    @Test
    public void commitTx2PhaseTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.beginTx(txuid, timestamp);
        service.commitTx2Phase(txuid, timestamp);

        final Transaction tx = transactionDAO.retrieve(txuid);

        assertEquals("Transaction record shows incorrect status", Status.COMMIT, tx.getStatus());
        assertTrue("Could not find event record with type END", eventExists(tx.getEvents(), EventType.END));
    }

    @Test
    public void commitTx1PhaseTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.beginTx(txuid, timestamp);
        service.commitTx1Phase(txuid, timestamp);

        final Transaction tx = transactionDAO.retrieve(txuid);

        assertEquals("Transaction record shows incorrect status", Status.COMMIT, tx.getStatus());
        assertTrue("Transaction did not correctly report one phase commit", tx.isOnePhase());
        assertTrue("Could not find event record with type END", eventExists(tx.getEvents(), EventType.END));
    }

    @Test
    public void topLevelAbortTxTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.beginTx(txuid, timestamp);
        service.topLevelAbortTx(txuid, timestamp);

        final Transaction tx = transactionDAO.retrieve(txuid);

        assertEquals("Transaction record shows incorrect status", Status.ROLLBACK_CLIENT, tx.getStatus());
        assertTrue("Could not find event record with type END", eventExists(tx.getEvents(), EventType.END));
    }

    @Test
    public void resourceDrivenAbortTxTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.beginTx(txuid, timestamp);
        service.resourceDrivenAbortTx(txuid, timestamp);

        final Transaction tx = transactionDAO.retrieve(txuid);

        assertEquals("Transaction record shows incorrect status", Status.ROLLBACK_RESOURCE, tx.getStatus());
        assertTrue("Could not find event record with type END", eventExists(tx.getEvents(), EventType.END));
    }

    @Test
    public void enlistResourceManagerTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.beginTx(txuid, timestamp);

        // Test that the service creates a new ResourceManager if it does not already exist
        final String jndiName1 = idGen.getUniqueJndiName();
        service.enlistResourceManager(txuid, jndiName1, null, null, timestamp);
        final ResourceManager rm1 = resourceManagerDAO.retrieve(jndiName1);
        assertNotNull("ResourceManager not created", rm1);
        assertEquals("ResourceManager contained incorrect Jndi name", jndiName1, rm1.getJndiName());

        // Test that the service functions correctly if the ResourceManager already exists
        final String jndiName2 = idGen.getUniqueJndiName();
        ResourceManager rm2 = new ResourceManager(jndiName2, null, null);
        resourceManagerDAO.create(rm2);
        assertNotNull("ResourceManager not created",resourceManagerDAO.retrieve(jndiName2));
        service.enlistResourceManager(txuid, jndiName2, null, null, timestamp);
        rm2 = resourceManagerDAO.retrieve(jndiName2);
        assertNotNull("ResourceManager not created", rm2);
        assertEquals("ResourceManager contained incorrect Jndi name", jndiName2, rm2.getJndiName());

        assertEquals("Incorrect number of ParticipantRecords created", 2,
                transactionDAO.retrieve(txuid).getParticipantRecords().size());
    }

    @Test
    public void resourceVoteCommitTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.beginTx(txuid, timestamp);
        final String jndiName = idGen.getUniqueJndiName();
        service.enlistResourceManager(txuid, jndiName, null, null, timestamp);
        service.resourceVoteCommit(txuid, jndiName, timestamp);

        assertEquals("ParticipantRecord contained incorrect vote" ,Vote.COMMIT,
                dao.retrieveParticipantRecord(txuid, jndiName).getVote());
    }

    @Test
    public void resourceVoteAbortTest() throws Exception {
        final String txuid = idGen.getUniqueTxId();
        service.beginTx(txuid, timestamp);
        final String jndiName = idGen.getUniqueJndiName();
        service.enlistResourceManager(txuid, jndiName, null, null, timestamp);
        service.resourceVoteAbort(txuid, jndiName, timestamp);

        assertEquals("ParticipantRecord contained incorrect vote" , Vote.ABORT,
                dao.retrieveParticipantRecord(txuid, jndiName).getVote());
    }

    private boolean eventExists(Collection<Event> events, EventType type) {

        boolean exists = false;
        for (Event e : events) {
            if (e.getEventType().equals(type))
                exists = true;
        }
        return exists;
    }
}
