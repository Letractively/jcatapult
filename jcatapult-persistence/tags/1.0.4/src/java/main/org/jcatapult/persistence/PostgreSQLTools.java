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

import java.util.logging.Logger;

import net.java.naming.MockJNDI;
import static org.jcatapult.persistence.ProjectTools.*;
import org.postgresql.ds.PGSimpleDataSource;

/**
 * <p>
 * This is a toolkit that provides helper methods for working with a PostgreSQL
 * relational databases.
 * </p>
 *
 * @author  James Humphrey
 * @author  Brian Pontarelli
 */
public class PostgreSQLTools {
    private static final Logger logger = Logger.getLogger(PostgreSQLTools.class.getName());

    /**
     * Sets up the connection pool to PostgreSQL and puts that into the JNDI tree. This uses the
     * project.xml file and the $HOME/build.properties file to grab the project name and DB username
     * and password.
     *
     * @param jndi {@link net.java.naming.MockJNDI}
     * @param dbName the db name
     * @return The DataSource and never null.
     */
    public static PGSimpleDataSource setup(MockJNDI jndi, String dbName) {
        String projectName = loadProjectName();

        // if the dbName is empty then assume <projectName>_test
        if (dbName == null || dbName.isEmpty()) {
            dbName = projectName.replace('-', '_').replace('.', '_') + "_test";
        }

        PGSimpleDataSource pds = new PGSimpleDataSource();
        pds.setDatabaseName(dbName);
        pds.setServerName("localhost");
        pds.setUser("dev");
        pds.setPassword("dev");

        String jndiName = "java:comp/env/jdbc/" + projectName;
        jndi.bind(jndiName, pds);

        logger.info("JNDI name is [" + jndiName + "]");

        return pds;
    }
}