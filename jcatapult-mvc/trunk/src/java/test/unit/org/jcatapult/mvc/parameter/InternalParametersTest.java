package org.jcatapult.mvc.parameter;

import org.jcatapult.test.JCatapultBaseTest;
import org.junit.Test;
import org.junit.Assert;

/**
 * @author jhumphrey
 */
public class InternalParametersTest extends JCatapultBaseTest {

    @Test
    public void testValidationParam() {
        // test setting it to true
        {
            request.setPost(true);
            request.setParameter(InternalParameters.JCATAPULT_EXECUTE_VALIDATION, "true");
            boolean keyState = InternalParameters.is(request, InternalParameters.JCATAPULT_EXECUTE_VALIDATION);
            Assert.assertTrue(keyState);
            request.clearParameters();
        }

        // test setting it to anything but 'false'
        {
            request.setPost(true);
            request.setParameter(InternalParameters.JCATAPULT_EXECUTE_VALIDATION, "foo");
            boolean keyState = InternalParameters.is(request, InternalParameters.JCATAPULT_EXECUTE_VALIDATION);
            Assert.assertTrue(keyState);
            request.clearParameters();
        }

        // test that's it's true even if the parameter isn't in the request
        {
            request.setPost(true);
            boolean keyState = InternalParameters.is(request, InternalParameters.JCATAPULT_EXECUTE_VALIDATION);
            Assert.assertTrue(keyState);
            request.clearParameters();
        }
    }
}
