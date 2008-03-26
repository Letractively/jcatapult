package org.jcatapult.deployment.service;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;
import org.jcatapult.deployment.BaseTest;
import org.jcatapult.deployment.domain.jaxb.Deploy;
import org.jcatapult.deployment.domain.jaxb.Server;
import org.jcatapult.deployment.domain.jaxb.Environment;

import com.google.inject.Inject;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class JaxbXmlServiceTest extends BaseTest {
    private XmlService<Deploy> xs;

    @Inject
    public void setService(XmlService<Deploy> xs) {
        this.xs = xs;
    }

    @Test
    public void testUnmarshall() throws XmlServiceException {
        File file = new File("src/xml/examples/deploy.xml");
        System.out.println(file.getAbsolutePath());
        Deploy deploy = xs.unmarshall(file);

        List<Server> serverEntries = deploy.getServer();

        Assert.assertEquals(1, serverEntries.size());

        Server serverEntry = serverEntries.get(0);

        Assert.assertEquals("demo.jcatapult.org", serverEntry.getHost());
        Assert.assertEquals("Demo Server", serverEntry.getDescription());
        Assert.assertEquals("jcatapult-demo", serverEntry.getPassword());
        Assert.assertEquals("jcatapult-demo", serverEntry.getUsername());

        List<Environment> envEntries = serverEntry.getEnvironment();

        Assert.assertEquals(1, envEntries.size());

        Environment envEntry = envEntries.get(0);

        Assert.assertEquals("staging", envEntry.getType());
        Assert.assertEquals("jcatapult-demo", envEntry.getDbName());
        Assert.assertEquals("jcatapult-demo", envEntry.getDbPassword());
        Assert.assertEquals("jcatapult-demo", envEntry.getDbUsername());
        Assert.assertEquals("dev/files", envEntry.getFileDir());
        Assert.assertEquals("/home/jcatapult-demo", envEntry.getHomeDir());
        Assert.assertEquals("dev/apache-tomcat-5.5.26/webapps/ROOT", envEntry.getWebDir());
        Assert.assertEquals("dev/work", envEntry.getWorkDir());

    }
}
