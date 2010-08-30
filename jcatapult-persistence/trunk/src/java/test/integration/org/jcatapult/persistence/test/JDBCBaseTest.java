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
package org.jcatapult.persistence.test;

import javax.sql.RowSet;
import java.sql.SQLException;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.jcatapult.persistence.service.jdbc.ConnectionContext;
import org.jcatapult.test.JCatapultBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * <p>
 * This class is a base class that contains helpful methods for
 * setting up and tearing down JDBC.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Ignore
public abstract class JDBCBaseTest extends JCatapultBaseTest {
    /**
     * Constructs the the DataSource.
     */
    @BeforeClass
    public static void setDataSource() {
        JPATestHelper.initializeJPA(jndi);
    }

    /**
     * Constructs the Connection and puts it in the context.
     */
    @Before
    @Override
    public void setUp() {
        try {
            ConnectionContext.set(JPATestHelper.dataSource.getConnection());
            super.addModules(new AbstractModule() {
                protected void configure() {
                    bindConstant().annotatedWith(Names.named("non-jta-data-source")).to("java:comp/env/jdbc/jcatapult-persistence");
                }
            });
            super.setUp();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the EntityManager and removes it from the context.
     */
    @After
    public void tearDown() {
        try {
            ConnectionContext.get().close();
            ConnectionContext.remove();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs the given query and returns the results in a detached RowSet.
     *
     * @param   query The query to run.
     * @return The results of the query.
     * @throws java.sql.SQLException If the query failed.
     */
    protected RowSet executeQuery(String query) throws SQLException {
        return JPATestHelper.executeQuery(query);
    }
}
