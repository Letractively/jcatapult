/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action.account;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.email.EmailTestHelper;
import static org.junit.Assert.*;
import org.junit.Test;

import com.inversoft.module.user.BaseIntegrationTest;
import com.inversoft.module.user.domain.DefaultUser;
import com.inversoft.module.user.domain.User;

/**
 * <p>
 * This class tests the Summary action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class EditIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testRender() throws IOException, ServletException {
        User user = makeUser("edit");
        EnhancedSecurityContext.login(user);
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/account/edit").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();
        assertNull(runner.response.getRedirect());
        assertTrue(runner.response.getStream().toString().contains("My Account | Update"));
        assertTrue(runner.response.getStream().toString().contains("<input"));
    }

    @Test
    public void testUpdateNoPassword() throws IOException, ServletException {
        User user = makeUser("edit-update@test.com");
        EnhancedSecurityContext.login(user);

        // Render first
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/account/edit").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();

        // Then update
        runner.test("/account/edit").
            withParameter("user.login", "edit-update-new@test.com").
            withParameter("user.phoneNumbers['work'].number", "303-555-1212").
            withParameter("user.phoneNumbers['cell'].number", "303-555-1212").
            withParameter("user.phoneNumbers['home'].number", "303-555-1212").
            withParameter("user.name.firstName", "Test").
            withParameter("user.name.lastName", "Testerson").
            withParameter("user.companyName", "JCatapult").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();

        System.out.println("errors are " + runner.messageStore.getFieldErrors());
        System.out.println("errors are " + runner.messageStore.getActionErrors());
        user = persistenceService.findById(DefaultUser.class, user.getId());
        assertEquals("summary", runner.response.getRedirect());
        assertEquals("test-password", user.getPassword());
    }

    @Test
    public void testUpdatePassword() throws IOException, ServletException {
        User user = makeUser("edit-update-password@test.com");
        EnhancedSecurityContext.login(user);

        // Render first
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/account/edit").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();

        // Then update
        runner.test("/account/edit").
            withParameter("user.login", "edit-update-password@test.com").
            withParameter("user.phoneNumbers['work'].number", "303-555-1212").
            withParameter("user.phoneNumbers['cell'].number", "303-555-1212").
            withParameter("user.phoneNumbers['home'].number", "303-555-1212").
            withParameter("user.name.firstName", "Test").
            withParameter("user.name.lastName", "Testerson").
            withParameter("user.companyName", "JCatapult").
            withParameter("password", "new-password").
            withParameter("passwordConfirm", "new-password").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();

        System.out.println("errors are " + runner.messageStore.getFieldErrors());
        System.out.println("errors are " + runner.messageStore.getActionErrors());
        user = persistenceService.findById(DefaultUser.class, user.getId());
        assertEquals("summary", runner.response.getRedirect());
        assertEquals("w1CXrvjkekaU5zH7HAxMHw==", user.getPassword());
    }
}