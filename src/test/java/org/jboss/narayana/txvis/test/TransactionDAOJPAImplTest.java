package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.ConfigurationManager;
import org.jboss.narayana.txvis.dataaccess.*;
import org.jboss.narayana.txvis.test.utils.UniqueIdGenerator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static junit.framework.Assert.*;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 01/05/2013
 * Time: 14:10
 */
@Ignore
@RunWith(Arquillian.class)
public class TransactionDAOJPAImplTest {
//
//    @Deployment
//    public static WebArchive createDeployment() {
//        String ManifestMF = "Manifest-Version: 1.0\n"
//                + "Dependencies: org.jboss.jts\n";
//
//        File[] libs = Maven.resolver()
//                .loadPomFromFile("pom.xml").resolve("commons-io:commons-io:2.4")
//                .withTransitivity().asFile();
//
//        WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
//                .addPackages(true, "org.jboss.narayana.txvis")
//                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")), "classes/META-INF/persistence.xml")
//                .addAsLibraries(libs)
//                .setManifest(new StringAsset(ManifestMF));
//
//        return archive;
//    }
//
//    TransactionDAO transactionDAO;
//    UniqueIdGenerator idGen;
//
//    @Before
//    public void setup() throws Exception {
//        ConfigurationManager.INSTANCE.setResourceDaoImplementationClass(
//                "org.jboss.narayana.txvis.dataaccess.ResourceDAOJPAImpl");
//        ConfigurationManager.INSTANCE.setTransactionDaoImplementationClass(
//                "org.jboss.narayana.txvis.dataaccess.TransactionDAOJPAImpl");
//        DAOFactory.initialize();
//        transactionDAO = DAOFactory.getInstance();
//
//        idGen = new UniqueIdGenerator();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        DAOFactory.shutdown();
//    }
//
//
//    @Test(expected = NullPointerException.class)
//    public void createWithNullParamTest() throws Exception {
//        transactionDAO.create(null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void createWithIllegelParamTest() throws Exception {
//        final String p = "illegal.txID";
//        transactionDAO.create(p);
//    }
//
//    @Test
//    public void createAndGetTest() throws Exception {
//        final String txID = idGen.getUniqueTxId();
//        transactionDAO.create(txID);
//        assertEquals("Did not successfully retrieve transaction from database", txID,
//                transactionDAO.get(txID).getTransactionID());
//    }
//
//    @Test
//    public void getAllTest() throws Exception {
//        final int noOfTx = 4;
//
//        final String[] txID = new String[noOfTx];
//        for (int i = 0; i < noOfTx ; i++) {
//            txID[i] = idGen.getUniqueTxId();
//            transactionDAO.create(txID[i]);
//        }
//        assertEquals("Incorrect number of transaction records retrieved", noOfTx, transactionDAO.getAll().size());
//    }
//
//    @Test
//    public void enlistAndRetrieveParticipantTest() throws Exception {
//        final String txID = idGen.getUniqueTxId();
//        final String participantID = idGen.getUniqueResourceId();
//
//        transactionDAO.create(txID);
//        transactionDAO.enlistParticipantResource(txID, participantID);
//
//        ParticipantRecord pr = transactionDAO.getEnlistedParticipant(txID, participantID);
//        assertNotNull("Unable to retrieve ParticipantRecord", pr);
//        assertEquals("Did not retrieve correct participant record", txID, pr.getTransaction().getTransactionID());
//        assertEquals("Did not retrieve correct participant record", participantID, pr.getResource().getResourceID());
//    }
//
//    @Test
//    public void totalTxTest() throws Exception {
//        final int noOfTx = 4;
//
//        final String[] txID = new String[noOfTx];
//        for (int i = 0; i < noOfTx ; i++) {
//            txID[i] = idGen.getUniqueTxId();
//            transactionDAO.create(txID[i]);
//        }
//
//        assertEquals("Incorrect number of transactions returned", noOfTx, transactionDAO.totalTx());
//    }
}
