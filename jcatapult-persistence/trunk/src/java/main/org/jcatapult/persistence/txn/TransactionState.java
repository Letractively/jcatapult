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

/**
 * <p>
 * This interface defines the state of the current transaction.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface TransactionState<T, E extends Throwable> {
    /**
     * @return  The wrapped transaction class. This will vary for different ORMs and database connections. JDBC uses the
     *          {@link java.sql.Connection} interface and JPA uses the {@link javax.persistence.EntityTransaction}.
     */
    T wrapped();

    /**
     * @return  Whether or not the transaction is embedded.
     */
    boolean embedded();

    /**
     * This method is called if an embedded transaction fails but should delegate to the outer transaction to rollback.
     */
    void setRollbackOnly();

    /**
     * @return  Whether or not the current transaction has been set to rollback only.
     */
    boolean isRollbackOnly();

    /**
     * Commits the transaction. This method doesn't need to check the embedding state, but doing so will not impact the
     * operation. The {@link TransactionManager} performs these checks.
     *
     * @throws  E Any exception that the underlying transaction API throws.
     */
    void commit() throws E;

    /**
     * Rollsback the transaction. This method doesn't need to check the embedding state, but doing so will not impact the
     * operation. The {@link TransactionManager} performs these checks.
     *
     * @throws  E Any exception that the underlying transaction API throws.
     */
    void rollback() throws E;
}
