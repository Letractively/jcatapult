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
package org.jcatapult.persistence;

import javax.sql.DataSource;
import java.util.logging.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import net.java.naming.MockJNDI;
import static org.jcatapult.persistence.ProjectTools.*;

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
public class MySQLTools {
    private static final Logger logger = Logger.getLogger(MySQLTools.class.getName());

    /**
     * Sets up the connection pool to MySQL and puts that into the JNDI tree. This uses the
     * project.xml file and the $HOME/build.properties file to grab the project name and DB username
     * and password.
     *
     * @param jndi {@link MockJNDI}
     * @param dbName the db name
     * @return The DataSource and never null.
     */
    public static DataSource setup(MockJNDI jndi, String dbName) {
        String projectName = loadProjectName();

        // if the dbName is empty then assume <projectName>_test
        if (dbName == null || dbName.isEmpty()) {
            dbName = projectName.replace('-', '_').replace('.', '_') + "_test";
        }

        String url = "jdbc:mysql://localhost:3306/" + dbName + "?user=dev&password=dev";
        return setup(jndi, url, projectName);
    }

    /**
     * Sets up the JDBC connection pool to MySQL and puts that into the JNDI tree.
     *
     * @param   jndi The {@link MockJNDI} instance.
     * @param   url The database url.
     * @param   projectName The name of the project that is used to build the JNDI tree.
     * @return  The DataSource and never null.
     */
    public static DataSource setup(MockJNDI jndi, String url, String projectName) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setAutoReconnect(true);

        String jndiName = "java:comp/env/jdbc/" + projectName;
        jndi.bind(jndiName, dataSource);

        logger.info("DB Url [" + url + "]");
        logger.info("JNDI name is [" + jndiName + "]");
        return dataSource;
    }
}
