package org.jboss.narayana.txvis.test;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.dataaccess.DAOFactory;
import org.jboss.narayana.txvis.dataaccess.DataAccessObject;
import org.jboss.narayana.txvis.test.utils.UniqueIdGenerator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
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


    @Test
    public void crudTest() throws Exception {
        DAOFactory.initialize();
        final String txID = new UniqueIdGenerator().getUniqueTxId();
        DAOFactory.getInstance().create(txID);
        assertNotNull(DAOFactory.getInstance().retrieve(txID));
        DAOFactory.shutdown();
    }

    @Test
    public void enlistTest() throws Exception {
        DAOFactory.initialize();

        UniqueIdGenerator idGen = new UniqueIdGenerator();

        final String txID = idGen.getUniqueTxId();
        final String ptID = idGen.getUniqueResourceId();

        DAOFactory.getInstance().create(txID);
        DAOFactory.getInstance().enlistParticipant(txID, ptID);

        assertNotNull(DAOFactory.getInstance().getEnlistedParticipant(txID, ptID));

        DAOFactory.shutdown();
    }

    @Test
    public void enlistTest2() throws Exception {
        DAOFactory.initialize();

        UniqueIdGenerator idGen = new UniqueIdGenerator();

        final String txID = idGen.getUniqueTxId();
        final String ptID = idGen.getUniqueResourceId();
        final String ptID2 = idGen.getUniqueResourceId();

        DAOFactory.getInstance().create(txID);
        DAOFactory.getInstance().enlistParticipant(txID, ptID);
        DAOFactory.getInstance().enlistParticipant(txID, ptID2);
        assertNotNull(DAOFactory.getInstance().getEnlistedParticipant(txID, ptID));
        assertNotNull(DAOFactory.getInstance().getEnlistedParticipant(txID, ptID2));
    }
}
