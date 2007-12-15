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
 */
package org.jcatapult.migration.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * User: jhumphrey
 * Date: Nov 26, 2007
 */
public abstract class BaseDatabaseProvider implements DatabaseProvider {

    private static final Logger logger = Logger.getLogger(BaseDatabaseProvider.class.getName());

    protected String url;

    private String dbDriver;

    protected BaseDatabaseProvider(String url) {
        this.url = url;
        this.dbDriver = getDriverClassname();
        loadDriver();
    }

    /**
     * Loads the db driver
     */
    private void loadDriver() {
        logger.info("Loading database driver [" + dbDriver + "]");
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Unable to load driver [com.mysql.jdbc.Driver].  Please make sure it is in your classpath", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    public Connection getConnection() {
        logger.info("Getting connection to [" + url + "]");
        Connection c = null;
        try {
            c = DriverManager.getConnection(url);
        } catch (SQLException se) {
            logger.log(Level.SEVERE, "Unable to connect to database url  [" + url + "]", se);
            throw new RuntimeException(se);
        }

        return c;
    }

    /**
     * {@inheritDoc}
     */
    public DataSource getDatasource() {
        return new DataSource() {
            public Connection getConnection() throws SQLException {
                return BaseDatabaseProvider.this.getConnection();
            }

            public Connection getConnection(String s, String s1) throws SQLException {
                throw new RuntimeException("this method is not yet implemnented");
            }

            public PrintWriter getLogWriter() throws SQLException {
                throw new RuntimeException("this method is not yet implemnented");
            }

            public void setLogWriter(PrintWriter printWriter) throws SQLException {
                throw new RuntimeException("this method is not yet implemnented");
            }

            public void setLoginTimeout(int i) throws SQLException {
                throw new RuntimeException("this method is not yet implemnented");
            }

            public int getLoginTimeout() throws SQLException {
                throw new RuntimeException("this method is not yet implemnented");
            }

            public <T> T unwrap(Class<T> tClass) throws SQLException {
                throw new RuntimeException("this method is not yet implemnented");
            }

            public boolean isWrapperFor(Class<?> aClass) throws SQLException {
                throw new RuntimeException("this method is not yet implemnented");
            }
        };
    }

    /**
     * Gets the driver classname
     *
     * @return the driver classname
     */
    protected abstract String getDriverClassname();
}
