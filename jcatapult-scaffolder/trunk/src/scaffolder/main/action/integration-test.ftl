package ${pkgName};

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.WebappTestRunner;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class performs the integration test for the ${className} action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ${className}IntegrationTest {
    @Test
    public void testExecute() throws IOException, ServletException {
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("${uri}").
            // Setup parameters here
            post();

        String result = runner.response.getStream().toString();
        assertTrue(result.contains("<html>"));
    }
}