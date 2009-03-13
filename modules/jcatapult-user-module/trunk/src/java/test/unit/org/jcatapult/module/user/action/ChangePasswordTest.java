/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 */
package org.jcatapult.module.user.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.jcatapult.config.Configuration;
import org.jcatapult.module.user.BaseTest;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.user.service.UserService;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

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