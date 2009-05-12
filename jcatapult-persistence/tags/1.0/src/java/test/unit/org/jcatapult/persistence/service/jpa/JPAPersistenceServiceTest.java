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
 */
package org.jcatapult.persistence.service.jpa;

import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.sql.RowSet;

import org.jcatapult.persistence.PersistenceBaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.mapNV;

/**
 * <p>
 * This tests the entity bean service.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class JPAPersistenceServiceTest extends PersistenceBaseTest {

    @Test
    public void testReloadAttached() throws Exception {
        clearTable("User");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Fred')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'George')");

        EntityManager em = EntityManagerContext.get();
        User user = (User) em.createQuery("select u from User u where u.name = 'Fred'").getSingleResult();
        user.setName("Brian");

        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        service.reload(user);

        assertEquals("Fred", user.getName());
    }

    @Test
    public void testReloadDetached() throws Exception {
        clearTable("User");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Fred')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'George')");

        EntityManager em = EntityManagerContext.get();
        User user = (User) em.createQuery("select u from User u where u.name = 'Fred'").getSingleResult();
        user.setName("Brian");

        em.clear();
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        try {
            service.reload(user);
            fail("Should have failed because it is detached");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testFindAll() throws Exception {
        clearTable("User");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Fred')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'George')");

        clearTable("SoftDeletableUser");
        executeSQL("insert into SoftDeletableUser (insert_date, update_date, name, deleted) " +
            "values (now(), now(), 'Fred', false)");
        executeSQL("insert into SoftDeletableUser (insert_date, update_date, name, deleted) " +
            "values (now(), now(), 'George', true)");

        // This tests that non-soft delete find all works.
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        List<User> users = service.findAllByType(User.class);
        assertEquals(2, users.size());
        assertEquals("Fred", users.get(0).getName());
        assertEquals("George", users.get(1).getName());

        // This tests that non-soft delete find all works even if true is sent in. That param should
        // be ignored
        users = service.findAllByType(User.class);
        assertEquals(2, users.size());
        assertEquals("Fred", users.get(0).getName());
        assertEquals("George", users.get(1).getName());

        // This tests that soft delete objects work correctly when ignoring inactive
        List<SoftDeletableUser> softDeleteUsers = service.findAllByType(SoftDeletableUser.class, false);
        assertEquals(1, softDeleteUsers.size());
        assertEquals("Fred", softDeleteUsers.get(0).getName());

        // This tests that soft delete objects work correctly when including inactive
        softDeleteUsers = service.findAllByType(SoftDeletableUser.class, true);
        assertEquals(2, softDeleteUsers.size());
        assertEquals("Fred", softDeleteUsers.get(0).getName());
        assertEquals("George", softDeleteUsers.get(1).getName());

        System.out.println("" + users.get(0));
        System.out.println("" + softDeleteUsers.get(0));
    }

    @Test
    public void testFind() throws Exception {
        clearTable("User");
        for (int i = 0; i < 100; i++) {
            executeSQL("insert into User (insert_date, update_date, name) " +
                "values (now(), now(), 'Fred" + i + "')");
        }

        clearTable("SoftDeletableUser");
        for (int i = 0; i < 100; i++) {
            executeSQL("insert into SoftDeletableUser (insert_date, update_date, name, deleted) " +
                "values (now(), now(), 'Fred" + i + "', " + ((i % 2 == 0) ? "false" : "true") + ")");
        }

        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());

        // This tests that we correctly get the paginated results for non-soft delete beans
        for (int i = 0; i < 100; i += 10) {
            List<User> users = service.findByType(User.class, i, 10);
            assertEquals(10, users.size());
            for (int j = 0; j < 10; j++) {
                assertEquals("Fred" + (j + i), users.get(j).getName());
            }
        }

        // This tests that we correctly get the paginated results for non-soft delete beans, even
        // with inactive set to true
        for (int i = 0; i < 100; i += 10) {
            List<User> users = service.findByType(User.class, i, 10);
            assertEquals(10, users.size());
            for (int j = 0; j < 10; j++) {
                assertEquals("Fred" + (j + i), users.get(j).getName());
            }
        }

        // This tests that we correctly get the paginated results for soft delete beans with inactive
        // set to false
        for (int i = 0; i < 100; i += 20) {
            List<SoftDeletableUser> users = service.findByType(SoftDeletableUser.class, i / 2, 10, false);
            assertEquals(10, users.size());
            for (int j = 0; j < 10; j++) {
                assertEquals("Fred" + ((j * 2) + i), users.get(j).getName());
            }
        }

        // This tests that we correctly get the paginated results for soft delete beans with inactive
        // set to true
        for (int i = 0; i < 100; i += 10) {
            List<SoftDeletableUser> users = service.findByType(SoftDeletableUser.class, i, 10, true);
            assertEquals(10, users.size());
            for (int j = 0; j < 10; j++) {
                assertEquals("Fred" + (j + i), users.get(j).getName());
            }
        }
    }

    @Test
    public void testFindById() throws SQLException {
        clearTable("User");
        executeSQL("insert into User (id, insert_date, update_date, name) " +
            "values (1, now(), now(), 'Fred')");
        executeSQL("insert into User (id, insert_date, update_date, name) " +
            "values (2, now(), now(), 'George')");
        executeSQL("insert into User (id, insert_date, update_date, name) " +
            "values (3, now(), now(), 'Alan')");

        // This tests that querying by id works
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        User user = service.findById(User.class, 1);
        assertNotNull(user);
        assertEquals("Fred", user.getName());

        user = service.findById(User.class, 2);
        assertNotNull(user);
        assertEquals("George", user.getName());

        user = service.findById(User.class, 3);
        assertNotNull(user);
        assertEquals("Alan", user.getName());
    }

    @Test
    public void testFindByIdNoVerify() throws SQLException {
        clearTable("User");
        executeSQL("insert into User (id, insert_date, update_date, name) " +
            "values (1, now(), now(), 'Fred')");
        executeSQL("insert into User (id, insert_date, update_date, name) " +
            "values (2, now(), now(), 'George')");
        executeSQL("insert into User (id, insert_date, update_date, name) " +
            "values (3, now(), now(), 'Alan')");

        // This tests that querying by id works
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        service.setVerifyEntityClasses(false);
        User user = service.findById(User.class, 1);
        assertNotNull(user);
        assertEquals("Fred", user.getName());

        user = service.findById(User.class, 2);
        assertNotNull(user);
        assertEquals("George", user.getName());

        user = service.findById(User.class, 3);
        assertNotNull(user);
        assertEquals("Alan", user.getName());
    }

    @Test
    public void testQueryAll() throws Exception {
        clearTable("User");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Fred')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'George')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Alan')");

        // This tests that querying with an orderBy clause works
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        List<User> users = service.queryAll(User.class, "select user from User user order by user.name");
        assertEquals(3, users.size());
        assertEquals("Alan", users.get(0).getName());
        assertEquals("Fred", users.get(1).getName());
        assertEquals("George", users.get(2).getName());
    }

    @Test
    public void testQueryCount() throws Exception {
        clearTable("User");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Fred')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'George')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Alan')");

        // This tests that querying with an orderBy clause works
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        long count = service.queryCount("select count(user) from User user where user.name = ?1", "Alan");
        assertEquals(1, count);

        count = service.queryCount("select count(user) from User user");
        assertEquals(3, count);
    }

    @Test
    public void testQuery() throws Exception {
        clearTable("User");
        char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        for (int i = 25; i >= 0; i--) {
            executeSQL("insert into User (insert_date, update_date, name) " +
                "values (now(), now(), 'Fred" + alphabet[i] + "')");
        }

        // This tests that querying with an orderBy clause works
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        List<User> users = service.query(User.class, "select user from User user order by user.name", 3, 21);
//        System.out.println("List is \n" + users);
        assertEquals(21, users.size());
        for (int i = 0; i < 21; i++) {
            assertEquals("Fred" + alphabet[i + 3], users.get(i).getName());
        }
    }

    @Test
    public void testQueryAllWithNamedParameters() throws Exception {
        clearTable("User");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Fred')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'George')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Alan')");

        // This tests that querying with an orderBy clause works
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        List<User> users = service.queryAllWithNamedParameters(User.class, "select user from User user where name = :name order by user.name",
            mapNV("name", "Alan"));
        assertEquals(1, users.size());
        assertEquals("Alan", users.get(0).getName());
    }

    @Test
    public void testQueryCountWithNamedParameters() throws Exception {
        clearTable("User");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Fred')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'George')");
        executeSQL("insert into User (insert_date, update_date, name) " +
            "values (now(), now(), 'Alan')");

        // This tests that querying with an orderBy clause works
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        long count = service.queryCountWithNamedParameters("select count(user) from User user where user.name = :name",
            mapNV("name", "Alan"));
        assertEquals(1, count);

        count = service.queryCountWithNamedParameters("select count(user) from User user", mapNV());
        assertEquals(3, count);
    }

    @Test
    public void testQueryWithNamedParameters() throws Exception {
        clearTable("User");
        char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        for (int i = 25; i >= 0; i--) {
            executeSQL("insert into User (insert_date, update_date, name) " +
                "values (now(), now(), 'Fred" + alphabet[i] + "')");
        }

        // This tests that querying with an orderBy clause works
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        List<User> users = service.queryWithNamedParameters(User.class, "select user from User user where name like :name order by user.name", 3, 21,
            mapNV("name", "%Fred%"));
        assertEquals(21, users.size());
        for (int i = 0; i < 21; i++) {
            assertEquals("Fred" + alphabet[i + 3], users.get(i).getName());
        }
    }

    @Test
    public void testInsert() throws Exception {
        clearTable("User");
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());

        // Test that the persist works and that it correctly handles the dates using the interceptor
        User user = new User();
        user.setName("Fred");
        service.persist(user);

        user = service.findAllByType(User.class).get(0);
        assertNotNull(user);
        assertEquals("Fred", user.getName());

        // Test the unique key violation
        user = new User();
        user.setName("Fred");
        try {
            service.persist(user);
            fail("Should have failed");
        } catch (EntityExistsException e) {
        }
    }

    @Test
    public void testUpdate() throws Exception {
        testInsert();
        EntityManagerContext.get().clear();

        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());

        // Test that the persist works and that it correctly handles the dates using the interceptor
        User user = service.findAllByType(User.class).get(0);
        user.setName("Barry");
        service.persist(user);
        EntityManagerContext.get().clear();

        user = service.findAllByType(User.class).get(0);
        assertNotNull(user);
        assertEquals("Barry", user.getName());

        // Test update unique key violation
        user = new User();
        user.setName("Manilow");
        service.persist(user);

        user = service.findAllByType(User.class).get(1);
        user.setName("Barry");
        try {
            service.persist(user);
            fail("Should have failed");
        } catch (RollbackException e) {
        }
    }

    @Test
    public void testUpdateDetached() throws Exception {
        testInsert();
        EntityManagerContext.get().clear();

        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());

        // Test that the persist works and that it correctly handles the dates using the interceptor
        User user = service.findAllByType(User.class).get(0);
        EntityManagerContext.get().clear();
        user.setName("Barry");
        service.persist(user);

        user = service.findAllByType(User.class).get(0);
        assertNotNull(user);
        assertEquals("Barry", user.getName());
    }

    @Test
    public void testPersistOuterTransaction() throws Exception {
        clearTable("User");
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());

        EntityManager em = EntityManagerContext.get();
        EntityTransaction et = em.getTransaction();
        et.begin();

        // This should NOT commit because there is a transaction in progress
        User user = new User();
        user.setName("Fred");
        service.persist(user);

        // Check via JDBC that the data is NOT in the database yet
        RowSet rw = executeQuery("select * from User");
        assertFalse(rw.next());
        rw.close();

        // Now commit the txn
        et.commit();

        // Now check again
        rw = executeQuery("select * from User");
        assertTrue(rw.next());
        assertEquals("Fred", rw.getString("name"));
        rw.close();
    }

    @Test
    public void testRemove() throws Exception {
        // Test by id
        doRemove(true, true);
        ensureDeleted();

        // Test by object
        doRemove(false, true);
        ensureDeleted();

        // Test outer transaction by id
        EntityManager em = EntityManagerContext.get();
        EntityTransaction et = em.getTransaction();
        et.begin();
        doRemove(true, false);
        et.commit();
        ensureDeleted();

        // Test outer transaction by object
        et = em.getTransaction();
        et.begin();
        doRemove(false, false);
        et.commit();
        ensureDeleted();
    }

    /**
     * Does the actual removal of the users from the database.
     *
     * @param   id Determines if the ID or Object method should be used.
     * @param   verifyExists Determines if the code should verify that the data was added to the DB
     *          or that it wasn't added to the DB. It won't be added to the DB if there is an outer
     *          transaction occurring.
     * @throws  Exception If things get dicey.
     */
    private void doRemove(boolean id, boolean verifyExists) throws Exception {
        clearTable("User");
        clearTable("SoftDeletableUser");
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());

        User user = new User();
        user.setName("Fred");

        SoftDeletableUser softDeleteUser = new SoftDeletableUser();
        softDeleteUser.setName("Zeus");

        service.persist(user);
        service.persist(softDeleteUser);

        // Ensure the ids got generated
        assertNotNull(user.getId());
        assertNotNull(softDeleteUser.getId());

        if (verifyExists) {
            // Verify the data is in there
            RowSet rw = executeQuery("select * from User");
            assertTrue(rw.next());
            assertEquals("Fred", rw.getString("name"));
            assertFalse(rw.next());
            rw.close();

            rw = executeQuery("select * from SoftDeletableUser where deleted = false");
            assertTrue(rw.next());
            assertEquals("Zeus", rw.getString("name"));
            assertFalse(rw.next());
            rw.close();
        } else {
            // Verify the data has not been committed yet because there is an outer transaction
            RowSet rw = executeQuery("select * from User");
            assertFalse(rw.next());
            rw.close();

            rw = executeQuery("select * from SoftDeletableUser");
            assertFalse(rw.next());
            rw.close();
        }

        // Remove and verify it was removed
        if (!id) {
            service.delete(user);
            service.delete(softDeleteUser);
        } else {
            assertTrue(service.delete(User.class, user.getId()));
            assertTrue(service.delete(SoftDeletableUser.class, softDeleteUser.getId()));
        }
    }

    /**
     * Can be called to ensure that the values were deleted from the DB.
     *
     * @throws  Exception If things get dicey.
     */
    private void ensureDeleted() throws Exception {
        // Ensure it was deleted
        RowSet rw = executeQuery("select * from User");
        assertFalse(rw.next());
        rw.close();

        rw = executeQuery("select * from SoftDeletableUser where deleted = true");
        assertTrue(rw.next());
        assertEquals("Zeus", rw.getString("name"));
        assertFalse(rw.next());
        rw.close();
    }

    @Test
    public void testVerify() {
        JPAPersistenceService service = new JPAPersistenceService(new EntityManagerProxy());
        service.setVerifyEntityClasses(true);
        try {
            service.persist(new BadEntity());
            fail("Should have failed");
        } catch (Exception e) {
        }
    }
}