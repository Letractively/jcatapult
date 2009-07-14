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
package org.jcatapult.module.user.action.admin.user;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.email.EmailTestHelper;
import static org.junit.Assert.*;
import org.junit.Test;

import org.jcatapult.module.user.BaseIntegrationTest;

/**
 * <p>
 * This class tests the Add action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class AddIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testRender() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/user/add").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();
        assertNull(runner.response.getRedirect());
        assertTrue(runner.response.getStream().toString().contains("User Admin | Add"));
        assertTrue(runner.response.getStream().toString().contains("<input"));
    }

    @Test
    public void testPost() throws IOException, ServletException {
        EnhancedSecurityContext.logout();

        // Render first
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/user/add").
            withParameter("user.username", "addintegration").
            withParameter("user.email", "add@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "password").
            withParameter("user.name.firstName", "Test").
            withParameter("user.name.lastName", "McGee").
            withParameter("user.companyName", "Jcatapult").
            withParameter("user.addresses['home'].street", "132 Main").
            withParameter("user.addresses['home'].city", "Broomfield").
            withParameter("user.addresses['home'].state", "CO").
            withParameter("user.addresses['home'].postalCode", "80020").
            withParameter("user.addresses['home'].country", "US").
            withParameter("user.addresses['work'].street", "132 Main").
            withParameter("user.addresses['work'].city", "Broomfield").
            withParameter("user.addresses['work'].state", "CO").
            withParameter("user.addresses['work'].postalCode", "80020").
            withParameter("user.addresses['work'].country", "US").
            withParameter("user.phoneNumbers['home'].number", "303-555-1212").
            withParameter("user.phoneNumbers['work'].number", "303-555-1212").
            withParameter("user.phoneNumbers['cell'].number", "303-555-1212").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();
        assertNoErrors(runner);
        assertEquals("/admin/user/", runner.response.getRedirect());
    }

    @Test
    public void testValidationError() throws IOException, ServletException {
        EnhancedSecurityContext.logout();

        // Render first
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/user/add").
            withParameter("user.username", "addintegration").
            withParameter("user.email", "add@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "bad-password").
            withParameter("user.name.firstName", "Test").
            withParameter("user.name.lastName", "McGee").
            withParameter("user.companyName", "Jcatapult").
            withParameter("user.addresses['home'].street", "132 Main").
            withParameter("user.addresses['home'].city", "Broomfield").
            withParameter("user.addresses['home'].state", "CO").
            withParameter("user.addresses['home'].postalCode", "80020").
            withParameter("user.addresses['home'].country", "US").
            withParameter("user.addresses['work'].street", "132 Main").
            withParameter("user.addresses['work'].city", "Broomfield").
            withParameter("user.addresses['work'].state", "CO").
            withParameter("user.addresses['work'].postalCode", "80020").
            withParameter("user.addresses['work'].country", "US").
            withParameter("user.phoneNumbers['home'].number", "303-555-1212").
            withParameter("user.phoneNumbers['work'].number", "303-555-1212").
            withParameter("user.phoneNumbers['cell'].number", "303-555-1212").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();
        System.out.println("Result" + runner.response.getStream().toString());
        assertEquals(3, runner.messageStore.getFieldErrors().size());
        assertNotNull(runner.messageStore.getFieldErrors().get("user.username"));
        assertNotNull(runner.messageStore.getFieldErrors().get("user.email"));
        assertNotNull(runner.messageStore.getFieldErrors().get("passwordConfirm"));
        assertNull(runner.response.getRedirect());
        assertTrue(runner.response.getStream().toString().contains("User Admin | Add"));
        assertTrue(runner.response.getStream().toString().contains("<input"));
    }
}