/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.jcatapult.config.Configuration;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

import com.inversoft.module.user.BaseTest;
import com.inversoft.module.user.domain.DefaultUser;
import com.inversoft.module.user.service.UserService;

/**
 * <p>
 * This class tests the ChangePassword action.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Ignore
public class ChangePasswordTest extends BaseTest {
    @Test
    public void testSuccess() {
        DefaultUser user = new DefaultUser();

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.findByGUID("test-guid")).andReturn(user);
        EasyMock.replay(userService);

        HttpSession session = EasyMock.createStrictMock(HttpSession.class);
        session.setAttribute("password-reset-user", user);
        EasyMock.replay(session);

        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession()).andReturn(session);
        EasyMock.replay(request);

        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.replay(ms);

        ChangePassword cp = new ChangePassword(ms, userService);
        cp.guid = "test-guid";
        assertEquals("input", cp.get());
        EasyMock.verify(session, request);
    }

    @Test
    public void testMissing() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.replay(configuration);

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.findByGUID("test-guid")).andReturn(null);
        EasyMock.replay(userService);

        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        ms.addActionError(MessageScope.REQUEST, "missing");
        EasyMock.replay(ms);

        ChangePassword cp = new ChangePassword(ms, userService);
        cp.guid = "test-guid";
        assertEquals("input", cp.get());
    }
}