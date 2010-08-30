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

import org.jcatapult.persistence.txn.jpa.JPATransactionManager;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the operations that are used by the JPATransactionMethodInterceptor
 * in order to provide AOP transaction support around method invocations. Implementers need
 * not be thread safe because a new transaction manager can be created for each invocation.
 * However, if you want to make your manager a singleton for performance, it will need to
 * be thread safe.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(JPATransactionManager.class)
public interface TransactionManager<T, E extends Throwable> {
    /**
     * Starts a new transaction or joins an existing transaction.
     *
     * @return  The transaction state.
     * @throws  E The exception thrown from the transaction implementations.
     */
    TransactionState<T, E> startTransaction() throws E;

    /**
     * Process the end of the transaction using the information in the transaction itself (i.e.
     * rollback state) and any exception that might have been thrown.
     *
     * @param   result (Optional) The return value from the method or null if the method returns
     *          void or threw an exception.
     * @param   t (Optional) The exception thrown from the method invocation or null if nothing was
     *          thrown and the method completed successfully.
     * @param   transactionState The transaction state from the {@link #startTransaction()} method.
     * @param   processor The transaction result processor that can be used to help determine if the
     *          transaction should be rolled back or not. This is pulled from the annotation.
     *          Implementing classes can ignore this if they like.
     * @throws  E The exception thrown from the transaction implementations.
     */
    void endTransaction(Object result, Throwable t, TransactionState<T, E> transactionState, TransactionResultProcessor processor)
    throws E;
}
