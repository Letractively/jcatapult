package org.jcatapult.deployment.service;

import org.jcatapult.deployment.DeploymentBaseTest;
import org.jcatapult.deployment.domain.Deploy;
import org.jcatapult.deployment.domain.DeploymentProperties;
import org.jcatapult.deployment.domain.Environment;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;
import net.java.error.ErrorList;

/**
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class ValidationServiceImplTest extends DeploymentBaseTest {
    private ValidationService<DeploymentProperties> vs;

    @Inject
    public void setService(ValidationService<DeploymentProperties> vs) {
        this.vs = vs;
    }

    @Test
    public void testServiceValidation() {
        DeploymentProperties props = new DeploymentProperties();
        String errorMsg = vs.validate(props);
        Assert.assertNotNull(errorMsg);
        testOutput(errorMsg);
    }

    @Test
    public void testNoDeployFailure() {
        DeploymentProperties props = new DeploymentProperties();

        ErrorList errorList = props.validate();

        Assert.assertEquals(1, errorList.size());

        testOutput(vs.validate(props));
    }

    @Test
    public void testInvalidDeployFailure1() {
        DeploymentProperties props = new DeploymentProperties();
        Deploy deploy = new Deploy();
        props.addDeploy(deploy);

        ErrorList errorList = props.validate();

        Assert.assertEquals(2, errorList.size());

        testOutput(vs.validate(props));
    }

    @Test
    public void testInvalidDeployFailure2() {
        DeploymentProperties props = new DeploymentProperties();
        Deploy deploy = new Deploy();
        deploy.setDomain("test domain");
        props.addDeploy(deploy);

        ErrorList errorList = props.validate();

        Assert.assertEquals(1, errorList.size());

        testOutput(vs.validate(props));
    }

    @Test
    public void testEnvironmentFailure1() {
        DeploymentProperties props = new DeploymentProperties();
        Deploy deploy = new Deploy();
        deploy.setDomain("test domain");
        Environment env = new Environment();
        deploy.addEnv(env);
        props.addDeploy(deploy);

        ErrorList errorList = props.validate();

        Assert.assertEquals(4, errorList.size());

        testOutput(vs.validate(props));
    }

    @Test
    public void testEnvironmentFailure2() {
        DeploymentProperties props = new DeploymentProperties();
        Deploy deploy = new Deploy();
        deploy.setDomain("test domain");
        Environment env = new Environment();
        env.setType("test env");
        deploy.addEnv(env);
        props.addDeploy(deploy);

        ErrorList errorList = props.validate();

        Assert.assertEquals(3, errorList.size());

        testOutput(vs.validate(props));
    }

    /**
     * Helper method to output error messages to sout for testing purposes only
     *
     * @param errorMsg the error messsage
     */
    private void testOutput(String errorMsg) {
        System.out.println("Testing error message output:\n" + errorMsg);
    }
}
