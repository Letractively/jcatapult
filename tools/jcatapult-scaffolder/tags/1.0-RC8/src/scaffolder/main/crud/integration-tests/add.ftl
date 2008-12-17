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
<#list type.allFields as field>
  <#if field.mainType.fullName == "java.lang.String" && !field.hasAnnotation("javax.persistence.Transient")>
            withParameter("${type.fieldName}.${field.name}", "test ${field.name}").
  </#if>
</#list>
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToMany")>
            withParameter("${field.name}IDs", "1").
            withParameter("${field.name}IDs", "2").
            withParameter("${field.name}IDs", "3").
  </#if>
</#list>
            // Add additional parameters here
            post();

        String result = runner.response.getRedirect();
        assertEquals("${uri}/", result);
    }
}