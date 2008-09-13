<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.WebappTestRunner;
import static org.junit.Assert.*;
import org.junit.Test;

import ${type.fullName};

/**
 * <p>
 * This class tests the edit action.
 * </p>
 *
 * @author  Scaffolder
 */
public class EditIntegrationTest extends BaseIntegrationTest {

    /**
     * Tests get.
     */
    @Test
    public void testGet() throws IOException, ServletException {
        ${type.name} ${type.fieldName} = make${type.name}();
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("${uri}/edit").
            withParameter("id", "" + ${type.fieldName}.getId()).
            get();

        String result = runner.response.getStream().toString();
        assertTrue(result.contains("<form"));
    }

    /**
     * Tests post.
     */
    @Test
    public void testPost() throws IOException, ServletException {
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("${uri}/edit").
            // Add parameters here
            post();

        String result = runner.response.getRedirect();
        assertEquals("${uri}/", result);
    }
}