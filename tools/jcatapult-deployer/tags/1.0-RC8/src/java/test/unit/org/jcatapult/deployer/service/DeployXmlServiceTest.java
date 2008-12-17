package org.jcatapult.deployer.service;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.jcatapult.deployer.DeploymentBaseTest;
import org.jcatapult.deployer.domain.Deploy;
import org.jcatapult.deployer.domain.Domain;
import org.jcatapult.deployer.domain.Environment;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * @author jhumphrey

 */
public class DeployXmlServiceTest extends DeploymentBaseTest {
    private DeployXmlService xs;

    @Inject
    public void setService(DeployXmlService xs) {
        this.xs = xs;
    }

    @Test
    public void testUnmarshallingMultipleDomains() throws XmlServiceException, ConfigurationException {
        File file = new File("src/xml/examples/deploy-multiple-domains.xml");
        Deploy dProps = xs.unmarshall(file);

        Assert.assertEquals(2, dProps.getDomains().size());

        Domain domain1 = dProps.getDomains().get(0);
        Assert.assertEquals("domain1", domain1.getName());

        Assert.assertEquals(1, domain1.getEnvs().size());

        {
            Environment env = domain1.getEnvs().get(0);
            Assert.assertEquals("staging.jcatapult.org", env.getHost());
            Assert.assertEquals("staging.host.username", env.getHostUsername());
            Assert.assertEquals("staging.host.password", env.getHostPassword());
            Assert.assertEquals("staging.db.name", env.getDbName());
            Assert.assertEquals("staging.db.username", env.getDbUsername());
            Assert.assertEquals("staging.db.password", env.getDbPassword());
        }

        Domain domain2 = dProps.getDomains().get(1);
        Assert.assertEquals("domain2", domain2.getName());

        Assert.assertEquals(2, domain2.getEnvs().size());

        {
            Environment env = domain2.getEnvs().get(0);
            Assert.assertEquals("qa.jcatapult.org", env.getHost());
            Assert.assertEquals("qa.host.username", env.getHostUsername());
            Assert.assertEquals("qa.host.password", env.getHostPassword());
            Assert.assertEquals("qa.db.name", env.getDbName());
            Assert.assertEquals("qa.db.username", env.getDbUsername());
            Assert.assertEquals("qa.db.password", env.getDbPassword());
        }

        {
            Environment env = domain2.getEnvs().get(1);
            Assert.assertEquals("prod.jcatapult.org", env.getHost());
            Assert.assertEquals("prod.host.username", env.getHostUsername());
            Assert.assertEquals("prod.host.password", env.getHostPassword());
            Assert.assertEquals("prod.db.name", env.getDbName());
            Assert.assertEquals("prod.db.username", env.getDbUsername());
            Assert.assertEquals("prod.db.password", env.getDbPassword());
        }
    }
}
