<#import "/global/macros.ftl" as global>
package ${servicePackage};

import java.util.List;

import org.jcatapult.persistence.test.JPABaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;
import ${type.fullName};
<@global.importFields />

/**
 * <p>
 * This class tests the service.
 * </p>
 *
 * @author Scaffolder
 */
public class ${type.name}ServiceImplTest extends JPABaseTest {
    private ${type.name}Service service;

    @Inject
    public void setService(${type.name}Service service) {
        this.service = service;
    }

    @Test
    public void testPersist() throws Exception {
        <#if type.hasAnnotation("javax.persistence.Table") && type.getAnnotation("javax.persistence.Table").parameters['name']??>
        clearTable("${type.getAnnotation("javax.persistence.Table").parameters['name']}");
        <#else>
        clearTable("${type.name}");
        </#if>
        make${type.name}();
    }

    @Test
    public void testFind() {
        List<${type.name}> list = service.find();
        assertEquals(1, list.size());
        verify(list.get(0));
    }

    @Test
    public void testDelete() {
        ${type.name} ${type.fieldName} = make${type.name}();
        service.delete(${type.fieldName}.getId());
        ${type.name} removed = service.findById(${type.fieldName}.getId());
        assertNull(removed);
    }

    @Test
    public void testDeleteMany() {
        ${type.name} ${type.fieldName} = make${type.name}();
        ${type.name} ${type.fieldName}2 = make${type.name}();
        ${type.name} ${type.fieldName}3 = make${type.name}();
        service.deleteMany(new int[]{${type.fieldName}.getId(), ${type.fieldName}2.getId(), ${type.fieldName}3.getId()});
        ${type.name} removed = service.findById(${type.fieldName}.getId());
        assertNull(removed);
        removed = service.findById(${type.fieldName}2.getId());
        assertNull(removed);
        removed = service.findById(${type.fieldName}3.getId());
        assertNull(removed);
    }

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
    @Test
    public void testGet${field.mainType.pluralName}() {
        List<${field.mainType.name}> ${field.pluralName} = service.get${field.mainType.pluralName}();
        assertNotNull(${field.pluralName});
        assertTrue(${field.pluralName}.size() > 0);
    }

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
    @Test
    public void test${field.genericTypes[0].pluralName}() {
        List<${field.genericTypes[0].name}> ${field.pluralName} = service.get${field.genericTypes[0].pluralName}();
        assertNotNull(${field.pluralName});
        assertTrue(${field.pluralName}.size() > 0);
    }

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
    @Test
    public void test${field.genericTypes[1].pluralName}() {
        List<${field.genericTypes[1].name}> ${field.pluralName} = service.get${field.genericTypes[1].pluralName}();
        assertNotNull(${field.pluralName});
        assertTrue(${field.pluralName}.size() > 0);
    }

  </#if>
</#list>
    /**
     * Creates an ${type.name}. This assumes that all relationship objects have seed values in the database.
     * If this isn't true, they should be created here.
     *
     * @return  The ${type.name}.
     */
    private ${type.name} make${type.name}() {
        ${type.name} ${type.fieldName} = new ${type.name}();
<#list type.allFields as field>
  <#if field.mainType.fullName == "java.lang.String" && !field.hasAnnotation("javax.persistence.Transient")>
        ${type.fieldName}.set${field.methodName}("test ${field.name}");
  </#if>
</#list>
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToMany")>
        Integer[] ${field.name}IDs = new Integer[]{1, 2, 3};
  </#if>
</#list>

        service.persist(${type.fieldName}<@global.idValues />);
        return ${type.fieldName};
    }

    /**
     * Verifies the test ${type.name}.
     *
     * @param   ${type.fieldName} The test ${type.name}.
     */
    private void verify(${type.name} ${type.fieldName}) {
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToMany")>
        assertEquals(1, (int) ${type.fieldName}.get${field.methodName}().get(0).getId());
        assertEquals(2, (int) ${type.fieldName}.get${field.methodName}().get(1).getId());
        assertEquals(3, (int) ${type.fieldName}.get${field.methodName}().get(2).getId());
  <#elseif field.hasAnnotation("javax.persistence.ManyToOne")>
        assertEquals(1, (int) ${type.fieldName}.get${field.methodName}().getId());
  </#if>
</#list>
<#list type.allFields as field>
  <#if field.mainType.fullName == "java.lang.String" && !field.hasAnnotation("javax.persistence.Transient")>
        assertEquals("test ${field.name}", ${type.fieldName}.get${field.methodName}());
  </#if>
</#list>
    }
}