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
package org.jcatapult.persistence.txn.jpa;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.jcatapult.persistence.txn.TransactionState;

/**
 * <p>
 * This is the JPA implementation of the transaction state.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JPATransactionState implements TransactionState<EntityTransaction, PersistenceException> {
    private final EntityTransaction txn;
    private final boolean embedded;

    public JPATransactionState(EntityTransaction txn, boolean embedded) {
        this.txn = txn;
        this.embedded = embedded;
    }

    /**
     * @return  The EntityTransaction.
     */
    @Override
    public EntityTransaction wrapped() {
        return txn;
    }

    /**
     * @return  True if this transaction is embedded, false otherwise.
     */
    @Override
    public boolean embedded() {
        return embedded;
    }

    /**
     * Calls the {@link }EntityTransaction#setRollbackOnly()} method.
     */
    @Override
    public void setRollbackOnly() {
        txn.setRollbackOnly();
    }

    /**
     * @return  The result from the {@link EntityTransaction#getRollbackOnly()} method.
     */
    @Override
    public boolean isRollbackOnly() {
        return txn.getRollbackOnly();
    }

    /**
     * Calls the {@link EntityTransaction#commit()} method.
     */
    @Override
    public void commit() {
        txn.commit();
    }

    /**
     * Calls the {@link EntityTransaction#rollback()} method.
     */
    @Override
    public void rollback() {
        txn.rollback();
    }
}
