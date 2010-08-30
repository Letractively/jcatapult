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
package org.jcatapult.persistence.service.jdbc;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * <p>
 * This is the default implementation of the JDBC service. it uses a single named constant
 * to determine if JDBC is enabled. This constant is named <strong>non-jta-data-source</strong>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultJDBCService implements JDBCService {
    private DataSource ds;

    @Inject(optional = true)
    public void setDatasourceName(@Named("non-jta-data-source") String dataSourceName) {
        if (dataSourceName == null) {
            return;
        }

        try {
            InitialContext context = new InitialContext();
            ds = (DataSource) context.lookup(dataSourceName);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSouce() {
        return ds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection setupConnection() {
        Connection c = ConnectionContext.get();
        if (c != null) {
            return c;
        }

        try {
            if (ds == null) {
                return null;
            }

            c = ds.getConnection();
            ConnectionContext.set(c);
            return c;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDownConnection() {
        Connection c = ConnectionContext.get();
        if (c != null) {
            try {
                c.setAutoCommit(true);
                c.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
