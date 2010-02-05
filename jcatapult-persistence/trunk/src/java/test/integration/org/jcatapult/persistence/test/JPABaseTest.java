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
package org.jcatapult.persistence.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.sql.RowSet;

import org.jcatapult.test.JCatapultBaseTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * <p>
 * This class is a base class that contains helpful methods for
 * setting up and tearing down JPA.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Ignore
public abstract class JPABaseTest extends JCatapultBaseTest {
    /**
     * Constructs the EntityManager and puts it in the context.
     */
    public void setUpEntityManager() {
        JPATestHelper.setUpEntityManager();
    }

    /**
     * This can be called in the Test classes constructor in order to set the JPA persistent unit
     * to use. This is the same as the init parameter for the filter. This defaults to <em>punit</em>
     *
     * @param   persistentUnit The persistent unit to use.
     */
    public static void setPersistentUnit(String persistentUnit) {
        JPATestHelper.setPersistentUnit(persistentUnit);
    }

    /**
     * Allows sub-classes to setup different databases. The default is to setup a database connection
     * to the database that has the same name as the project (dashes are replaced by underscores) and
     * using the MySQL connection.
     *
     * @param   databaseName The database name.
     */
    public static void setDatabaseName(String databaseName) {
        JPATestHelper.setDatabaseName(databaseName);
    }

    /**
     * Allows sub-classes to setup different databases. This is a data source so that PostgreSQL or
     * Oracle database can be setup.
     *
     * @param   dataSource The data source to put into the JNDI tree.
     */
    public static void setDataSource(DataSource dataSource) {
        JPATestHelper.setDataSource(dataSource);
    }

    /**
     * Constructs the EntityManagerFactory.
     */
    @BeforeClass
    public static void setUpJPA() {
        JPATestHelper.initializeJPA(jndi);
    }

    /**
     * Constructs the EntityManager and puts it in the context.
     */
    @Before
    @Override
    public void setUp() {
        JPATestHelper.setupForTest();
        super.setUp();
    }

    /**
     * Closes the EntityManager and removes it from the context.
     */
    @After
    public void tearDownEntityManager() {
        JPATestHelper.tearDownFromTest();
    }

    /**
     * Closes the EntityManagerFactory.
     */
    @AfterClass
    public static void tearDownJPA() {
        JPATestHelper.tearDownJPA();
    }

    /**
     * Executes the given SQL script via plain old JDBC. This will be committed to the database.
     * This SQL script should be a SQL99 formatted SQL file that can have any number of statements
     * separated by semi-colons. Comments must be line comments and start with -- (two dashes).
     *
     * @param   script The SQL script to execute.
     * @throws java.sql.SQLException If the execute failed.
     * @throws java.io.IOException If the script file could not be read.
     */
    protected void executeScript(String script) throws SQLException, IOException {
        JPATestHelper.executeScript(script);
    }

    /**
     * Executes the given SQL statement via plain old JDBC. This will be committed to the database.
     * This SQL statement should be an insert, update or delete statement because it uses the
     * executeUpdate method on Statement.
     *
     * @param   sql The SQL to execute.
     * @throws SQLException If the execute failed.
     */
    protected void executeSQL(String sql) throws SQLException {
        JPATestHelper.executeSQL(sql);
    }

    /**
     * Clears all the data from the given table using JDBC.
     *
     * @param   table The Table to clear.
     * @throws SQLException If the clear failed.
     */
    protected void clearTable(String table) throws SQLException {
        JPATestHelper.clearTable(table);
    }

    /**
     * Clears all the data from the table that the given Class is mapped to using JPA.
     *
     * @param   klass The JPA class to get the table name from. This first checks if the Class has
     *          a JPA Table annotation and uses that. Otherwise, it uses the Class's simple name.
     * @throws SQLException If the clear failed.
     */
    protected void clearTable(Class<?> klass) throws SQLException {
        JPATestHelper.clearTable(klass);
    }

    /**
     * Runs the given query and returns the results in a detached RowSet.
     *
     * @param   query The query to run.
     * @return The results of the query.
     * @throws SQLException If the query failed.
     */
    protected RowSet executeQuery(String query) throws SQLException {
        return JPATestHelper.executeQuery(query);
    }

    /**
     * @return A connection.
     * @throws java.sql.SQLException on sql exception
     */
    protected Connection getConnection() throws SQLException {
        return JPATestHelper.getConnection();
    }

    /**
     * <p>
     * First clears the table for the given Entity object. This allows fixture files to load defaults
     * without worrying about unique and primary key collisions.
     * </p>
     *
     * <p>
     * Second, loads the given fixture XML file into a new instance of the given JavaBean using the
     * Java.net commons XML unmarshaller. This then uses an EntityManager from the EntityManagerFactory
     * to persist the JavaBean. This means that the bean must be an Entity.
     * </p>
     *
     * <p>
     * The fixture XML format must have a root element named fixture and then child elements for the
     * objects being created. The name of the child element must be the same as the name of the table
     * that the Entity is associated with. The attributes of the child elements correspond to the
     * properties of the JavaBean. Here's an example for the class: <b>com.example.Role</b>
     * </p>
     *
     * <pre>
     * &lt;fixture>
     *   &lt;role name="User"/>
     *   &lt;role name="Admin"/>
     * &lt;/fixture>
     * </pre>
     *
     * <p>
     * Notice that the ID is left out. This is because JPA freaks out if IDs are set. Therefore, you
     * will have to figure out the IDs manually in your tests.
     * </p>
     *
     * @param   fixture The fixture file. This is relative to the directory of the current test.
     * @param   type The type of objects to create and persist.
     * @throws RuntimeException If anything failed.
     */
    @SuppressWarnings(value = "unchecked")
    protected <T> void loadFixture(String fixture, Class<T> type) throws RuntimeException {
        JPATestHelper.loadFixture(getClass(), fixture, type);
    }
}