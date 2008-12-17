/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action.account;

import org.easymock.EasyMock;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import static org.junit.Assert.*;
import org.junit.Test;

import org.jcatapult.module.user.BaseTest;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.service.UpdateResult;
import org.jcatapult.module.user.service.UserService;

/**
 * <p>
 * This class tests the edit action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class EditTest extends BaseTest {
    @Test
    public void testLoad() {
        DefaultUser user = new DefaultUser();
        user.setLogin("test");

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.createUser()).andReturn(user);
        EasyMock.expect(userService.currentUser()).andReturn(user);
        EasyMock.replay(userService);

        Edit edit = new Edit();
        edit.setServices(null, null, userService);
        edit.prepare();
        assertEquals("input", edit.get());
        assertSame(user, edit.user);
        EasyMock.verify(userService);
    }

    @Test
    public void testSuccess() {
        DefaultUser user = new DefaultUser();
        user.setLogin("test");

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.createUser()).andReturn(user);
        EasyMock.expect(userService.update(user, "p")).andReturn(UpdateResult.SUCCESS);
        EasyMock.replay(userService);

        Edit edit = new Edit();
        edit.setServices(null, null, userService);
        edit.password = "p";
        edit.passwordConfirm = "pc";
        edit.user = user;
        edit.prepare();
        assertEquals("success", edit.post());
        assertSame(user, edit.user);
        EasyMock.verify(userService);
    }

    @Test
    public void testError() {
        DefaultUser user = new DefaultUser();
        user.setLogin("test");

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.createUser()).andReturn(user);
        EasyMock.expect(userService.update(user, "p")).andReturn(UpdateResult.ERROR);
        EasyMock.replay(userService);

        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        ms.addActionError(MessageScope.REQUEST, "error");
        EasyMock.replay(ms);

        Edit edit = new Edit();
        edit.setServices(ms, null, userService);
        edit.password = "p";
        edit.passwordConfirm = "pc";
        edit.prepare();
        assertEquals("error", edit.post());
        assertSame(user, edit.user);
        EasyMock.verify(userService, ms);
    }
}