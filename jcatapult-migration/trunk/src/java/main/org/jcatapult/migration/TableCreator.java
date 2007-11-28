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
package org.jcatapult.migration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jcatapult.migration.database.DbTools;

import net.java.sql.ScriptExecutor;

/**
 * <p>
 * This class is used to create the database and all of the tables for
 * the application. The database is created via Hibernates create mode
 * for the hibernate.hbm2ddl.auto property. After the database has been
 * created it is seeded with data by executing a series of seed SQL scripts
 * that are contained in a specific directory.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class TableCreator {
    private static final Logger logger = Logger.getLogger("TableCreator");
    private final String persistenceUnit;
    private final Connection connection;
    private final boolean containsDomain;
    private final File createDir;

    /**
     * Creates a new database loader that will use the persistence unit name for loading up the database
     * and tables via Hibernates create mode. After that this class will use the JDBC connection, seed
     * SQL directory and last version number to seed the database with data from the seed files.
     *
     * @param   persistenceUnit The JPA persistence unit to use with Hibernate's create mode.
     * @param   connection The JDBC connection to use when seeding the database.
     * @param   containsDomain True if this project contains any domain objects. False if it
     *          doesn't. This is used to determine if hibernate should be used to create the tables.
     *          If the project doesn't have any domain objects than we must assume that all the domain
     *          objects come from the classpath. If this is true, than we can check to see if there
     *          are create scripts in the path.
     * @param   createDir The directory on the file system that contains the create SQL files.
     * @throws  IllegalArgumentException If the seed SQL directory is not valid.
     */
    public TableCreator(String persistenceUnit, Connection connection, boolean containsDomain, File createDir) {
        this.persistenceUnit = persistenceUnit;
        this.connection = connection;
        this.containsDomain = containsDomain;
        this.createDir = createDir;
    }

    /**
     * Calls Hibernate to create the database tables.
     *
     * @return  True if the tables were create using SQL, false if Hibernate was used.
     * @throws  IOException If anything goes wrong while seeding.
     * @throws  SQLException If any of the SQL scripts or versions table control failed.
     */
    public boolean create() throws IOException, SQLException {
        // Disable keys so that we can load in any order.
        DbTools.disableKeys(connection);

        boolean created = false;
        try {
            // Determine if there are create scripts for this project or any included modules
            File create = new File(createDir, "tables.sql");
            if (create.exists()) {
                logger.info("Running local create script [" + create + "]");
                ScriptExecutor executor = new ScriptExecutor(connection);
                executor.execute(create);
                created = true;

                // Next load all the JAR files (components/libraries)
                createTablesClasspath();
            } else {
                // Check the class path for create scripts
                if (!containsDomain) {
                    Enumeration<URL> urls = this.getClass().getClassLoader().getResources("META-INF/sql/create/tables.sql");
                    if (urls.hasMoreElements()) {
                        createTablesClasspath();
                        created = true;
                    }
                }

                if (!created) {
                    // Use hibernate
                    logger.info("Using Hibernate to create tables");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("hibernate.hbm2ddl.auto", "create");
                    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit, params);
                    EntityManager em = emf.createEntityManager();
                    em.close();
                    emf.close();
                }
            }
        } finally {
            // Finally enable the keys again
            DbTools.enableKeys(connection);
        }

        return created;
    }

    /**
     * Executes all the scripts in the classpath.
     *
     * @throws  IOException If the entries in the classpath couldn't be loaded or executed.
     */
    protected void createTablesClasspath() throws IOException {
        ScriptExecutor executor = new ScriptExecutor(connection);
        Enumeration<URL> urls = this.getClass().getClassLoader().getResources("META-INF/sql/create/tables.sql");
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            logger.info("Running create script from classpath [" + url + "]");
            executor.execute(url);
        }
    }
}