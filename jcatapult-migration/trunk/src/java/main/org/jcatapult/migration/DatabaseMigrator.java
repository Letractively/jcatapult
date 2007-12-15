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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jcatapult.migration.database.DatabaseProvider;
import org.jcatapult.migration.database.DatabaseProviderFactory;
import org.jcatapult.migration.domain.ComponentJar;
import org.jcatapult.migration.service.ComponentJarServiceImpl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.java.naming.MockJNDI;
import net.java.util.Version;

/**
 * <p>
 * This class is the main entry point from the command-line or via Java code to
 * use the database migration functionality of JCatapult. This class performs many
 * steps to migrate database schema, patches and seed data from one environment
 * to another.
 * </p>
 *
 * <h2>Usage</h2>
 * <h3>Development</h3>
 * <p>
 * There are two main ways that this class is used. The first is during development
 * when the domain for an application is changing rapidly. During this phase of
 * development, the database migrator uses Hibernate to construct the database
 * using the JPA configuration file (persistence.xml). This doesn't use any of
 * the SQL files from any of the components but does use seed files from both
 * the project and from the components.
 * </p>
 *
 * <h3>Production</h3>
 * <p>
 * Once an application has completed the initial development process it moves
 * into the staging and production process. The main difference is that once
 * the application moves into this stage it starts to be versioned in a much
 * more controlled manner. The reason for this is that it is important to ensure
 * that the database can always be reconstructed to specific released version
 * of the application. For this reason, once the application moves from the
 * initial development stage into the production stage, Hibernate is no longer
 * used. In order to tell the JCatapult Database migrator to stop using Hibernate
 * the application needs to generate a table SQL file that constructs only the
 * tables for the current application and not for any components.
 * </p>
 *
 * <h3>Changing stages</h3>
 * <p>
 * Just to recap, in order to tell the migrator to change from the development
 * stage to the production stage, you need to construct a table SQL file and
 * tell the migrator where it is located.
 * </p>
 *
 * <h2>Directories</h2>
 * <p>
 * The migrator uses a specific directory structure for both the current
 * application as well components. Based on this structure the migrator
 * can determine which files to execute.
 * </p>
 *
 * <h3>Application</h3>
 * <p>
 * For the current application, there needs to be an SQL directory that has
 * this format. This directory must be passed to the migrator in order for
 * the migrator to correctly find the SQL files for the application. The format
 * of the directories is:
 * </p>
 *
 * <pre>
 * dir
 *  |-create
 *  |-patch
 *  |-seed
 * </pre>
 *
 * <p>
 * The create directory should contain a single file named tables.sql. This
 * file contains the commands that construct the tables for this application.
 * The patch directory and the seed directory should contain any number of
 * files that contain patch and seed commands. These files must have a specific
 * naming convention in order for the migrator to correctly determine which to
 * run. The format is:
 * </p>
 *
 * <pre>
 * &lt;version>-&lt;name>.sql
 * </pre>
 *
 * <p>
 * An example of a correctly named file might be:
 * </p>
 *
 * <pre>
 * 1.0-patch-users.sql
 * </pre>
 *
 * <p>
 * This version number must be of the form:
 * </p>
 *
 * <pre>
 * major.minor.patch
 * </pre>
 *
 * <p>
 * The migrator will use the version number in the name to determine which
 * files will be executed. This is discussed later. The name is just for
 * readability and isn't used by the migrator.
 * </p>
 *
 * <h3>Components</h3>
 * <p>
 * The migrator is highly componentized such that a component can define
 * any number of entities that it provides to an application as well as
 * manage the creation, patching and seeding of the database tables. This
 * allows a highly reusable component model.
 * </p>
 *
 * <p>
 * Components that wish to participate in the migration process must
 * supplying SQL files in a specific location within the component JAR
 * files. This location is:
 * </p>
 *
 * <pre>
 * META-INF/sql
 * </pre>
 *
 * <p>
 * Inside this directory within the component JAR file, the component
 * should contain the same three directories as described above in the
 * application section. Likewise, the same files should exist in these
 * directories and they are used in the same manner as those from the
 * application are used.
 * </p>
 *
 * <h3>Component names</h3>
 * <p>
 * Any component that wants to participate in the database migration
 * must define a component.xml file in the <b>META-INF</b> directory
 * inside the component JAR file. This XML file must contain a root
 * element named component with a name attribute like this:
 * </p>
 *
 * <pre>
 * &lt;component name="foo">
 * </pre>
 *
 * <p>
 * This name will be used to determine the current version of the
 * component in the database and is used to figure out which SQL files
 * from the component to execute.
 * </p>
 *
 * <h2>Workflow</h2>
 * <p>
 * In order to understand how the migrator works the workflow is outlined
 * here. These are the steps that the migrator uses to determine what
 * stage the application is in, what files to execute and how to migrate
 * the database and data from one environment to another.
 * </p>
 *
 * <ol>
 * <li>Check to see if the <b>currentVersions</b> table exists in the datbase.
 * If it does, set production mode, skip the table creation process and skip to
 * the patching and seeing process steps.
 * </li>
 * <li>Load up the versions from the <b>currentVersions</b> table if it exists.</li>
 * <li>Table creation process
 *  <ol>
 *  <li>If the project doesn't contain any entities (this is specified on the
 *  command-line as a parameter to the migrator), look in the classpath for
 *  tables.sql files in components. If there aren't any, assume development mode.
 *  </li>
 *  <li>If the project contains any entities, look in SQL directory of the project
 *  and look for a tables.sql file. If there isn't one, assume development mode.
 *  </li>
 *  <li>If development mode, run Hibernate to create the tables and move onto
 *  the seeding step.
 *  </li>
 *  <li>If production mode, execute the tables.sql from the application's SQL
 *  directory first and then execute the tables.sql from the components in the
 *  classpath.
 *  </li>
 *  </ol>
 * </li>
 * <li>Pathcing process
 *  <ol>
 *  <li>Based on the version for the application and each component in the
 *  <b>currentVersions</b> table, execute all the patch scripts for the
 *  application and each component whose version number is greater than the
 *  version number in the table. The name of the application is passed to the
 *  migrator on the command-line and that is used to find the current version
 *  of the application and the name of the components is specified in the
 *  component.xml files in the META-INF directory.
 *  </li>
 *  <li>Update the versions in the <b>currentVersions</b> table based on the
 *  SQL scripts that were just executed.
 *  </li>
 *  </ol>
 * </li>
 * <li>Seeding process
 *  <ol>
 *  <li>Based on the version for the application and each component in the
 *  <b>currentVersions</b> table, execute all the seed scripts for the
 *  application and each component whose version number is greater than the
 *  version number in the table. The name of the application is passed to the
 *  migrator on the command-line and that is used to find the current version
 *  of the application and the name of the components is specified in the
 *  component.xml files in the META-INF directory.
 *  </li>
 *  <li>Update the versions in the <b>currentVersions</b> table based on the
 *  SQL scripts that were just executed.
 *  </li>
 *  </ol>
 * </li>
 * </ol>
 *
 * <h2>Command-line</h2>
 * <p>
 * Here is the usage from the command line
 * </p>
 *
 * <pre>
 * Usage: DatabaseMigrator [--no-domain] &lt;persistence-unit> &lt;db-url> &lt;application-name> &lt;sql-dir>");
 *
 *      --no-domain: Tells the DatabaseMigrator that the current application doesn't");
 *                   contain any domain objects and that it should only check for component");
 *                   tables.sql files.");
 *
 * persistence-unit: The name of the persistence unit to use when in development mode.");
 *
 *           db-url: The full JDBC URL to the database.");
 *
 * application-name: The name of the current application, which is used to find the current");
 *                   version of the application in the currentVersions table.");
 *
 *          sql-dir: The directory that contains the SQL files for this project. This can");
 *                   be a bogus directory if the project doesn't contain any SQL files.");
 * </pre>
 *
 * @author Brian Pontarelli
 */
