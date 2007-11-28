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
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.jcatapult.migration.PatcherSeeder;
import org.jcatapult.migration.database.DatabaseProviderFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import net.java.naming.MockJNDI;
import net.java.sql.HSQLTools;
import net.java.sql.ScriptExecutor;
import net.java.util.Version;

/**
 * <p>
 * This class tests the database patcher.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class PatcherSeederTest {
    @Test
    public void testPatcher() throws SQLException, IOException {

        String dbURL = "jdbc:mysql://localhost:3306/patcher_seeder_" +
            "" +
            "test?user=dev&password=dev";
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

        ScriptExecutor executor = new ScriptExecutor(c);
        executor.execute(new File("test/component1/META-INF/sql/create/tables.sql"));
        executor.execute(new File("test/component2/META-INF/sql/create/tables.sql"));
        executor.execute(new File("test/component3/META-INF/sql/create/tables.sql"));
        executor.execute(new File("test/component4/META-INF/sql/create/tables.sql"));
        executor.execute(new File("test/db/create/tables.sql"));

        Map<String, Version> versions = new HashMap<String, Version>();
        PatcherSeeder patcherSeeder = new PatcherSeeder(c, "projectName", new File("test/db/patch"),
            new File("test/db/seed"), true, versions);
        patcherSeeder.patchSeed();

        Assert.assertEquals(new Version("1.1"), versions.get("foo"));
        Assert.assertEquals(new Version("1.1"), versions.get("bar"));
        Assert.assertEquals(new Version("1.1"), versions.get("projectName"));

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
        verifyData(s, "select name from projectName", "projectName", "projectName2");
        verifyData(s, "select name from foo", "foo", "foo2");
        verifyData(s, "select name from bar", "bar", "bar2");
        s.close();
        c.close();
    }

    private void verifyTable(Statement s, String tableName) throws SQLException {
        s.execute("describe " + tableName);
    }

    private void verifyData(Statement s, String sql, String... values) throws SQLException {
        ResultSet rs = s.executeQuery(sql);
        for (String value : values) {
            Assert.assertTrue(rs.next());
            Assert.assertEquals(value, rs.getString(1));
        }
        rs.close();
    }
}