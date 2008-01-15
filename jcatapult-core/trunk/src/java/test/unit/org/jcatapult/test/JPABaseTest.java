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
import java.util.Map;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.EntityTransaction;
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
import static net.java.util.CollectionTools.map;
import net.java.xml.Unmarshaller;
import net.java.xml.JavaBeanObjectCreator;
import net.java.text.SimplePluralizer;

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
     * Constructs the EntityManager and puts it in the context.
     */
    public void setUpEntityManager() {
        EntityManager em = emf.createEntityManager();
        EntityManagerContext.set(em);
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
     * Clears all the data from the table that the given Class is mapped to using JPA.
     *
     * @param   klass The JPA class to get the table name from. This first checks if the Class has
     *          a JPA Table annotation and uses that. Otherwise, it uses the Class's simple name.
     * @throws SQLException If the clear failed.
     */
    protected void clearTable(Class<?> klass) throws SQLException {
        Table table = klass.getAnnotation(Table.class);
        String tableName;
        if (table == null || table.name().equals("")) {
            tableName = klass.getSimpleName();
        } else {
            tableName = table.name();
        }

        clearTable(tableName);
    }

    /**
     * Runs the given query and returns the results in a detached RowSet.
     *
     * @param   query The query to run.
     * @return The results of the query.
     * @throws SQLException If the query failed.
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
     * @return A connection.
     * @throws java.sql.SQLException on sql exception
     */
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
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
    protected <T> void loadFixture(String fixture, Class<T> type)
        throws RuntimeException {
        logger.fine("Loading fixtures from [" + fixture + "] for [" + type + "]");

        // Clear the table via the annotation
        Table table = type.getAnnotation(Table.class);
        String tableName;
        if (table == null) {
            Entity entity = type.getAnnotation(Entity.class);
            if (entity == null) {
                throw new IllegalArgumentException("The type [" + type + "] is not annotated with @Entity");
            }

            tableName = type.getSimpleName();
        } else {
            tableName = table.name();
        }

        try {
            clearTable(tableName);

            String pkg = getClass().getPackage().getName();
            String location = "src/java/test/unit/" + pkg.replace(".", "/") + "/" + fixture;
            logger.fine("Mapped location to [" + location + "]");

            Map<String, String> mappings = map(tableName, "child");
            Unmarshaller um = new Unmarshaller(null);
            um.addObjectCreator("fixture", JavaBeanObjectCreator.forClass(Fixture.class,
                new SimplePluralizer(), mappings));
            um.addObjectCreator("fixture/*", JavaBeanObjectCreator.forClass(type));
            Fixture<T> fix = (Fixture<T>) um.unmarshal(location);

            EntityManager em = emf.createEntityManager();
            EntityTransaction et = em.getTransaction();
            et.begin();

            List<T> objs = fix.getChildren();
            for (T obj : objs) {
                logger.fine("Running fixure for object [" + obj + "]");
                em.persist(obj);
            }

            et.commit();
            em.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}