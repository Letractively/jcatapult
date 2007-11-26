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
package org.jcatapult.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

import org.jcatapult.database.DatabaseTools;
import org.jcatapult.guice.JPAModule;
import org.jcatapult.jpa.EntityManagerContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

import com.google.inject.Module;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.sun.rowset.CachedRowSetImpl;
import net.java.sql.ScriptExecutor;
import net.java.util.CollectionTools;

/**
 * <p>
 * This class is a base class that contains helpful methods for
 * setting up and tearing down JPA.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public abstract class JPABaseTest extends JCatapultBaseTest {
    public static final Logger logger = Logger.getLogger(JPABaseTest.class.getName());
    public static String persistentUnit = "punit";
    public static EntityManagerFactory emf;
    public static MysqlDataSource dataSource = new MysqlDataSource();
    public static String databaseName;

    /**
     * Default constructor to setup modules with {@link org.jcatapult.guice.JPAModule}
     */
    protected JPABaseTest() {
        modules = CollectionTools.<Module>list(new JPAModule());
    }

    /**
     * This can be called in the Test classes constructor in order to set the JPA persistent unit
     * to use. This is the same as the init parameter for the filter. This defaults to <em>punit</em>
     *
     * @param   persistentUnit The persistent unit to use.
     */
    public static void setPersistentUnit(String persistentUnit) {
        JPABaseTest.persistentUnit = persistentUnit;
    }

    /**
     * Allows sub-classes to setup different databases. The default is to setup a database connection
     * to the database that has the same name as the project (dashes are replaced by underscores).
     *
     * @param   databaseName The database name.
     */
    public static void setDatabaseName(String databaseName) {
        JPABaseTest.databaseName = databaseName;
    }

    /**
     * Constructs the EntityManagerFactory.
     */
    @BeforeClass
    public static void setUpJPA() {
        dataSource = DatabaseTools.setupJDBCandJNDI(jndi, databaseName);

        // Create the JPA EMF
        emf = Persistence.createEntityManagerFactory(persistentUnit);
    }

    /**
     * Constructs the EntityManager and puts it in the context.
     */
    @Before
    @Override
    public void setUp() {
        logger.info("Setting up JPA test support.");
        EntityManager em = emf.createEntityManager();
        EntityManagerContext.set(em);
        super.setUp();
    }

    /**
     * Closes the EntityManager and removes it from the context.
     */
    @After
    public void tearDownEntityManager() {
        EntityManager em = EntityManagerContext.get();
        EntityManagerContext.remove();
        em.close();
    }

    /**
     * Closes the EntityManagerFactory.
     */
    @AfterClass
    public static void tearDownJPA() {
        EntityManagerContext.remove();
        emf.close();
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
        ScriptExecutor executor = new ScriptExecutor(getConnection());
        executor.execute(new File(script));
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
        Connection c = dataSource.getConnection();
        Statement s = c.createStatement();
        s.executeUpdate(sql);
        s.close();
        c.close();
    }

    /**
     * Clears all the data from the given table using JDBC.
     *
     * @param   table The Table to clear.
     * @throws SQLException If the clear failed.
     */
    protected void clearTable(String table) throws SQLException {
        executeSQL("delete from " + table);
    }

    /**
     * Runs the given query and returns the results in a detached RowSet.
     *
     * @param   query The query to run.
     * @return  The results of the query.
     * @throws  SQLException If the query failed.
     */
    protected RowSet executeQuery(String query) throws SQLException {
        Connection c = dataSource.getConnection();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery(query);
        CachedRowSet rowSet = new CachedRowSetImpl();
        rowSet.populate(rs);
        rs.close();
        s.close();
        c.close();
        return rowSet;
    }

    /**
     * @return  A connection.
     * @throws java.sql.SQLException on sql exception
     */
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}