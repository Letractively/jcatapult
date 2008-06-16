package org.jcatapult.deployer.service;

import java.io.File;

import org.jcatapult.deployer.DeploymentBaseTest;
import org.jcatapult.deployer.domain.Project;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * @author jhumphrey

 */
public class ProjectXmlServiceTest extends DeploymentBaseTest {
    private ProjectXmlService xs;

    @Inject
    public void setService(ProjectXmlService xs) {
        this.xs = xs;
    }

    @Test
    public void test() throws XmlServiceException {
        Project project = xs.unmarshall(new File("project.xml").getAbsoluteFile());

        Assert.assertEquals("jcatapult-deployer", project.getName());
    }
}
