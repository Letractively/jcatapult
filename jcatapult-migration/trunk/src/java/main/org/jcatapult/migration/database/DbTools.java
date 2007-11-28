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

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Logger;

import net.java.sql.ScriptExecutor;

/**
 * User: jhumphrey
 * Date: Nov 26, 2007
 */
public class DbTools {

    private static final Logger logger = Logger.getLogger(DbTools.class.getName());

        /**
     * Runs the <b>disable-keys.sql</b> file from the jcatault-migration JAR to turn of MySQL key constraints so
     * that the database doesn't blow up when running SQL files.
     *
     * @param   connection The connection to use.
     * @throws java.io.IOException If the file couldn't be read.
     */
    public static void disableKeys(Connection connection) throws IOException {
        logger.info("Disabling keys on database");
        ScriptExecutor executor = new ScriptExecutor(connection);

        executor.execute(DbTools.class.getClassLoader().getResource("disable-keys.sql"));
    }

    /**
     * Runs the <b>enable-keys.sql</b> file from the jcatapult-migration JAR to turn back on the MySQL key
     * constraints which were turned off during loading so that the database didn't blow up when
     * running SQL files.
     *
     * @param   connection The connection to use.
     * @throws  IOException If the file couldn't be read.
     */
    public static void enableKeys(Connection connection) throws IOException {
        logger.info("Enabling keys on database");
        ScriptExecutor executor = new ScriptExecutor(connection);
        executor.execute(DbTools.class.getClassLoader().getResource("enable-keys.sql"));
    }
}
