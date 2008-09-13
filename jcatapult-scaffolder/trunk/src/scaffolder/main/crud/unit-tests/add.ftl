<#import "/global/macros.ftl" as global>
package ${actionPackage};

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import ${type.fullName};
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class tests the add action.
 * </p>
 *
 * @author  Scaffolder
 */
public class AddTest {

    /**
     * Tests get.
     */
    @Test
    public void testGet() {
        Add action = new Add();
        String result = action.get();
        assertEquals("input", result);
    }

    /**
     * Tests post.
     */
    @Test
    public void testPost() {
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToMany")>
        Integer[] ${field.name}IDs = new Integer[]{1, 2, 3};
  </#if>
</#list>
        ${type.name} ${type.fieldName} = new ${type.name}();
        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
        service.persist(${type.fieldName}<@global.idValues />);
        EasyMock.replay(service);

        Add action = new Add();
        action.setServices(service, null);
        action.${type.fieldName} = ${type.fieldName};
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        action.${field.name}ID = 1;
  </#if>
  <#if field.hasAnnotation("javax.persistence.ManyToMany")>
        action.${field.name}IDs = ${field.name}IDs;
  </#if>
</#list>
        String result = action.post();
        assertEquals("success", result);
        EasyMock.verify(service);
    }
}