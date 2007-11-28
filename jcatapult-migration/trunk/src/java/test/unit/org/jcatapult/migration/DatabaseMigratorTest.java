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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jcatapult.migration.database.DatabaseProviderFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * <p>
 * This class tests the database loader.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DatabaseMigratorTest {

    @Test
    public void testMysql5Provider() throws SQLException, IOException {

        String dbURL = "jdbc:mysql://localhost:3306/database_migrator_test?user=dev&password=dev";
        String dbType = "mysql5";

        runSQL(dbURL, dbType);
    }

    private void runSQL(String dbURL, String dbType) throws SQLException, IOException {
        Connection c = DatabaseProviderFactory.getProvider(dbType, dbURL).getConnection();

        Statement s = c.createStatement();
        s.executeUpdate("drop table if exists projectName");
        s.executeUpdate("drop table if exists projectName_patch1_0");
        s.executeUpdate("drop table if exists projectName_patch1_1");
        s.executeUpdate("drop table if exists foo");
        s.executeUpdate("drop table if exists bar");
        s.executeUpdate("drop table if exists bar_patch1_0");
        s.executeUpdate("drop table if exists bar_patch1_1");
        s.executeUpdate("drop table if exists foo_patch1_0");
        s.executeUpdate("drop table if exists foo_patch1_1");
        s.executeUpdate("drop table if exists component3");
        s.executeUpdate("drop table if exists component3_patch_2_0");
        s.executeUpdate("drop table if exists component3_patch_2_1");
        s.executeUpdate("drop table if exists component4");
        s.executeUpdate("drop table if exists currentVersions");
        s.close();

        DatabaseMigrator.main("punit", dbURL, "projectName", "test/db", dbType,
            "java:comp/env/jdbc/database_migrator_test");

        s = c.createStatement();
        verifyTable(s, "projectName");
        verifyTable(s, "projectName_patch1_0");
        verifyTable(s, "projectName_patch1_1");
        verifyTable(s, "foo");
        verifyTable(s, "foo_patch1_0");
        verifyTable(s, "foo_patch1_1");
        verifyTable(s, "bar");
        verifyTable(s, "bar_patch1_0");
        verifyTable(s, "bar_patch1_1");
        verifyTable(s, "component3");
        verifyTable(s, "component3_patch2_0");
        verifyTable(s, "component3_patch2_1");
        verifyTable(s, "component4");
        verifyData(s, "select name from projectName", "projectName", "projectName2");
        verifyData(s, "select name from foo", "foo", "foo2");
        verifyData(s, "select name from bar", "bar", "bar2");
        verifyData(s, "select name from component4", "bar", "bar2");

        ResultSet rs = s.executeQuery("select * from currentVersions where name = 'projectName'");
        assertTrue(rs.next());
        assertEquals("projectName", rs.getString("name"));
        assertEquals("1.1.0", rs.getString("version"));
        rs.close();

        rs = s.executeQuery("select * from currentVersions where name = 'foo'");
        assertTrue(rs.next());
        assertEquals("foo", rs.getString("name"));
        assertEquals("1.1.0", rs.getString("version"));
        rs.close();

        rs = s.executeQuery("select * from currentVersions where name = 'bar'");
        assertTrue(rs.next());
        assertEquals("bar", rs.getString("name"));
        assertEquals("1.1.0", rs.getString("version"));
        rs.close();

        rs = s.executeQuery("select * from currentVersions where name = 'component3'");
        assertTrue(rs.next());
        assertEquals("component3", rs.getString("name"));
        assertEquals("2.1.0", rs.getString("version"));
        rs.close();

        rs = s.executeQuery("select * from currentVersions where name = 'component4'");
        assertTrue(rs.next());
        assertEquals("component4", rs.getString("name"));
        assertEquals("1.0.0", rs.getString("version"));
        rs.close();

        s.close();
        c.close();
    }

    private void verifyTable(Statement s, String tableName) throws SQLException {
        s.execute("select * from " + tableName);
    }

    private void verifyData(Statement s, String sql, String... values) throws SQLException {
        ResultSet rs = s.executeQuery(sql);
        for (String value : values) {
            assertTrue("SQL failed [" + sql + "]", rs.next());
            assertEquals(value, rs.getString(1));
        }
        rs.close();
    }
}