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
package org.jcatapult.dbmgr;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.java.util.Version;

/**
 * User: jhumphrey
 * Date: Dec 15, 2007
 */
public class DatabaseManagerTest extends BaseTest {

    @Test
    public void testManagerProject1() throws SQLException, IOException {
        String pUnit = "punit";
        Connection c = getMySQL5Connection("database_manager_test");
        String projectName = "project1";
        boolean containsDomain = true;
        File baseDir = new File("test/project1/db/base");
        File alterDir = new File("test/project1/db/alter");
        File seedDir = new File("test/project1/db/seed");
        File projectXmlFile = new File("test/project1/project.xml");
        Version projectVersion = new Version("1.0");
        DatabaseManager dm = new DatabaseManager(pUnit, c, projectName, containsDomain, baseDir, alterDir, seedDir,
            projectXmlFile, projectVersion);

        dm.manage();
    }
}
