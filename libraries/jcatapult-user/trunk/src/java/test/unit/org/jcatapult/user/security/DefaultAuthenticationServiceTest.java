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
package org.jcatapult.user.security;

import java.sql.SQLException;

import org.jcatapult.user.BaseTest;
import org.jcatapult.user.TestRole;
import org.jcatapult.user.TestUser;
import static org.junit.Assert.*;
import org.junit.Before;
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
    @Inject public DefaultAuthenticationService service;

    @Before
    public void createRoles() throws SQLException {
        clearTable("test_user_test_role");
        clearTable("test_user");
        clearTable("test_role");

        ps.persist(new TestRole("user"));
        ps.persist(new TestRole("admin"));
    }

    @Test
    public void testLoadUserByUsername() throws Exception {
        makeUser("test-auth@test.com");
        ps.clearCache();
        
        TestUser user = (TestUser) service.loadUser("test-auth@test.com", null);
        assertNotNull(user);
        assertEquals("test-auth@test.com", user.getLogin());
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(new TestRole("user")));
        assertTrue(user.getRoles().contains(new TestRole("admin")));

        assertNull(service.loadUser("bad-auth@test.com", null));
    }

    @Test
    public void testNotVerified() throws Exception {
        TestUser user = makeUser("test-auth-not-verified@test.com");
        user.setVerified(false);
        ps.persist(user);
        ps.clearCache();

        try {
            service.loadUser("test-auth-not-verified@test.com", null);
        } catch (Exception e) {
            assertEquals("not-verified", e.getMessage());
        }
    }

    @Test
    public void testExpired() throws Exception {
        TestUser user = makeUser("test-auth-expired@test.com");
        user.setExpired(true);
        ps.persist(user);
        ps.clearCache();

        try {
            service.loadUser("test-auth-expired@test.com", null);
        } catch (Exception e) {
            assertEquals("expired", e.getMessage());
        }
    }

    @Test
    public void testLocked() throws Exception {
        TestUser user = makeUser("test-auth-locked@test.com");
        user.setLocked(true);
        ps.persist(user);
        ps.clearCache();

        try {
            service.loadUser("test-auth-locked@test.com", null);
        } catch (Exception e) {
            assertEquals("locked", e.getMessage());
        }
    }

    @Test
    public void testPasswordExpired() throws Exception {
        TestUser user = makeUser("test-auth-password-expired@test.com");
        user.setPasswordExpired(true);
        ps.persist(user);
        ps.clearCache();

        try {
            service.loadUser("test-auth-password-expired@test.com", null);
        } catch (Exception e) {
            assertEquals("password-expired", e.getMessage());
        }
    }
}