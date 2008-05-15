package org.jcatapult.deployment.service;

import java.io.File;

import org.jcatapult.deployment.domain.Project;
import org.jcatapult.deployment.DeploymentBaseTest;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * User: jhumphrey
 * Date: May 15, 2008
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

        Assert.assertEquals("jcatapult-deployment", project.getName());
    }
}
