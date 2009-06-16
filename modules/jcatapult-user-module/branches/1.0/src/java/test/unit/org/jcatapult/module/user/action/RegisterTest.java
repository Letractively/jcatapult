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

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;
import org.jcatapult.module.user.BaseTest;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.service.UserConfiguration;
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

        UserConfiguration configuration = createStrictMock(UserConfiguration.class);
        expect(configuration.isRegistrationDisabled()).andReturn(false);
        expect(configuration.isVerifyEmails()).andReturn(false);
        replay(configuration);

        UserService userService = createStrictMock(UserService.class);
        expect(userService.register(user, "password", null)).andReturn(RegisterResult.EXISTS);
        replay(userService);

        MessageStore ms = createStrictMock(MessageStore.class);
        ms.addFieldError(MessageScope.REQUEST, "user.username", "user.username.exists");
        ms.addFieldError(MessageScope.REQUEST, "user.email", "user.email.exists");
        replay(ms);

        Register register = new Register(null, request);
        register.setServices(ms, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        assertEquals("input", register.post());
        verify(configuration, userService);
    }

    @Test
    public void testError() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        UserConfiguration configuration = createStrictMock(UserConfiguration.class);
        expect(configuration.isRegistrationDisabled()).andReturn(false);
        expect(configuration.isVerifyEmails()).andReturn(false);
        replay(configuration);

        UserService userService = createStrictMock(UserService.class);
        expect(userService.register(user, "password", null)).andReturn(RegisterResult.ERROR);
        replay(userService);

        MessageStore ms = createStrictMock(MessageStore.class);
        ms.addActionError(MessageScope.REQUEST, "error");
        replay(ms);

        Register register = new Register(null, request);
        register.setServices(ms, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        assertEquals("error", register.post());
        verify(configuration, userService);
    }

    @Test
    public void testSuccessNoSavedRequest() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        UserConfiguration configuration = createStrictMock(UserConfiguration.class);
        expect(configuration.isRegistrationDisabled()).andReturn(false);
        expect(configuration.isVerifyEmails()).andReturn(false);
        expect(configuration.getRegistrationSuccessURI()).andReturn("/");
        replay(configuration);

        UserService userService = createStrictMock(UserService.class);
        expect(userService.register(user, "password", null)).andReturn(RegisterResult.SUCCESS);
        replay(userService);

        SavedRequestService savedRequestService = createStrictMock(SavedRequestService.class);
        expect(savedRequestService.processSavedRequest(request)).andReturn(null);
        replay(savedRequestService);

        Register register = new Register(savedRequestService, request);
        register.setServices(null, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        assertEquals("success", register.post());
        assertEquals("/", register.uri);
        verify(configuration, userService, savedRequestService);
    }

    @Test
    public void testSuccessWithSavedRequest() {
        DefaultUser user = new DefaultUser();
        user.setUsername("test");

        UserConfiguration configuration = createStrictMock(UserConfiguration.class);
        expect(configuration.isRegistrationDisabled()).andReturn(false);
        expect(configuration.isVerifyEmails()).andReturn(false);
        replay(configuration);

        UserService userService = createStrictMock(UserService.class);
        expect(userService.register(user, "password", null)).andReturn(RegisterResult.SUCCESS);
        replay(userService);

        SavedRequestService savedRequestService = createStrictMock(SavedRequestService.class);
        expect(savedRequestService.processSavedRequest(request)).andReturn("/test");
        replay(savedRequestService);

        Register register = new Register(savedRequestService, request);
        register.setServices(null, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        assertEquals("success", register.post());
        assertEquals("/test", register.uri);
        verify(configuration, userService, savedRequestService);
    }

    @Test
    public void testValidateNoErrors() {
        DefaultUser user = new DefaultUser();
        user.setEmail("test");

        UserConfiguration configuration = createStrictMock(UserConfiguration.class);
        expect(configuration.isUsernameSameAsEmail()).andReturn(true);
        replay(configuration);

        Map<String, int[]> associations = new HashMap<String, int[]>();

        UserService userService = createStrictMock(UserService.class);
        expect(userService.validate(user, associations, false, "password", "password")).andReturn(new ErrorList());
        replay(userService);

        MessageStore messageStore = createStrictMock(MessageStore.class);
        replay(messageStore);

        Register register = new Register(null, request);
        register.setServices(messageStore, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "password";
        register.user = user;
        register.associations = associations;
        register.validate();
        verify(configuration, userService);
    }

    @Test
    public void testValidateWithErrors() {
        DefaultUser user = new DefaultUser();
        user.setEmail("test");

        UserConfiguration configuration = createStrictMock(UserConfiguration.class);
        expect(configuration.isUsernameSameAsEmail()).andReturn(true);
        replay(configuration);

        ErrorList errors = new ErrorList();
        errors.addError("passwordConfirm", "passwordConfirm.match");

        Map<String, int[]> associations = new HashMap<String, int[]>();

        UserService userService = createStrictMock(UserService.class);
        expect(userService.validate(user, associations, false, "password", "passwordconfirm")).andReturn(errors);
        replay(userService);

        MessageStore messageStore = createStrictMock(MessageStore.class);
        messageStore.addFieldError(MessageScope.REQUEST, "passwordConfirm", "passwordConfirm.match");
        replay(messageStore);

        Register register = new Register(null, request);
        register.setServices(messageStore, configuration, userService);
        register.password = "password";
        register.passwordConfirm = "passwordconfirm";
        register.user = user;
        register.associations = associations;
        register.validate();
        verify(configuration, userService);
    }
}