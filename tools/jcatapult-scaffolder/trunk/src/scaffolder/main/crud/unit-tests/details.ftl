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
 * This class tests the details action.
 * </p>
 *
 * @author  Scaffolder
 */
public class DetailsTest {

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

        Details action = new Details(service, messageStore);
        action.id = 1;

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

        Details action = new Details(service, messageStore);
        action.id = 1;

        String result = action.get();
        assertEquals("success", result);
        EasyMock.verify(service, messageStore);
    }
}