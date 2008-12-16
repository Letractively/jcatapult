/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action.admin.user;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.email.EmailTestHelper;
import static org.junit.Assert.*;
import org.junit.Test;

import com.inversoft.module.user.BaseIntegrationTest;
import com.inversoft.module.user.domain.DefaultUser;
import com.inversoft.module.user.domain.User;

/**
 * <p>
 * This class tests the Delete action.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DeleteIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testDelete() throws IOException, ServletException {
        User user1 = makeUser("delete1@test.com");
        User user2 = makeUser("delete2@test.com");

        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/user/delete").
            withParameter("ids", user1.getId().toString()).
            withParameter("ids", user2.getId().toString()).
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            get();
        assertEquals("/admin/user/", runner.response.getRedirect());

        assertTrue(persistenceService.findById(DefaultUser.class, user1.getId()).isDeleted());
        assertTrue(persistenceService.findById(DefaultUser.class, user2.getId()).isDeleted());
    }
}