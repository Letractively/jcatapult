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
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.email.EmailTestHelper;
import static org.junit.Assert.*;
import org.junit.Test;

import org.jcatapult.module.user.BaseIntegrationTest;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.domain.User;

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