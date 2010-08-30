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
package org.jcatapult.persistence.txn;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.sql.RowSet;
import java.sql.SQLException;

import com.google.inject.Inject;
import org.easymock.EasyMock;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.service.jpa.User;
import org.jcatapult.persistence.test.JDBCTestHelper;
import org.jcatapult.persistence.test.JPABaseTest;
import org.jcatapult.persistence.txn.annotation.Transactional;
import org.jcatapult.persistence.txn.jpa.JPATransactionManager;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the transaction annotation and the defaults at the macro and micro levels.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JPATransactionTest extends JPABaseTest {
    private JPATestService service;

    @Inject
    public void setService(JPATestService service) {
        this.service = service;
    }

    @Test
    public void testMarco() throws SQLException, InterruptedException {
        service.success();
        RowSet rs = executeQuery("select name from users where name = 'JPATransactionTest-success'");
        assertTrue(rs.next());
        rs.close();

        rs = executeQuery("select name from users where name = 'JPATransactionTest-failure'");
        assertFalse(rs.next());
        rs.close();
        try {
            service.failure();
        } catch (Exception e) {
            // Expected
        }
        rs = executeQuery("select name from users where name = 'JPATransactionTest-failure'");
        assertFalse(rs.next());
        rs.close();

        service.returnValueSuccess();
        rs = executeQuery("select name from users where name = 'JPATransactionTest-returnValueSuccess'");
        assertTrue(rs.next());
        rs.close();

        service.returnValueFailure();
        rs = executeQuery("select name from users where name = 'JPATransactionTest-returnValueFailure'");
        assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void testMicroResultProcessor() {
        DefaultTransactionResultProcessor processor = new DefaultTransactionResultProcessor();
        assertFalse(processor.rollback(null, new Throwable()));
        assertTrue(processor.rollback(null, new RuntimeException()));
        assertFalse(processor.rollback(new Object(), null));
    }

    @Test
    public void testMicroTransactionManager() {
        EntityTransaction et = EasyMock.createStrictMock(EntityTransaction.class);
        EasyMock.expect(et.isActive()).andReturn(false);
        et.begin();
        EasyMock.replay(et);

        EntityManager em = EasyMock.createStrictMock(EntityManager.class);
        EasyMock.expect(em.getTransaction()).andReturn(et);
        EasyMock.replay(em);

        JPATransactionManager manager = new JPATransactionManager(em);
        TransactionState state = manager.startTransaction();
        assertSame(et, state.wrapped());
        assertFalse(state.embedded());

        EasyMock.verify(et, em);
    }

    @Test
    public void testMicroTransactionManagerEmbedded() {
        EntityTransaction et = EasyMock.createStrictMock(EntityTransaction.class);
        EasyMock.expect(et.isActive()).andReturn(true);
        EasyMock.replay(et);

        EntityManager em = EasyMock.createStrictMock(EntityManager.class);
        EasyMock.expect(em.getTransaction()).andReturn(et);
        EasyMock.replay(em);

        JPATransactionManager manager = new JPATransactionManager(em);
        TransactionState state = manager.startTransaction();
        assertSame(et, state.wrapped());
        assertTrue(state.embedded());

        EasyMock.verify(et, em);
    }

    public static class JPATestService {
        private final PersistenceService persistenceService;

        @Inject
        public JPATestService(PersistenceService persistenceService) {
            this.persistenceService = persistenceService;
        }

        @Transactional()
        public void success() throws SQLException {
            User user = new User();
            user.setName("JPATransactionTest-success");
            persistenceService.persist(user);

            // Verify that another session can't see the data
            RowSet rs = JDBCTestHelper.executeQuery("select name from users where name = 'JPATransactionTest-success'");
            assertFalse(rs.next());
            rs.close();
        }

        @Transactional()
        public void failure() throws InterruptedException {
            User user = new User();
            user.setName("JPATransactionTest-failure");
            persistenceService.persist(user);
            try {
                RowSet rs = JDBCTestHelper.executeQuery("select name from users where name = 'JPATransactionTest-failure'");
                assertFalse(rs.next());
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throw new RuntimeException();
        }

        @Transactional(processor = UserProcessor.class)
        public User returnValueSuccess() {
            User user = new User();
            user.setName("JPATransactionTest-returnValueSuccess");
            persistenceService.persist(user);
            return user;
        }

        @Transactional(processor = UserProcessor.class)
        public User returnValueFailure() {
            User user = new User();
            user.setName("JPATransactionTest-returnValueFailure");
            persistenceService.persist(user);
            return user;
        }
    }

    public static class UserProcessor extends DefaultTransactionResultProcessor<User> {
        @Override
        public boolean rollback(User result, Throwable throwable) {
            return result.getName().equals("JPATransactionTest-returnValueFailure") ||
                super.rollback(result, throwable);
        }
    }
}
