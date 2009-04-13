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
package org.jcatapult.module.simpleuser.action.admin.user;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.module.simpleuser.BaseIntegrationTest;
import org.jcatapult.module.simpleuser.domain.DefaultUser;
import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the Edit action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class EditIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testRender() throws IOException, ServletException {
        DefaultUser user = makeUser("test-edit-integration-get");

        EnhancedSecurityContext.logout();
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/user/edit/" + user.getId()).
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();
        assertNull(runner.response.getRedirect());
        assertTrue(runner.response.getStream().toString().contains("User Admin | Edit"));
        assertTrue(runner.response.getStream().toString().contains("<form action=\"/admin/user/edit/" + user.getId() + ""));
        assertTrue(runner.response.getStream().toString().contains("<input"));
    }

    @Test
    public void testPost() throws IOException, ServletException {
        DefaultUser user = makeUser("test-edit-integration-post");

        EnhancedSecurityContext.logout();

        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/user/edit/" + user.getId()).
            withParameter("user.email", "edit@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "password").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();
        assertEquals("/admin/user/", runner.response.getRedirect());
    }

    @Test
    public void testValidationError() throws IOException, ServletException {
        DefaultUser user = makeUser("test-edit-integration-validation");

        EnhancedSecurityContext.logout();

        // Render first
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/user/edit/" + user.getId()).
            withParameter("user.email", "edit-validation@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "bad-password").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();
        System.out.println("Result" + runner.response.getStream().toString());
        assertEquals(1, runner.messageStore.getFieldErrors().size());
        assertEquals("Passwords don't match", runner.messageStore.getFieldErrors().get("passwordConfirm").get(0));
        assertNull(runner.response.getRedirect());
        assertTrue(runner.response.getStream().toString().contains("User Admin | Edit"));
        assertTrue(runner.response.getStream().toString().contains("<form action=\"/admin/user/edit/" + user.getId() + ""));
        assertTrue(runner.response.getStream().toString().contains("<input"));
    }
}