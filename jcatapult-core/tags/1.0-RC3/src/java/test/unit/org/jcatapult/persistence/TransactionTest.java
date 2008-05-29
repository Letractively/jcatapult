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
package org.jcatapult.persistence;

import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.sql.RowSet;

import org.easymock.EasyMock;
import org.jcatapult.jpa.User;
import org.jcatapult.jpa.JPATransactionManager;
import org.jcatapult.persistence.annotation.Transactional;
import org.jcatapult.test.JPABaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the transactin annotation and the defaults at
 * the macro and micro levels.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class TransactionTest extends JPABaseTest {
    private JPATestService service;

    @Inject
    public void setService(JPATestService service) {
        this.service = service;
    }

    @Test
    public void testMarco() throws SQLException {
        service.success();
        RowSet rs = executeQuery("select name from User where name = 'TransactionTest-success'");
        assertTrue(rs.next());
        rs.close();

        try {
            service.failure();
        } catch (Exception e) {
            // Expected
        }
        rs = executeQuery("select name from User where name = 'TransactionTest-failure'");
        assertFalse(rs.next());
        rs.close();

        service.returnValueSuccess();
        rs = executeQuery("select name from User where name = 'TransactionTest-returnValueSuccess'");
        assertTrue(rs.next());
        rs.close();

        service.returnValueFailure();
        rs = executeQuery("select name from User where name = 'TransactionTest-returnValueFailure'");
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
        assertSame(et, state.transaction());
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
        assertSame(et, state.transaction());
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
        public void success() {
            User user = new User();
            user.setName("TransactionTest-success");
            persistenceService.persist(user);
        }

        @Transactional()
        public void failure() {
            User user = new User();
            user.setName("TransactionTest-failure");
            persistenceService.persist(user);
            throw new RuntimeException();
        }

        @Transactional(processor = UserProcessor.class)
        public User returnValueSuccess() {
            User user = new User();
            user.setName("TransactionTest-returnValueSuccess");
            persistenceService.persist(user);
            return user;
        }

        @Transactional(processor = UserProcessor.class)
        public User returnValueFailure() {
            User user = new User();
            user.setName("TransactionTest-returnValueFailure");
            persistenceService.persist(user);
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