package org.jcatapult.module.user.action.admin.user;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

import org.jcatapult.module.user.BaseTest;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.service.UserService;

/**
 * <p>
 * This class tests the save action.
 * </p>
 *
 * @author  Scaffolder
 */
@Ignore
public class EditTest extends BaseTest {
    /**
     * Tests edit fails when the id is invalid.
     */
    @Test
    public void testNoEdit() {
        UserService service = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(service.findById(1)).andReturn(null);
        EasyMock.replay(service);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        messageStore.addActionError(MessageScope.REQUEST, "missing.user");
        EasyMock.replay(messageStore);

        Edit edit = new Edit();
        edit.setServices(messageStore, null, service);
        edit.id = 1;
        String result = edit.get();
        assertEquals("error", result);
        EasyMock.verify(service, messageStore);
    }

    /**
     * Tests that all the selected ids are deleted.
     */
    @Test
    public void testEdit() {
        DefaultUser user = new DefaultUser();
        user.setLogin("test");

        Map<String, Integer[]> associations = new HashMap<String, Integer[]>();
        associations.put("roles", new Integer[]{1, 2, 3});

        UserService service = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(service.findById(1)).andReturn(user);
        EasyMock.expect(service.getAssociationIds(user)).andReturn(associations);
        EasyMock.replay(service);

        Edit edit = new Edit();
        edit.setServices(null, null, service);
        edit.id = 1;
        String result = edit.post();
        assertEquals("input", result);
        assertSame(associations, edit.associations);

        EasyMock.verify(service);
    }
}