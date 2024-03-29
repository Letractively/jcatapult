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

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.mvc.message.scope.MessageType;
import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import static org.junit.Assert.*;
import org.junit.Test;

import org.jcatapult.module.user.BaseIntegrationTest;

/**
 * <p>
 * This class tests the change password action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ChangePasswordIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testMissing() throws IOException, ServletException {
        makeUser("change-password");
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/change-password").
            withParameter("guid", "wrong").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();

        assertEquals(1, runner.messageStore.getActionMessages(MessageType.ERROR).size());
        assertEquals("Unable to locate your account. Try using the Forgot password link again to reset your password.",
            runner.messageStore.getActionMessages(MessageType.ERROR).get(0));
        assertNull(runner.response.getRedirect());
        assertFalse(runner.response.getStream().toString().contains("<input"));
        assertEquals("anonymous", EnhancedSecurityContext.getCurrentUsername());
    }

    @Test
    public void testRender() throws IOException, ServletException {
        makeUser("change-password2");
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/change-password").
            withParameter("guid", "test-guid").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();
        assertNull(runner.response.getRedirect());
        assertTrue(runner.response.getStream().toString().contains("<input"));
    }
}