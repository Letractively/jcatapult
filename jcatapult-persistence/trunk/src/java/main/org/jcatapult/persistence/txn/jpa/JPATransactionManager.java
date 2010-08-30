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
package org.jcatapult.persistence.txn.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import com.google.inject.Inject;
import org.jcatapult.persistence.txn.TransactionManager;
import org.jcatapult.persistence.txn.TransactionResultProcessor;
import org.jcatapult.persistence.txn.TransactionState;

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
 * This is a not a singleton since it gets injected with the
 * current EntityManager.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JPATransactionManager implements TransactionManager<EntityTransaction, PersistenceException> {
    private final EntityManager entityManager;

    @Inject
    public JPATransactionManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    public TransactionState<EntityTransaction, PersistenceException> startTransaction() {
        EntityTransaction txn = entityManager.getTransaction();
        boolean embedded = txn.isActive();
        if (!embedded) {
            txn.begin();
        }

        return new JPATransactionState(txn, embedded);
    }

    /**
     * {@inheritDoc}
     */
    public void endTransaction(Object result, Throwable t, TransactionState<EntityTransaction, PersistenceException> txn,
                               TransactionResultProcessor processor) {
        boolean rollback = processor.rollback(result, t) || txn.isRollbackOnly();
        if (rollback && !txn.embedded()) { // Not embedded, roll back
            txn.rollback();
        } else if (rollback) { // Embedded, set flag
            txn.setRollbackOnly();
        } else if (!txn.embedded()) { // Not embedded and success
            txn.commit();
        } // Embedded and success
    }
}