public class DatabaseMigrator {
    private static final Logger logger = Logger.getLogger("DatabaseMigrator");

    public static final String VERSION_TABLE = "JCATAPULT_ARTIFACT_VERSIONS";

    private final String persistenceUnit;
    private final Connection connection;
    private final String applicationName;
    private final boolean containsDomain;
    private final File createDir;
    private final File patchDir;
    private final File seedDir;
    private final File projectXml;
    private String dependenciesId;

    private final Map<String, Version> currentVersions = new HashMap<String, Version>();

    private Injector injector = Guice.createInjector();

    public DatabaseMigrator(String persistenceUnit, Connection connection, String applicationName, boolean containsDomain,
        File createDir, File patchDir, File seedDir, File projectXml, String dependenciesId) {
        this.persistenceUnit = persistenceUnit;
        this.connection = connection;
        this.applicationName = applicationName;
        this.containsDomain = containsDomain;
        this.createDir = createDir;
        this.patchDir = patchDir;
        this.seedDir = seedDir;
        this.projectXml = projectXml;
        this.dependenciesId = dependenciesId;
    }

    /**
     * This main method provides an external execution point to the DatabaseMigrator
     * Typically this is executed via a script; (ant, shell, etc)
     *
     * @param   args Arguments described in class comment.
     * @throws SQLException on sql exception
     * @throws IOException on io exception
     */
    public static void main(String... args) throws SQLException, IOException {

        if (args.length < 8 || args.length > 9) {
            StringBuffer errMsg = new StringBuffer();
            errMsg.append("Invalid arguments: ").append(Arrays.asList(args)).append("\n");
            errMsg.append("Usage: DatabaseMigrator [--no-domain] <persistence-unit> <db-url> <application-name> <sql-dir> <db-type> <jndi-name> <project-xml-path>");
            errMsg.append("\n\n");
            errMsg.append("--no-domain: Tells the DatabaseMigrator that the current application doesn't");
            errMsg.append("contain any domain objects and that it should only check for component");
            errMsg.append(" tables.sql files.\n\n");
            errMsg.append("persistence-unit: The name of the persistence unit to use when in development mode.\n\n");
            errMsg.append("db-url: The full JDBC URL to the database.").append("\n\n");
            errMsg.append("application-name: The name of the current application, which is used to find the current");
            errMsg.append(" version of the application in the currentVersions table.\n\n");
            errMsg.append("sql-dir: The directory that contains the SQL files to create the database from. This can");
            errMsg.append(" be a bogus directory if the project doesn't contain any SQL files.\n\n");
            errMsg.append("db-type: The database type to connect to.\n\n");
            errMsg.append("jndi-name: The jndi name name that maps to the datasource.\n\n");
            errMsg.append("project-xml-path: the path to the project.xml file.\n\n");
            errMsg.append("dependencies-id: the dependencies id defined within the project.xml.");
            System.err.println(errMsg);
            System.exit(1);
        }

        int count = 0;
        boolean containsDomain = true;
        if (args[count].equals("--no-domain")) {
            containsDomain = false;
            count++;
        }

        String persistenceUnit = args[count++];
        String dbURL = args[count++];
        String projectName = args[count++];
        String sqlDir = args[count++];
        String dbType = args[count++];
        String jndiName = args[count++];
        String projectXmlPath = args[count++];
        String dependenciesId = args[count];

        // create the database provider
        DatabaseProvider dp = DatabaseProviderFactory.getProvider(dbType, dbURL);

        // create a mock jndi objet to bind the datasource so that hibernate can work properly
        MockJNDI jndi = new MockJNDI();
        jndi.bind(jndiName, dp.getDatasource());
        jndi.activate();

        logger.info("DB Url [" + dbURL + "]");
        logger.info("JNDI name is [" + jndiName + "]");

        DatabaseMigrator dm = new DatabaseMigrator(persistenceUnit, dp.getConnection(), projectName,
            containsDomain, new File(sqlDir, "create"), new File(sqlDir, "patch"),
            new File(sqlDir, "seed"), new File(projectXmlPath), dependenciesId);
        dm.migrate();
    }

