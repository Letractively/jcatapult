package org.jcatapult.deployment.service;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.jcatapult.deployment.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class XmlServiceImplTest extends BaseTest {
    private XmlService<Configuration> xs;

    @Inject
    public void setService(XmlService<Configuration> xs) {
        this.xs = xs;
    }

    @Test
    public void testUnmarshallGetPropsManually() throws XmlServiceException {
        File file = new File("src/xml/examples/deploy.xml");
        XMLConfiguration c = (XMLConfiguration) xs.unmarshall(file);

        // test domain1 props
        Assert.assertEquals("domain1", c.getString("deploy(0).[@domain]"));
        Assert.assertEquals("staging", c.getString("deploy(0).environment.[@type]"));
        Assert.assertEquals("staging.jcatapult.org", c.getString("deploy(0).environment.host"));
        Assert.assertEquals("staging.host.username", c.getString("deploy(0).environment.host-username"));
        Assert.assertEquals("staging.host.password", c.getString("deploy(0).environment.host-password"));
        Assert.assertEquals("staging.db.username", c.getString("deploy(0).environment.db-username"));
        Assert.assertEquals("staging.db.password", c.getString("deploy(0).environment.db-password"));

        // test domain1 props
        Assert.assertEquals("domain2", c.getString("deploy(1).[@domain]"));
        Assert.assertEquals("qa", c.getString("deploy(1).environment(0).[@type]"));
        Assert.assertEquals("qa.jcatapult.org", c.getString("deploy(1).environment(0).host"));
        Assert.assertEquals("qa.host.username", c.getString("deploy(1).environment(0).host-username"));
        Assert.assertEquals("qa.host.password", c.getString("deploy(1).environment(0).host-password"));
        Assert.assertEquals("qa.db.username", c.getString("deploy(1).environment(0).db-username"));
        Assert.assertEquals("qa.db.password", c.getString("deploy(1).environment(0).db-password"));

        // test domain1 props
        Assert.assertEquals("domain2", c.getString("deploy(1).[@domain]"));
        Assert.assertEquals("prod", c.getString("deploy(1).environment(1).[@type]"));
        Assert.assertEquals("prod.jcatapult.org", c.getString("deploy(1).environment(1).host"));
        Assert.assertEquals("prod.host.username", c.getString("deploy(1).environment(1).host-username"));
        Assert.assertEquals("prod.host.password", c.getString("deploy(1).environment(1).host-password"));
        Assert.assertEquals("prod.db.username", c.getString("deploy(1).environment(1).db-username"));
        Assert.assertEquals("prod.db.password", c.getString("deploy(1).environment(1).db-password"));
    }

    public void testUnmarshallGetPropsIteratively() throws XmlServiceException {
        File file = new File("src/xml/examples/deploy.xml");
        XMLConfiguration c = (XMLConfiguration) xs.unmarshall(file);

        List domains = c.getList("deploy.[@domain]");
        
    }
}
