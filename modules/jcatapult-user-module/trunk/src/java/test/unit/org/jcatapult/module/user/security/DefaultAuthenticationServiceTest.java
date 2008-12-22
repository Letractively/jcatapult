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
package org.jcatapult.module.user.security;

import org.jcatapult.module.user.BaseTest;
import org.jcatapult.module.user.domain.DefaultUser;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class test the JCatapult security integration.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultAuthenticationServiceTest extends BaseTest {
    private DefaultAuthenticationService service;

    @Inject
    public void setService(DefaultAuthenticationService service) {
        this.service = service;
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        makeUser("test-auth@test.com");
        DefaultUser user = (DefaultUser) service.loadUser("test-auth@test.com", null);
        assertNotNull(user);
        org.junit.Assert.assertEquals("test-auth@test.com", user.getLogin());
        org.junit.Assert.assertEquals(1, user.getRoles().size());
        org.junit.Assert.assertEquals("user", user.getRoles().iterator().next().getName());

        assertNull(service.loadUser("bad-auth@test.com", null));
    }
}