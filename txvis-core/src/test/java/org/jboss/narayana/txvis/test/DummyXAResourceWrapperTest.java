package org.jboss.narayana.txvis.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.persistence.entities.Transaction;
import org.jboss.narayana.txvis.persistence.enums.Status;
import org.jboss.narayana.txvis.test.utils.TransactionUtil;
import org.jboss.narayana.txvis.test.utils.TransactionUtil2;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 31/05/2013
 * Time: 19:25
 */
@RunWith(Arquillian.class)
public class DummyXAResourceWrapperTest {

    @Deployment
    public static WebArchive createDeployment() {
        String ManifestMF = "Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.jts\n";

        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve("commons-io:commons-io:2.4")
                .withTransitivity().asFile();

        WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.narayana.txvis.test")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new FileAsset(new File("src/test/resources/persistence.xml")), "classes/META-INF/persistence.xml")
                .addAsManifestResource(new FileAsset(new File("src/test/resources/txvis-test-ds.xml")), "txvis-test-ds.xml")
                .addAsLibraries(libs)
                .setManifest(new StringAsset(ManifestMF));

        return archive;
    }

    private TransactionUtil txUtil;

    @Before
    public void setup() throws Exception {
        txUtil = new TransactionUtil();
    }


    @Test
    public void test1() throws Exception {
        TransactionUtil2 txUtil = new TransactionUtil2();
        txUtil.createTx(3, 3, Status.COMMIT);
        Thread.sleep(500);
        txUtil.createTx(3,3,Status.ROLLBACK_CLIENT);
        Thread.sleep(500);
        txUtil.createTx(3,3, Status.ROLLBACK_RESOURCE);
    }
}
