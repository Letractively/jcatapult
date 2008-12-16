/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action.account;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.email.EmailTestHelper;
import static org.junit.Assert.*;
import org.junit.Test;

import org.jcatapult.module.user.BaseIntegrationTest;
import org.jcatapult.module.user.domain.User;

/**
 * <p>
 * This class tests the Summary action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class SummaryIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testRender() throws IOException, ServletException {
        User user = makeUser("summary");
        EnhancedSecurityContext.login(user);
        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/account/summary").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();
        assertNull(runner.response.getRedirect());
        assertTrue(runner.response.getStream().toString().contains("Account Summary"));
    }
}