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
package org.jcatapult.module.simpleuser.action;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.jcatapult.module.simpleuser.BaseTest;
import org.jcatapult.module.simpleuser.domain.DefaultUser;
import org.jcatapult.module.simpleuser.service.UserConfiguration;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.security.saved.SavedRequestService;
import org.jcatapult.user.service.RegisterResult;
import org.jcatapult.user.service.UserService;
import static org.junit.Assert.*;
import org.junit.Test;

import net.java.error.ErrorList;

/**
 * <p>
 * This class tests the Register action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class RegisterTest extends BaseTest {

    @Test
    public void testExists() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        UserConfiguration configuration = EasyMock.createStrictMock(UserConfiguration.class);
        EasyMock.expect(configuration.isRegistrationDisabled()).andReturn(false);
        EasyMock.expect(configuration.isVerifyEmails()).andReturn(false);
        EasyMock.replay(configuration);

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.register(user, "password", null)).andReturn(RegisterResult.EXISTS);
        EasyMock.replay(userService);

        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        ms.addFieldError(MessageScope.REQUEST, "user.username", "user.username.exists");
        EasyMock.replay(ms);

        Register register = new Register(null, request);
        register.setServices(ms, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        assertEquals("input", register.post());
        EasyMock.verify(configuration, userService);
    }

    @Test
    public void testError() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        UserConfiguration configuration = EasyMock.createStrictMock(UserConfiguration.class);
        EasyMock.expect(configuration.isRegistrationDisabled()).andReturn(false);
        EasyMock.expect(configuration.isVerifyEmails()).andReturn(false);
        EasyMock.replay(configuration);

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.register(user, "password", null)).andReturn(RegisterResult.ERROR);
        EasyMock.replay(userService);

        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        ms.addActionError(MessageScope.REQUEST, "error");
        EasyMock.replay(ms);

        Register register = new Register(null, request);
        register.setServices(ms, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        assertEquals("error", register.post());
        EasyMock.verify(configuration, userService);
    }

    @Test
    public void testSuccessNoSavedRequest() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        UserConfiguration configuration = EasyMock.createStrictMock(UserConfiguration.class);
        EasyMock.expect(configuration.isRegistrationDisabled()).andReturn(false);
        EasyMock.expect(configuration.isVerifyEmails()).andReturn(false);
        EasyMock.expect(configuration.getRegistrationSuccessURI()).andReturn("/");
        EasyMock.replay(configuration);

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.register(user, "password", null)).andReturn(RegisterResult.SUCCESS);
        EasyMock.replay(userService);

        SavedRequestService savedRequestService = EasyMock.createStrictMock(SavedRequestService.class);
        EasyMock.expect(savedRequestService.processSavedRequest(request)).andReturn(null);
        EasyMock.replay(savedRequestService);

        Register register = new Register(savedRequestService, request);
        register.setServices(null, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        assertEquals("success", register.post());
        assertEquals("/", register.getUri());
        EasyMock.verify(configuration, userService, savedRequestService);
    }

    @Test
    public void testSuccessWithSavedRequest() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        UserConfiguration configuration = EasyMock.createStrictMock(UserConfiguration.class);
        EasyMock.expect(configuration.isRegistrationDisabled()).andReturn(false);
        EasyMock.expect(configuration.isVerifyEmails()).andReturn(false);
        EasyMock.replay(configuration);

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.expect(userService.register(user, "password", null)).andReturn(RegisterResult.SUCCESS);
        EasyMock.replay(userService);

        SavedRequestService savedRequestService = EasyMock.createStrictMock(SavedRequestService.class);
        EasyMock.expect(savedRequestService.processSavedRequest(request)).andReturn("/test");
        EasyMock.replay(savedRequestService);

        Register register = new Register(savedRequestService, request);
        register.setServices(null, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        assertEquals("success", register.post());
        assertEquals("/test", register.getUri());
        EasyMock.verify(configuration, userService, savedRequestService);
    }

    @Test
    public void testValidateNoErrors() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        UserConfiguration configuration = EasyMock.createStrictMock(UserConfiguration.class);
        EasyMock.replay(configuration);

        Map<String, int[]> associations = new HashMap<String, int[]>();

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.replay(userService);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.replay(messageStore);

        Register register = new Register(null, request);
        register.setServices(messageStore, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        register.associations = associations;
        register.validate();
        EasyMock.verify(configuration, userService);
    }

    @Test
    public void testValidateWithErrors() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        UserConfiguration configuration = EasyMock.createStrictMock(UserConfiguration.class);
        EasyMock.replay(configuration);

        ErrorList errors = new ErrorList();
        errors.addError("passwordConfirm", "passwordConfirm.match");

        Map<String, int[]> associations = new HashMap<String, int[]>();

        UserService userService = EasyMock.createStrictMock(UserService.class);
        EasyMock.replay(userService);

        MessageStore messageStore = EasyMock.createStrictMock(MessageStore.class);
        messageStore.addFieldError(MessageScope.REQUEST, "passwordConfirm", "passwordConfirm.match");
        EasyMock.replay(messageStore);

        Register register = new Register(null, request);
        register.setServices(messageStore, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password2";
        register.user = user;
        register.associations = associations;
        register.validate();
        EasyMock.verify(configuration, userService);
    }
}