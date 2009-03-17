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
package org.jcatapult.module.simpleuser.domain;

import org.jcatapult.module.simpleuser.BaseTest;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the User entity.
 * </p>
 *
 * @author James Humphrey
 */
public class UserTest extends BaseTest {
    @Test
    public void testDuplicates() {
        makeUser("test-dups@test.com");
        DefaultRole role = persistenceService.findById(DefaultRole.class, 1);
        DefaultUser user = new DefaultUser();
        user.setUsername("test-dups@test.com");
        user.setGuid("test uid");
        user.setPassword("blah blah");
        user.addRole(role);
        try {
            persistenceService.persist(user);
            fail("Should have failed");
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    public void testContactInfo() {
        DefaultRole role = persistenceService.findById(DefaultRole.class, 1);
        DefaultUser user = new DefaultUser();
        user.setUsername("test-insert@test.com");
        user.setGuid("test uid");
        user.setPassword("blah blah");
        user.addRole(role);

        persistenceService.persist(user);

        persistenceService.clearCache();

        user = persistenceService.queryFirst(DefaultUser.class, "select u from DefaultUser u where u.email = ?1", "test-insert@test.com");
        Assert.assertEquals("test uid", user.getGuid());
        Assert.assertEquals("blah blah", user.getPassword());
        Assert.assertEquals(1, user.getRoles().size());
        Assert.assertEquals("user", user.getRoles().iterator().next().getName());
        Assert.assertFalse(user.isDeleted());
        Assert.assertFalse(user.isAdmin());
        Assert.assertFalse(user.isExpired());
        Assert.assertFalse(user.isLocked());
        Assert.assertFalse(user.isPasswordExpired());
    }
}