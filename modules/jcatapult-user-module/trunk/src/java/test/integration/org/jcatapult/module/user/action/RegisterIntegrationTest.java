/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action;

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

import org.jcatapult.module.user.BaseIntegrationTest;
import org.jcatapult.module.user.domain.DefaultRole;
import org.jcatapult.module.user.domain.DefaultUser;

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
            withParameter("user.login", "login@test.com").
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
            withParameter("user.login", "login@test.com").
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
            withParameter("user.login", "login@test.com").
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
        assertEquals(1, runner.messageStore.getFieldMessages(MessageType.ERROR).size());
        assertEquals("That email is already registered.", runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.login").get(0));
        assertEquals("anonymous", EnhancedSecurityContext.getCurrentUsername());
    }

    @Test
    public void testValidation() throws IOException, ServletException {
        EnhancedSecurityContext.logout();
        Configuration configuration = makeConfiguration(false);
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/register").withMock(Configuration.class, configuration).
            withParameter("user.login", "login@test.com").
            withParameter("password", "password").
            withParameter("passwordConfirm", "different").
            withParameter("user.name.firstName", "Test").
            withParameter("user.addresses['home'].city", "Broomfield").
            withParameter("user.addresses['home'].postalCode", "80020").
            withParameter("user.addresses['home'].country", "US").
            withParameter("user.addresses['work'].state", "CO").
            withParameter("user.addresses['work'].country", "US").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();

        String result = runner.response.getStream().toString();
        assertTrue(result.contains("html"));
        assertEquals(11, runner.messageStore.getFieldMessages(MessageType.ERROR).size());
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("passwordConfirm").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.name.lastName").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.addresses['home'].street").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.addresses['home'].state").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.addresses['work'].street").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.addresses['work'].city").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.addresses['work'].postalCode").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.addresses['work'].postalCode").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.phoneNumbers['home'].number").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.phoneNumbers['work'].number").get(0));
        assertNotNull(runner.messageStore.getFieldMessages(MessageType.ERROR).get("user.phoneNumbers['cell'].number").get(0));
        assertEquals("anonymous", EnhancedSecurityContext.getCurrentUsername());
    }
}