package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.persistence.*;
import org.jboss.narayana.txvis.persistence.entities.ResourceManager;
import org.jboss.narayana.txvis.persistence.entities.ParticipantRecord;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.test.utils.UniqueIdGenerator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.File;
import java.sql.Timestamp;

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

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.narayana.txvis.persistence", "org.jboss.narayana.txvis.test.utils")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")), "classes/META-INF/persistence.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/txvis-test-ds.xml")), "txvis-test-ds.xml")
                .setManifest(new StringAsset(ManifestMF));
    }

    UniqueIdGenerator idGen;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    @EJB
    DataAccessObject dao;

    @Before
    public void setup() throws Exception {
        idGen = new UniqueIdGenerator();
    }

    @Test
    public void createAndRetrieveTest() throws Exception {
        final String txUID = idGen.getUniqueTxId();
        Transaction t = new Transaction(txUID);
        dao.create(t);

        assertNotNull("Entity did not contain an ID after attempting to persist", t.getId());

        assertNotNull("Unable to retrieve persisted Entity",
                dao.retrieve(Transaction.class, t.getId()));
    }

    @Test
    public void retrieveTransactionByTxUIDTest() throws Exception {
        final String txUID = idGen.getUniqueTxId();
        Transaction t = new Transaction(txUID);
        dao.create(t);

        assertEquals("Transaction ID did not match", txUID,
                dao.retrieveTransactionByTxUID(txUID).getTxuid());
    }

    @Test
    public void UpdateTest() throws Exception {
        final String txUID = idGen.getUniqueTxId();

        Transaction t = new Transaction(txUID);
        dao.create(t);

        t = dao.retrieveTransactionByTxUID(txUID);
        t.setStatus(Status.COMMIT, timestamp);
        dao.update(t);


        t = dao.retrieveTransactionByTxUID(txUID);
        assertEquals("Retrieved transaction entity did not report correct status",
                Status.COMMIT, t.getStatus());
    }

    @Test
    public void retrieveAllTest() throws Exception {

    }

    @Test
    public void deleteTest() throws Exception {

    }

    @Test
    public void deleteAllTest() throws Exception {

    }

    @Test
    public void retrieveTransactionsWithStatusTest() throws Exception {
        dao.deleteAll(Transaction.class);
        final String[] txUIDs = new String[4];

        final Transaction[] txs = new Transaction[4];

        for (int i = 0; i < txUIDs.length; i++) {
            txUIDs[i] = idGen.getUniqueTxId();
            txs[i] = new Transaction(txUIDs[i]);
        }

        txs[0].setStatus(Status.COMMIT, timestamp);
        txs[1].setStatus(Status.COMMIT, timestamp);
        txs[2].setStatus(Status.ROLLBACK_RESOURCE, timestamp);
        txs[3].setStatus(Status.ROLLBACK_RESOURCE, timestamp);

        for (int i = 0; i < txs.length; i++)
            dao.create(txs[i]);

        assertEquals("Incorrect number of Transaction objects with Status.COMMIT", 2,
                dao.retrieveTransactionsWithStatus(Status.COMMIT).size());
        assertEquals("Incorrect number of Transaction objects with Status.ROLLBACK_RESOURCE", 2,
                dao.retrieveTransactionsWithStatus(Status.ROLLBACK_RESOURCE).size());
    }

    @Test
    public void createParticipantRecordTest() throws Exception {
        final String txUID = idGen.getUniqueTxId();
        Transaction tx = new Transaction(txUID);
        dao.create(tx);

        final String jndiName = idGen.getUniqueJndiName();
        ResourceManager rm = new ResourceManager(jndiName, null, null);
        dao.create(rm);

        dao.createParticipantRecord(tx, rm, timestamp);

        tx = dao.retrieve(Transaction.class, tx.getId());
        assertEquals("Transaction contains incorrect number of ParticipantRecords", 1, tx.getParticipantRecords().size());

        rm = dao.retrieve(ResourceManager.class, rm.getId());
        assertEquals("ResourceManager contains incorrect number of ParticipantRecords", 1, rm.getParticipantRecords().size());

        for (ParticipantRecord pr : tx.getParticipantRecords()) {
            assertEquals("Incorrect Transaction found in PariticpantRecord", txUID, pr.getTransaction().getTxuid());
            assertEquals("Incorrect ResourceManager found in ParticipantRecord", jndiName, pr.getResourceManager().getJndiName());
        }
    }

    @Test
    public void retrieveParticipantRecordTest() throws Exception {
        final String txUID = idGen.getUniqueTxId();
        Transaction tx = new Transaction(txUID);
        dao.create(tx);

        final String jndiName = idGen.getUniqueJndiName();
        ResourceManager rm = new ResourceManager(jndiName, null, null);
        dao.create(rm);

        dao.createParticipantRecord(tx, rm, timestamp);

        assertNotNull("Did not retrieve ParticipantRecord", dao.retrieveParticipantRecord(txUID, jndiName));
    }


}
