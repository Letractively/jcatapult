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
package org.jcatapult.database;

import java.io.File;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import net.java.naming.MockJNDI;

/**
 * <p>
 * This is a toolkit that provides helper methods for working with
 * relational databases. Some of this is specific to MySQL and should
 * eventually be refactored.
 * </p>
 *
 * @author  James Humphrey
 * @author  Brian Pontarelli
 */
public class DatabaseTools {
    private static final Logger logger = Logger.getLogger(DatabaseTools.class.getName());

    /**
     * Sets up the JDBC connection pool to MySQL and puts that into the JNDI tree. This uses the
     * project.xml file and the $HOME/build.properties file to grab the project name and DB username
     * and password.
     *
     * @param jndi {@link MockJNDI}
     * @param dbName the db name
     * @return The DataSource and never null.
     */
    public static MysqlDataSource setupJDBCandJNDI(MockJNDI jndi, String dbName) {
        String projectName = loadProjectName();

        // if the dbName is empty then assume <projectName>_test
        if (dbName == null || dbName.isEmpty()) {
            dbName = projectName + "_test";
        }

        String url = "jdbc:mysql://localhost:3306/" + dbName + "?user=dev&password=dev";
        return setupJDBCURLandJNDI(jndi, url, projectName);
    }

    /**
     * Sets up the JDBC connection pool to MySQL and puts that into the JNDI tree.
     *
     * @param   jndi The {@link MockJNDI} instance.
     * @param   url The database url.
     * @param   projectName The name of the project that is used to build the JNDI tree.
     * @return  The DataSource and never null.
     */
    public static MysqlDataSource setupJDBCURLandJNDI(MockJNDI jndi, String url, String projectName) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setAutoReconnect(true);

        String jndiName = "java:comp/env/jdbc/" + projectName.replace("-", "_");
        jndi.bind(jndiName, dataSource);

        logger.info("DB Url [" + url + "]");
        logger.info("JNDI name is [" + jndiName + "]");

        return dataSource;
    }

    public static MysqlDataSource setupJDBCandJNDI(MockJNDI jndi) {
        return setupJDBCandJNDI(jndi, null);
    }

    /**
     * Loads the project name from the project.xml file.
     *
     * @return  The project name.
     */
    public static String loadProjectName() {
        // Open project.xml and grab the project name for the JDBC connections
        File f = new File("project.xml");
        Document dom;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            dom = builder.parse(f);
        } catch (Exception e) {
            throw new AssertionError("Unable to locate the project.xml for the project. Make sure " +
                    "you are running ant tests from inside the project and that your project has this " +
                    "file.");
        }
        return dom.getDocumentElement().getAttribute("name").trim().replace('-', '_');
    }
}
