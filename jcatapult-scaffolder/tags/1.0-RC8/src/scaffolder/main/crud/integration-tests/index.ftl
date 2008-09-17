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
 * This class tests the index action.
 * </p>
 *
 * @author  Scaffolder
 */
public class IndexIntegrationTest extends BaseIntegrationTest {

    /**
     * Tests get.
     */
    @Test
    public void testGet() throws IOException, ServletException {
        make${type.name}();
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("${uri}/").get();

        String result = runner.response.getStream().toString();
        assertTrue(result.contains("<table"));
    }
}