package org.jcatapult.deployment.service;

import org.jcatapult.deployment.DeploymentBaseTest;
import org.jcatapult.deployment.domain.Domain;
import org.jcatapult.deployment.domain.Deploy;
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
    private ValidationService<Deploy> vs;

    @Inject
    public void setService(ValidationService<Deploy> vs) {
        this.vs = vs;
    }

    @Test
    public void testServiceValidation() {
        Deploy props = new Deploy();
        String errorMsg = vs.validate(props);
        Assert.assertNotNull(errorMsg);
        testOutput(errorMsg);
    }

    @Test
    public void testNoDeployFailure() {
        Deploy props = new Deploy();

        ErrorList errorList = props.validate();

        Assert.assertEquals(1, errorList.size());

        testOutput(vs.validate(props));
    }

    @Test
    public void testInvalidDeployFailure1() {
        Deploy props = new Deploy();
        Domain domain = new Domain();
        props.addDomain(domain);

        ErrorList errorList = props.validate();

        Assert.assertEquals(2, errorList.size());

        testOutput(vs.validate(props));
    }

    @Test
    public void testInvalidDeployFailure2() {
        Deploy props = new Deploy();
        Domain domain = new Domain();
        domain.setName("test domain");
        props.addDomain(domain);

        ErrorList errorList = props.validate();

        Assert.assertEquals(1, errorList.size());

        testOutput(vs.validate(props));
    }

    @Test
    public void testEnvironmentFailure1() {
        Deploy props = new Deploy();
        Domain domain = new Domain();
        domain.setName("test domain");
        Environment env = new Environment();
        domain.addEnv(env);
        props.addDomain(domain);

        ErrorList errorList = props.validate();

        Assert.assertEquals(4, errorList.size());

        testOutput(vs.validate(props));
    }

    @Test
    public void testEnvironmentFailure2() {
        Deploy props = new Deploy();
        Domain domain = new Domain();
        domain.setName("test domain");
        Environment env = new Environment();
        env.setName("test env");
        domain.addEnv(env);
        props.addDomain(domain);

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
