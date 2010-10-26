/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.txn;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

import com.google.inject.Inject;
import org.jcatapult.persistence.service.jpa.User;
import org.jcatapult.persistence.test.JDBCBaseTest;
import org.jcatapult.persistence.test.JDBCTestHelper;
import org.jcatapult.persistence.test.JPATestHelper;
import org.jcatapult.persistence.txn.annotation.Transactional;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * This class tests the transaction annotation and the defaults at
 * the macro and micro levels.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JDBCTransactionTest extends JDBCBaseTest {
    @Inject public JDBCTestService service;

    @BeforeClass
    public static void setUpJPA() {
        // This will create the tables if this tests is run by itself
        JPATestHelper.initialize(jndi);
    }

    @Test
    public void service() throws SQLException {
        JDBCTestHelper.executeSQL("delete from users");
        service.success();
        RowSet rs = executeQuery("select count(*) from users");
        rs.next();
        assertEquals(1, rs.getLong(1));
        rs = executeQuery("select name from users where name = 'TransactionTest-success'");
        assertTrue(rs.next());
        rs.close();

        try {
            service.failure();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            // Expected
        }
        rs = executeQuery("select count(*) from users");
        rs.next();
        assertEquals(1, rs.getLong(1));
        rs = executeQuery("select name from users where name = 'TransactionTest-failure'");
        assertFalse(rs.next());
        rs.close();

        service.returnValueSuccess();
        rs = executeQuery("select count(*) from users");
        rs.next();
        assertEquals(2, rs.getLong(1));
        rs = executeQuery("select name from users where name = 'TransactionTest-returnValueSuccess'");
        assertTrue(rs.next());
        rs.close();

        service.returnValueFailure();
        rs = executeQuery("select count(*) from users");
        rs.next();
        assertEquals(2, rs.getLong(1));
        rs = executeQuery("select name from users where name = 'TransactionTest-returnValueFailure'");
        assertFalse(rs.next());
        rs.close();
    }

    public static class JDBCTestService {
        private final Connection c;

        @Inject
        public JDBCTestService(Connection c) {
            this.c = c;
        }

        @Transactional()
        public void success() throws SQLException {
            c.createStatement().executeUpdate("insert into users (id, insert_date, name, update_date) values (20001, now(), 'TransactionTest-success', now())");
        }

        @Transactional()
        public void failure() throws SQLException {
            c.createStatement().executeUpdate("insert into users (id, insert_date, name, update_date) values (20002, now(), 'TransactionTest-failure', now())");
            throw new RuntimeException();
        }

        @Transactional(processor = UserProcessor.class)
        public User returnValueSuccess() throws SQLException {
            c.createStatement().executeUpdate("insert into users (id, insert_date, name, update_date) values (20003, now(), 'TransactionTest-returnValueSuccess', now())");
            User user = new User();
            user.setName("TransactionTest-returnValueSuccess");
            return user;
        }

        @Transactional(processor = UserProcessor.class)
        public User returnValueFailure() throws SQLException {
            c.createStatement().executeUpdate("insert into users (id, insert_date, name, update_date) values (20004, now(), 'TransactionTest-returnValueFailure', now())");
            User user = new User();
            user.setName("TransactionTest-returnValueFailure");
            return user;
        }
    }

    public static class UserProcessor extends DefaultTransactionResultProcessor<User> {
        @Override
        public boolean rollback(User result, Throwable throwable) {
            return result.getName().equals("TransactionTest-returnValueFailure") ||
                super.rollback(result, throwable);
        }
    }
}
