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

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.config.Configuration;
import org.jcatapult.mvc.message.scope.MessageType;
import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.email.EmailTestHelper;
import static org.junit.Assert.*;
import org.junit.Test;

import org.jcatapult.module.simpleuser.BaseIntegrationTest;
import org.jcatapult.module.simpleuser.domain.DefaultRole;
import org.jcatapult.module.simpleuser.domain.DefaultUser;

/**
 * <p>
 * This class tests the Register action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class RegisterIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testDisabled() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        Configuration configuration = makeConfiguration(true);

        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/register").
            withMock(Configuration.class, configuration).
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();

        assertEquals(1, runner.messageStore.getActionMessages(MessageType.ERROR).size());
        assertEquals("Registration is currently not allowed.", runner.messageStore.getActionMessages(MessageType.ERROR).get(0));
        assertNull(runner.response.getRedirect());
        assertEquals("anonymous", EnhancedSecurityContext.getCurrentUsername());
    }

    @Test
    public void testDisabledPost() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        Configuration configuration = makeConfiguration(true);
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/register").withMock(Configuration.class, configuration).
            withParameter("user.username", "login@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "password").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();
        assertEquals(1, runner.messageStore.getActionMessages(MessageType.ERROR).size());
        assertEquals("Registration is currently not allowed.", runner.messageStore.getActionMessages(MessageType.ERROR).get(0));
        assertNull(runner.response.getRedirect());
        assertEquals("anonymous", EnhancedSecurityContext.getCurrentUsername());
    }

    @Test
    public void testSuccessfulRegistration() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        Configuration configuration = makeConfiguration(false);
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/register").withMock(Configuration.class, configuration).
            withParameter("user.username", "login@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "password").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();
        System.out.println("Errors are " + runner.messageStore.getFieldErrors());
        System.out.println("Errors are " + runner.messageStore.getActionErrors());
        assertEquals(0, runner.messageStore.getActionMessages(MessageType.ERROR).size());
        assertEquals("/", runner.response.getRedirect());
        assertEquals("login@test.com", EnhancedSecurityContext.getCurrentUsername());
        DefaultUser user = (DefaultUser) EnhancedSecurityContext.getCurrentUser();
        assertTrue(user.getRoles().contains(new DefaultRole("user")));
        assertFalse(user.getRoles().contains(new DefaultRole("admin")));
    }

    @Test
    public void testDuplicateRegistration() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        Configuration configuration = makeConfiguration(false);
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/register").withMock(Configuration.class, configuration).
            withParameter("user.username", "login@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "password").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();
        assertEquals(1, runner.messageStore.getFieldMessages(MessageType.ERROR).size());
        assertEquals("That email is already registered.", runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.username").get(0));
        assertEquals("anonymous", EnhancedSecurityContext.getCurrentUsername());
    }

    @Test
    public void testValidation() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        Configuration configuration = makeConfiguration(false);
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/register").withMock(Configuration.class, configuration).
            withParameter("user.username", "login@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "different").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();

        String result = runner.response.getStream().toString();
        assertTrue(result.contains("html"));
        assertEquals(1, runner.messageStore.getFieldMessages(MessageType.ERROR).size());
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("passwordConfirm").get(0));
        assertEquals("anonymous", EnhancedSecurityContext.getCurrentUsername());
    }
}