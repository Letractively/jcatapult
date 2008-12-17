<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;

<@global.importFields />
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class tests the prepare action.
 * </p>
 *
 * @author  Scaffolder
 */
public class PrepareTest {
    /**
     * Tests prepare.
     */
    @Test
    public void testPrepare() {
        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        List<${field.mainType.name}> ${field.pluralName} = new ArrayList<${field.mainType.name}>();
        EasyMock.expect(service.get${field.mainType.pluralName}()).andReturn(${field.pluralName});
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
        List<${field.genericTypes[0].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[0].name}>();
        EasyMock.expect(service.get${field.genericTypes[0].pluralName}()).andReturn(${field.pluralName});
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
        List<${field.genericTypes[1].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[1].name}>();
        EasyMock.expect(service.get${field.genericTypes[1].pluralName}()).andReturn(${field.pluralName});
  </#if>
</#list>
        EasyMock.replay(service);

        Prepare prepare = new Prepare(service);
        String result = prepare.execute();
        assertEquals("success", result);

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        assertSame(${field.pluralName}, prepare.get${field.mainType.pluralName}());
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
        assertSame(${field.pluralName}, prepare.get${field.genericTypes[0].pluralName}());
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
        assertSame(${field.pluralName}, prepare.get${field.genericTypes[1].pluralName}());
  </#if>
</#list>

        EasyMock.verify(service);
    }
}