package ${actionPackage};

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.WebappTestRunner;
import static org.junit.Assert.*;
import org.junit.Test;

import ${type.fullName};

/**
 * <p>
 * This class tests the delete action.
 * </p>
 *
 * @author  Scaffolder
 */
public class DeleteIntegrationTest extends BaseIntegrationTest {
    /**
     * Tests the nothing is deleted if no ids are selected.
     */
    @Test
    public void testNoDelete() throws IOException, ServletException {
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("${uri}/delete").post();

        String result = runner.response.getRedirect();
        assertEquals("${uri}/", result);
    }

    /**
     * Tests that all the selected ids are deleted.
     */
    @Test
    public void testDelete() throws IOException, ServletException {
        ${type.name} ${type.fieldName} = make${type.name}();
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("${uri}/delete").
            withParameter("ids", "" + ${type.fieldName}.getId()).
            post();

        String result = runner.response.getRedirect();
        assertEquals("${uri}/", result);
        assertNull(persistenceService.findById(${type.name}.class, ${type.fieldName}.getId()));
    }
}