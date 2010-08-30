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

import com.google.inject.Inject;
import org.jcatapult.persistence.txn.TransactionManager;
import org.jcatapult.persistence.txn.TransactionResultProcessor;
import org.jcatapult.persistence.txn.TransactionState;

/**
 * <p>
 * This class provides the transaction handling for JDBC.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JDBCTransactionManager implements TransactionManager<Connection, SQLException> {
    private final Connection connection;

    @Inject
    public JDBCTransactionManager(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    public TransactionState<Connection, SQLException> startTransaction() throws SQLException {
        boolean embedded = !connection.getAutoCommit();
        if (!embedded) {
            connection.setAutoCommit(false);
        }

        return new JDBCTransactionState(connection, embedded);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void endTransaction(Object result, Throwable t, TransactionState<Connection, SQLException> txn,
                               TransactionResultProcessor processor) throws SQLException {
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
