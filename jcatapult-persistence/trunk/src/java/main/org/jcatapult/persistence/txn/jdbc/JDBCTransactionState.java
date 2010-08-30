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
package org.jcatapult.persistence.txn.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.jcatapult.persistence.txn.TransactionState;

/**
 * <p>
 * This class models the transaction state for the JDBC connection.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JDBCTransactionState implements TransactionState<Connection, SQLException> {
    private final Connection connection;
    private final boolean embedded;
    private boolean rollbackOnly;

    public JDBCTransactionState(Connection connection, boolean embedded) {
        this.connection = connection;
        this.embedded = embedded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection wrapped() {
        return connection;
    }

    /**
     * @return  True if the transaction was not started by the current scope, but by an outer scope.
     */
    @Override
    public boolean embedded() {
        return embedded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRollbackOnly() {
        rollbackOnly = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    /**
     * Calls the connection's {@link Connection#commit()} method.
     */
    @Override
    public void commit() throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Calls the connection's {@link Connection#rollback()} method.
     */
    @Override
    public void rollback() throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
    }
}
