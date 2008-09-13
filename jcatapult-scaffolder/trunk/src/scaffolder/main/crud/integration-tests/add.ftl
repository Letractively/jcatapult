<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.WebappTestRunner;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the add action.
 * </p>
 *
 * @author  Scaffolder
 */
public class AddIntegrationTest extends BaseIntegrationTest {

    /**
     * Tests get.
     */
    @Test
    public void testGet() throws IOException, ServletException {
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("${uri}/add").get();

        String result = runner.response.getStream().toString();
        assertTrue(result.contains("<form"));
    }

    /**
     * Tests post.
     */
    @Test
    public void testPost() throws IOException, ServletException {
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("${uri}/add").
            // Add parameters here
            post();

        String result = runner.response.getRedirect();
        assertEquals("${uri}/", result);
    }
}