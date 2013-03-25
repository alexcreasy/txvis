package org.jboss.narayana.txvis.simple;

import com.arjuna.ats.jta.TransactionManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jbossts.xts.bytemanSupport.BMScript;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.transaction.RollbackException;


/**
 * @author paul.robinson@redhat.com 21/01/2013
 */
@RunWith(Arquillian.class)
public class SimpleTest {

    private static final String TXVIS_RULES = "txvis_rules.txt";

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackages(true, SimpleTest.class.getPackage());

        archive.delete(ArchivePaths.create("META-INF/MANIFEST.MF"));

        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";


        archive.setManifest(new StringAsset(ManifestMF));

        return archive;
    }

    @BeforeClass()
    public static void submitBytemanScript() throws Exception {

        //BMScript.submit(TXVIS_RULES);
    }

    @AfterClass()
    public static void removeBytemanScript() {

        //BMScript.remove(TXVIS_RULES);
    }


    @Test
    public void clientDrivenCommitTest() throws Exception {

        TransactionManager.transactionManager().begin();

        DummyXAResource dummyXAResource1 = new DummyXAResource("dummy1");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource1);
        DummyXAResource dummyXAResource2 = new DummyXAResource("dummy2");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource2);

        TransactionManager.transactionManager().commit();
    }

    @Test
    public void clientDrivenRollbackTest() throws Exception {

        TransactionManager.transactionManager().begin();

        DummyXAResource dummyXAResource1 = new DummyXAResource("dummy1");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource1);
        DummyXAResource dummyXAResource2 = new DummyXAResource("dummy2");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource2);

        TransactionManager.transactionManager().rollback();
    }

    @Test(expected = RollbackException.class)
    public void resourceDrivenRollbackTest() throws Exception {

        TransactionManager.transactionManager().begin();

        DummyXAResource dummyXAResource1 = new DummyXAResource("dummy1");
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource1);
        DummyXAResource dummyXAResource2 = new DummyXAResource("dummy2", false);
        TransactionManager.transactionManager().getTransaction().enlistResource(dummyXAResource2);

        TransactionManager.transactionManager().commit();
    }
}
