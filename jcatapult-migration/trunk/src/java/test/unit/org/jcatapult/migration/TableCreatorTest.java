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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.jcatapult.migration.TableCreator;
import org.jcatapult.migration.database.DatabaseProviderFactory;

import net.java.sql.HSQLTools;

/**
 * <p>
 * This class tests the database loader.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class TableCreatorTest {
    @Test
    public void testLoader() throws SQLException, IOException {


        String dbURL = "jdbc:mysql://localhost:3306/table_creator_test" +
            "?user=dev&password=dev";
        String dbType = "mysql5";

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
        s.executeUpdate("drop table if exists component3_patch2_0");
        s.executeUpdate("drop table if exists component3_patch2_1");
        s.executeUpdate("drop table if exists component4");
        s.executeUpdate("drop table if exists currentVersions");
        s.close();

        TableCreator loader = new TableCreator(null, c, true, new File("test/db/create"));
        loader.create();

        s = c.createStatement();
        verifyTable(s, "projectName");
        verifyTable(s, "foo");
        verifyTable(s, "bar");
        verifyTable(s, "component3");
        verifyTable(s, "component4");
        verifyCount(s, "select count(name) from projectName", 0);
        verifyCount(s, "select count(name) from foo", 0);
        verifyCount(s, "select count(name) from bar", 0);
        verifyCount(s, "select count(name) from component3", 0);
        verifyCount(s, "select count(name) from component4", 0);

        s.close();
        c.close();
    }

    private void verifyTable(Statement s, String tableName) throws SQLException {
        s.execute("describe " + tableName);
    }

    private void verifyCount(Statement s, String sql, int count) throws SQLException {
        ResultSet rs = s.executeQuery(sql);
        assertTrue(rs.next());
        assertEquals(count, rs.getInt(1));
        rs.close();
    }
}