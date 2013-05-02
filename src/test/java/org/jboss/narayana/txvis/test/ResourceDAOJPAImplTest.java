package org.jboss.narayana.txvis.test;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.txvis.ConfigurationManager;
import org.jboss.narayana.txvis.dataaccess.*;
import org.jboss.narayana.txvis.test.utils.UniqueIdGenerator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
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

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 01/05/2013
 * Time: 11:41
 */
@RunWith(Arquillian.class)
public class ResourceDAOJPAImplTest {

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

    ResourceDAO resourceDAO;
    UniqueIdGenerator idGen;

    @Before
    public void setup() throws Exception {
        resourceDAO = new ResourceDAOJPAImpl();
        idGen = new UniqueIdGenerator();
    }

    @Test(expected = NullPointerException.class)
    public void getWithNullParamTest() throws Exception {
        resourceDAO.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getWithIllegalParamTest() throws Exception {
        resourceDAO.get("");
    }

    @Test
    public void getTest() throws Exception {
        final String fakeResourceID = idGen.getUniqueResourceId();
        Assert.assertNotNull("Did not create resource", resourceDAO.get(fakeResourceID));
        Assert.assertNotNull("Did not retrieve resource", resourceDAO.get(fakeResourceID));
    }
}