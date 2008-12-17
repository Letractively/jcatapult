<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.junit.Test;

import ${type.fullName};
import ${servicePackage}.${type.name}Service;
<@global.importFields />

/**
 * <p>
 * This class tests the edit action.
 * </p>
 *
 * @author  Scaffolder
 */
public class EditTest {

    /**
     * Tests get.
     */
    @Test
    public void testGetMissing() {
        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
        EasyMock.expect(service.findById(1)).andReturn(null);
        EasyMock.replay(service);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        messageStore.addActionError(MessageScope.FLASH, "missing");
        EasyMock.replay(messageStore);

        Edit action = new Edit();
        action.id = 1;
        action.setServices(service, messageStore);

        String result = action.get();
        assertEquals("error", result);
        EasyMock.verify(service, messageStore);
    }

    /**
     * Tests get.
     */
    @Test
    public void testGet() {
        ${type.name} ${type.fieldName} = new ${type.name}();
        ${type.name}Service service = EasyMock.createStrictMock(${type.name}Service.class);
        EasyMock.expect(service.findById(1)).andReturn(${type.fieldName});
        EasyMock.replay(service);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.replay(messageStore);

        Edit action = new Edit();
        action.setServices(service, messageStore);
        action.id = 1;

        String result = action.get();
        assertEquals("input", result);
        EasyMock.verify(service, messageStore);
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

        Edit action = new Edit();
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