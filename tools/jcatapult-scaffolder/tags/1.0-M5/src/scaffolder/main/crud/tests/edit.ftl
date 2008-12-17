<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.Arrays;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

import ${type.fullName};
<@global.importFields />
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class tests the edit action.
 * </p>
 *
 * @author  Scaffolder
 */
public class EditTest {
    /**
     * Tests edit fails when the id is invalid.
     */
    @Test
    public void testNoEdit() {
        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
        EasyMock.expect(service.getById(1)).andReturn(null);
        EasyMock.replay(service);

        Edit edit = new Edit(service);
        edit.setId(1);
        String result = edit.execute();
        assertEquals("error", result);
        assertEquals(1, edit.getActionErrors().size());
        EasyMock.verify(service);
    }

    /**
     * Tests that all the selected ids are deleted.
     */
    @Test
    public void testEdit() {
        ${type.name} ${type.fieldName} = new ${type.name}();

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        ${field.mainType.name} ${field.name} = new ${field.mainType.name}();
        ${field.name}.setId(1);
        ${type.fieldName}.set${field.methodName}(${field.name});
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
        for (int i = 0; i < 3; i++) {
            ${field.genericTypes[0].name} ${field.name} = new ${field.genericTypes[0].name}();
            ${field.name}.setId(i + 1);
            ${type.fieldName}.get${field.methodName}().add(${field.name});
        }
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
        for (int i = 0; i < 3; i++) {
            ${field.genericTypes[1].name} ${field.name} = new ${field.genericTypes[1].name}();
            ${field.name}.setId(i + 1);
            ${type.fieldName}.get${field.methodName}().add(${field.name});
        }
  </#if>
</#list>

        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
        EasyMock.expect(service.getById(1)).andReturn(${type.fieldName});
        EasyMock.replay(service);

        Edit edit = new Edit(service);
        edit.setId(1);
        String result = edit.execute();
        assertEquals("success", result);
        assertEquals(0, edit.getActionErrors().size());

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        assertEquals(1, (int) edit.get${field.methodName}Id());
  </#if>
  <#if field.hasAnnotation("javax.persistence.ManyToMany")>
        assertTrue(Arrays.equals(new Integer[]{1, 2, 3}, edit.get${field.methodName}Ids()));
  </#if>
</#list>

        EasyMock.verify(service);
    }
}