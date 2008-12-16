/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.mvc.message.scope.MessageType;
import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import static org.junit.Assert.*;
import org.junit.Test;

import com.inversoft.module.user.BaseIntegrationTest;

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