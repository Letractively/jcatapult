/*
 * Copyright (c) 2001-2008, Jcatapult, All Rights Reserved
 */
package org.jcatapult.module.user.action.account;

import org.easymock.EasyMock;
import org.jcatapult.module.user.BaseTest;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.security.spi.EnhancedSecurityContextProvider;
import org.jcatapult.user.service.UpdateResult;
import org.jcatapult.user.service.UserService;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * This class tests the edit action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class EditTest extends BaseTest {
    public DefaultUser user;

    @Before
    public void setup() {
        user = new DefaultUser();
        user.setId(1);
        user.setUsername("test");
        EnhancedSecurityContext.login(user);
    }

    @Before
    public void setupSecurityContext() {
        EnhancedSecurityContext.setProvider(new EnhancedSecurityContextProvider() {
            private DefaultUser user;
            public String getCurrentUsername() {
                return user.getUsername();
            }

            public Object getCurrentUser() {
                return user;
            }

            public void update(Object user) {
                this.user = (DefaultUser) user;
            }

            public void login(Object user) {
                this.user = (DefaultUser) user;
            }

            public void logout() {
                this.user = null;
            }
        });
    }

    @Test
    public void testLoad() {
        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.findByUsername("test")).andReturn(user);
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
        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.findByUsername("test")).andReturn(user);
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
        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.findByUsername("test")).andReturn(user);
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

    @Test
    public void testLoginUpdate() {
        // The security context is setup, so create a new user to simulate multiple requests
        user = new DefaultUser();
        user.setId(1);
        user.setUsername("changed");
        user.setPassword("password");
        
        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.findByUsername("test")).andReturn(user);
        EasyMock.expect(userService.update(user, "p")).andReturn(UpdateResult.SUCCESS);
        EasyMock.replay(userService);

        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.replay(ms);

        assertNotSame(user, EnhancedSecurityContext.getCurrentUser());

        Edit edit = new Edit();
        edit.setServices(ms, null, userService);
        edit.password = "p";
        edit.passwordConfirm = "pc";
        edit.prepare();
        assertEquals("success", edit.post());
        assertSame(user, edit.user);
        EasyMock.verify(userService, ms);

        assertSame(user, EnhancedSecurityContext.getCurrentUser());
    }
}