<#import "/global/macros.ftl" as global>
package ${actionPackage};

import static junit.framework.Assert.*;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Test;

import ${type.fullName};
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class tests the save action.
 * </p>
 *
 * @author  Scaffolder
 */
public class SaveTest {
    /**
     * Tests save.
     */
    @Test
    public void testSave() {
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToMany")>
        Integer[] ${field.name}Ids = new Integer[]{1, 2, 3};
  </#if>
</#list>
        ${type.name} ${type.fieldName} = new ${type.name}();
        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
        service.persist(${type.fieldName}<@global.idValues />);
        EasyMock.replay(service);

        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.replay(configuration);

        Save save = new Save(service, configuration);
        save.set${type.name}(${type.fieldName});
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        save.set${field.methodName}Id(1);
  </#if>
  <#if field.hasAnnotation("javax.persistence.ManyToMany")>
        save.set${field.methodName}Ids(${field.name}Ids);
  </#if>
</#list>
        String result = save.execute();
        assertEquals("success", result);
        EasyMock.verify(service, configuration);
    }
}