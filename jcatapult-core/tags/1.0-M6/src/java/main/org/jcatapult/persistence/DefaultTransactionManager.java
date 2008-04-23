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

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * This class provides a simple transaction management support
 * by leveraging the current persistence context and starting
 * transactions, but also by using the implementation of the
 * {@link TransactionResultProcessor} that is passed to the end
 * method to determine rollbacks.
 * </p>
 *
 * <p>
 * This is a singleton and it uses all of the default implementations
 * in this package.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultTransactionManager implements TransactionManager {
    private final EntityManager entityManager;

    @Inject
    public DefaultTransactionManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    public TransactionState startTransaction() {
        javax.persistence.EntityTransaction txn = entityManager.getTransaction();
        boolean embedded = txn.isActive();
        if (!embedded) {
            txn.begin();
        }

        return new DefaultTransactionState(txn, embedded);
    }

    /**
     * {@inheritDoc}
     */
    public void endTransaction(Object result, Throwable t, TransactionState txn,
            TransactionResultProcessor processor) {
        boolean rollback = processor.rollback(result, t);
        if (rollback && !txn.embedded()) { // Not embedded, roll back
            txn.transaction().rollback();
        } else if (rollback) { // Embedded, set flag
            txn.transaction().setRollbackOnly();
        } else if (!txn.embedded()) { // Not embedded and success
            txn.transaction().commit();
        } // Embedded and success
    }
}