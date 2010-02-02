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
package org.jcatapult.dbmgr.dsl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.jcatapult.dbmgr.database.Dialect;
import org.jcatapult.dbmgr.database.MySQL5InnoDBDialect;
import org.junit.Ignore;
import org.junit.Test;

import net.java.sql.ScriptExecutor;

/**
 * <p>
 * This test the groovy DSL executor.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class GroovyDSLExecutorTest {
    @Test
    public void testMySQL() throws IllegalAccessException, IOException, InstantiationException, SQLException {
        Dialect dialect = new MySQL5InnoDBDialect();
        GroovyDSLExecuteor executeor = new GroovyDSLExecuteor(dialect);
        String sql = executeor.execute(new File("src/groovy/test/unit/CreateSimpleTable.db"));
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_manager_test", "dev", "dev");
        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();
    }

    @Test
    public void testMySQLForeignKeys() throws IllegalAccessException, IOException, InstantiationException, SQLException {
        Dialect dialect = new MySQL5InnoDBDialect();
        GroovyDSLExecuteor executor = new GroovyDSLExecuteor(dialect);
        String sql = executor.execute(new File("src/groovy/test/unit/CreateReferenceTables.db"));
        System.out.println("SQL is " + sql);
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_manager_test", "dev", "dev");
        ScriptExecutor se = new ScriptExecutor(connection);
        se.execute(new ByteArrayInputStream(sql.getBytes()), "sql");
    }

    @Test
    public void testMySQLInsert() throws IllegalAccessException, IOException, InstantiationException, SQLException {
        Dialect dialect = new MySQL5InnoDBDialect();
        GroovyDSLExecuteor executor = new GroovyDSLExecuteor(dialect);
        String sql = executor.execute(new File("src/groovy/test/unit/InsertSimple.db"));
        System.out.println("SQL is " + sql);
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_manager_test", "dev", "dev");
        ScriptExecutor se = new ScriptExecutor(connection);
        se.execute(new ByteArrayInputStream(sql.getBytes()), "sql");
    }
}
