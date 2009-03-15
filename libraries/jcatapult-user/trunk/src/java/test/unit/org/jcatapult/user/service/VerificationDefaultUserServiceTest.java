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
package org.jcatapult.user.service;

import static org.easymock.EasyMock.*;
import org.jcatapult.config.Configuration;
import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.email.domain.Email;
import org.jcatapult.user.BaseTest;
import org.jcatapult.user.TestUser;
import org.jcatapult.user.TestUserHandler;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.AbstractModule;

/**
 * <p>
 * This class tests the service.
 * </p>
 *
 * @author Scaffolder
 */
public class VerificationDefaultUserServiceTest extends BaseTest {
    @Override
    protected void setupConfigurationAndUserHandlerAndEmail() {
        final Configuration config = createNiceMock(Configuration.class);
        expect(config.getString("jcatapult.user.default-role", "user")).andReturn("user");
        expect(config.getBoolean("jcatapult.user.verify-emails", false)).andReturn(true);
        replay(config);

        addModules(new AbstractModule() {
            public void configure() {
                bind(UserHandler.class).to(TestUserHandler.class);
                bind(Configuration.class).toInstance(config);
            }
        });

        EmailTestHelper.setup(this);
    }

    @Test
    public void testRegisterVerify() throws Exception {
        TestUser user = new TestUser();
        user.setLogin("verify@example.com");
        assertEquals(RegisterResult.PENDING, userService.register(user, "password", "http://www.example.com/verify-email"));
        assertFalse(user.isPartial());
        assertEquals(1, user.getRoles().size());
        assertNotNull(user.getRoles().iterator().next());

        Email email = EmailTestHelper.getEmailResults().poll();
        assertTrue(email.getHtml().contains("http://wwww.example.com/verify-email"));
    }
}