    public void migrate() throws SQLException, IOException {

        // resolve component
        ComponentJarServiceImpl cr = injector.getInstance(ComponentJarServiceImpl.class);
        List<ComponentJar> componentJars = cr.resolveJars(projectXml, dependenciesId);

        // Load up the component and project versions from the database.  If the table hasn't yet been created,
        // then assume we're NOT in production mode.  In that event, create all the tables
        boolean productionMode = loadVersions();
//        if (!productionMode) {
//            TableCreator creator = new TableCreator(persistenceUnit, connection, containsDomain, createDir,
//                componentJars);
//            productionMode = creator.create();
//        }

//        PatcherSeeder patcherSeeder = new PatcherSeeder(connection, applicationName, patchDir, seedDir,
//            productionMode, currentVersions, componentJars);
//        patcherSeeder.patchSeed();

        // Update with the stuff we did
        updateVersions();
    }

    /**
     * Construct the Versions mapping.
     *
     * @return True if the versions table exists, false it it was created.
     * @throws SQLException If the load or create failed.
     */
    private boolean loadVersions() throws SQLException {
        Statement statement = connection.createStatement();
        try {
            // execute a random sql script to see if the currentVersions table already exists.
            // if it doesn't, this will throw an exception.
            statement.execute("select * from " + VERSION_TABLE);

            logger.info("Loading the jcatapult artifact version table [" + VERSION_TABLE + "] into memory");
            ResultSet rs = statement.executeQuery("select * from " + VERSION_TABLE);
            while (rs.next()) {
                String name = rs.getString("name");
                String version = rs.getString("version");
                currentVersions.put(name, new Version(version));
                logger.info("Found version [" + version + "] for jcatapult artifact [" + name + "]");
            }

            return true;
        } catch (SQLException e) {
            logger.info("The [" + VERSION_TABLE + "] table doesn't exist, creating it");
            statement.execute("create table " + VERSION_TABLE + " (name varchar(1000), version varchar(255))");
            statement.close();
            return false;
        }
    }

    /**
     * Updates the version table in the database with the last version from the SQL files that were
     * executed.
     *
     * @throws SQLException If the update failed.
     */
    private void updateVersions() throws SQLException {
        // Update the jcatapult artifact version in the database using the
        // highest that we found on this entire patch/seed run
        Set<String> componentNames = currentVersions.keySet();
        for (String componentName : componentNames) {
            PreparedStatement statement = connection.prepareStatement("update " + VERSION_TABLE + " set version = ? where name = ?");
            Version version = currentVersions.get(componentName);
            statement.setString(1, componentName);
            statement.setString(2, version.toString());
            int results = statement.executeUpdate();
            if (results == 0) {
                logger.info("Set version for [" + componentName + "] to [" + version + "]");
                statement = connection.prepareStatement("insert into " + VERSION_TABLE + " values (?,?)");
                statement.setString(1, componentName);
                statement.setString(2, version.toString());
                results = statement.executeUpdate();
                if (results == 0) {
                    throw new RuntimeException("Unable to insert or update to table [" + VERSION_TABLE +
                        "] for column name [" + componentName + "] and column version [" + version + "]");
                }
            } else {
                logger.info("Updated version for [" + componentName + "] to [" + version + "]");
            }
        }
    }
}