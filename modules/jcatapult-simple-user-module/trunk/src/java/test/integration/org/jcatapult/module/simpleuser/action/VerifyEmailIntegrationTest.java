/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
 */
package org.jcatapult.module.simpleuser.action;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.config.Configuration;
import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.module.simpleuser.BaseIntegrationTest;
import org.jcatapult.module.simpleuser.domain.DefaultUser;
import org.jcatapult.mvc.message.scope.MessageType;
import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the verify email action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class VerifyEmailIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testVerifySuccess() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        Configuration configuration = makeConfiguration(false);
        DefaultUser user = makeUser("test-verify-email@test.com");
        user.setVerified(false);
        user.setGuid("test-verify-email");
        persistenceService.persist(user);

        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/verify-email").
            withMock(Configuration.class, configuration).
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            withParameter("guid", user.getGuid()).
            get();

        assertEquals(0, runner.messageStore.getActionMessages(MessageType.ERROR).size());
        assertEquals("email-verified", runner.response.getRedirect());

        persistenceService.reload(user);
        assertTrue(user.isVerified());
    }

    @Test
    public void testVerifyBadGUID() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        Configuration configuration = makeConfiguration(false);
        DefaultUser user = makeUser("test-verify-email-bad-guid@test.com");
        user.setVerified(false);
        persistenceService.persist(user);

        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/verify-email").
            withMock(Configuration.class, configuration).
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            withParameter("guid", "bad-guid").
            get();

        assertEquals(1, runner.messageStore.getActionMessages(MessageType.ERROR).size());
        assertNull(runner.response.getRedirect());
        assertTrue(runner.response.getStream().toString().contains("<form"));

        persistenceService.reload(user);
        assertFalse(user.isVerified());
    }

    @Test
    public void testResend() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        Configuration configuration = makeConfiguration(false);
        DefaultUser user = makeUser("test-verify-email-resend@test.com");
        user.setVerified(false);
        user.setGuid("test-verify-email-resend");
        persistenceService.persist(user);

        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/verify-email").
            withMock(Configuration.class, configuration).
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            withParameter("username", "test-verify-email-resend@test.com").
            post();

        assertEquals("verification-email-sent", runner.response.getRedirect());

        persistenceService.reload(user);
        assertFalse(user.isVerified());
        assertFalse(user.getGuid().equals("test-verify-email-resend"));
        assertEquals(1, EmailTestHelper.getEmailResults().size());
    }
}