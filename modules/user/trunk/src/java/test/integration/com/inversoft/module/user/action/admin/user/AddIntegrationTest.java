/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action.admin.user;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.email.EmailTestHelper;
import static org.junit.Assert.*;
import org.junit.Test;

import com.inversoft.module.user.BaseIntegrationTest;

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
            withParameter("user.login", "add@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "password").
            withParameter("user.name.firstName", "Test").
            withParameter("user.name.lastName", "McGee").
            withParameter("user.companyName", "Inversoft").
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
        assertEquals("/admin/user/", runner.response.getRedirect());
    }

    @Test
    public void testValidationError() throws IOException, ServletException {
        EnhancedSecurityContext.logout();

        // Render first
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/user/add").
            withParameter("user.login", "add@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "bad-password").
            withParameter("user.name.firstName", "Test").
            withParameter("user.name.lastName", "McGee").
            withParameter("user.companyName", "Inversoft").
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
        assertEquals(1, runner.messageStore.getFieldErrors().size());
        assertEquals("Passwords don't match", runner.messageStore.getFieldErrors().get("passwordConfirm").get(0));
        assertNull(runner.response.getRedirect());
        assertTrue(runner.response.getStream().toString().contains("User Admin | Add"));
        assertTrue(runner.response.getStream().toString().contains("<input"));
    }
}