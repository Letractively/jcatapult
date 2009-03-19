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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcatapult.user.BaseTest;
import org.jcatapult.user.TestRole;
import org.jcatapult.user.TestUser;
import org.jcatapult.user.domain.User;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the service.
 * </p>
 *
 * @author Scaffolder
 */
public class DefaultUserServiceTest extends BaseTest {
    @Test
    public void testPersist() throws SQLException {
        clear();
        makeUser("test login");
    }

    @Test
    public void testRegister() throws Exception {
        TestUser user = new TestUser();
        user.setUsername("foo@bar.com");
        assertEquals(RegisterResult.SUCCESS, userService.register(user, "password", null));
        assertFalse(user.isPartial());
        assertEquals(1, user.getRoles().size());
        assertNotNull(user.getRoles().iterator().next());
    }

    @Test
    public void testRegisterDuplicate() throws Exception {
        // This user was persisted in testRegister method
        TestUser user = new TestUser();
        user.setUsername("foo@bar.com");
        assertEquals(RegisterResult.EXISTS, userService.register(user, "password", null));
    }

    @Test
    public void testRegisterPartial() throws Exception {
        TestUser user = new TestUser();
        user.setUsername("partial@bar.com");
        assertEquals(RegisterResult.SUCCESS, userService.registerPartial(user));
        assertTrue(user.isPartial());
        assertEquals(1, user.getRoles().size());
        assertNotNull(user.getRoles().iterator().next());

        user = new TestUser();
        user.setUsername("partial@bar.com");
        assertEquals(RegisterResult.SUCCESS, userService.register(user, "password", null));
        assertFalse(user.isPartial());
        assertEquals(1, user.getRoles().size());
        assertNotNull(user.getRoles().iterator().next());
    }

    @Test
    public void testPersistExisting() throws SQLException {
        clear();
        int adminID = ps.queryFirst(TestRole.class, "select tr from TestRole tr where tr.name = 'admin'").getId();
        int userID = ps.queryFirst(TestRole.class, "select tr from TestRole tr where tr.name = 'user'").getId();

        Map<String, int[]> associations = new HashMap<String, int[]>();
        associations.put("roles", new int[]{adminID, userID});

        TestUser user = makeUser("test login");
        String password = user.getPassword();
        userService.persist(user, associations, "new");
        assertFalse(user.getPassword().equals(password));
    }

    @Test
    public void testUpdate() throws SQLException {
        clear();
        TestUser user = makeUser("test login");
        user.setUsername("changed");

        userService.update(user, "p");

        ps.reload(user);
        assertEquals("changed", user.getUsername());
    }

    @Test
    public void testUpdateNonManagedEntity() throws SQLException {
        clear();
        TestUser user = new TestUser();
        user.setUsername("test-non-managed");
        user.setPassword("test-non-managed");

        try {
            userService.update(user, "p");
            fail("Should have failed.");
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    public void testFind() throws SQLException {
        clear();
        makeUser("test login");

        List<User> list = userService.find(null);
        assertEquals(1, list.size());
        verify((TestUser) list.get(0));
    }

    @Test
    public void testDelete() throws SQLException {
        clear();

        TestUser user = makeUser("test login");
        userService.delete(user.getId());
        TestUser removed = (TestUser) userService.findById(user.getId());
        assertTrue(removed.isDeleted());
    }

    @Test
    public void testDeleteMany() throws SQLException {
        clear();

        TestUser user = makeUser("test login1");
        TestUser user2 = makeUser("test login2");
        TestUser user3 = makeUser("test login3");
        userService.deleteMany(new int[]{user.getId(), user2.getId(), user3.getId()});
        TestUser removed = (TestUser) userService.findById(user.getId());
        assertTrue(removed.isDeleted());
        removed = (TestUser) userService.findById(user2.getId());
        assertTrue(removed.isDeleted());
        removed = (TestUser) userService.findById(user3.getId());
        assertTrue(removed.isDeleted());
    }

    /**
     * Verifies the test User.
     *
     * @param   user The test User.
     */
    private void verify(TestUser user) {
        assertTrue(user.getRoles().contains(new TestRole("admin")));
        assertTrue(user.getRoles().contains(new TestRole("user")));
        assertEquals("test login", user.getUsername());
        assertEquals("S42HhdOgrK0rLUNPnnJVKw==", user.getPassword());
    }
}