package org.jboss.narayana.txvis.simple;

import com.arjuna.ats.jta.TransactionManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.narayana.txvis.LogParser;
import org.jboss.narayana.txvis.ParticipantDAO;
import org.jboss.narayana.txvis.TransactionDAO;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 16/04/2013
 * Time: 17:05
 */
@RunWith(Arquillian.class)
public class ParseTest {

    private static final String LOGFILE_PATH =
            "/Users/alex/Documents/workspace/jboss-as/build/target/jboss-as-8.0.0.Alpha1-SNAPSHOT/standalone/log/server.log";

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackages(true, ParseTest.class.getPackage())
                .addPackages(true, org.jboss.narayana.txvis.LogParser.class.getPackage());

        archive.delete(ArchivePaths.create("META-INF/MANIFEST.MF"));

        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";


        archive.setManifest(new StringAsset(ManifestMF));

        return archive;
    }


    @Test
    public void parseBeginTransactionTest() throws Exception {

        TransactionDAO txDAO = new TransactionDAO();
        ParticipantDAO ptDAO = new ParticipantDAO();

        Files.deleteIfExists(Paths.get(LOGFILE_PATH));

        LogParser parser = LogParser.getInstance(LOGFILE_PATH, txDAO, ptDAO);



        TransactionManager.transactionManager().begin();

        DummyXAResource dummyXAResource1 = new DummyXAResource("dummy1");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource1);
        DummyXAResource dummyXAResource2 = new DummyXAResource("dummy2");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource2);

        TransactionManager.transactionManager().commit();


    }


}
