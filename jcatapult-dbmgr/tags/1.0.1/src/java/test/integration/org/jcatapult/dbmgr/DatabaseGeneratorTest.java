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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcatapult.dbmgr.domain.ModuleJar;
import org.jcatapult.dbmgr.domain.ProjectContext;
import org.jcatapult.dbmgr.service.ModuleJarService;
import org.junit.Test;
import org.junit.Assert;

import com.google.inject.Inject;
import net.java.util.Version;

/**
 * User: jhumphrey
 * Date: Dec 13, 2007
 */
public class DatabaseGeneratorTest extends BaseTest {

    ModuleJarService moduleJarService;

    @Inject
    public void setModuleResolve(ModuleJarService cjs) {
        this.moduleJarService = cjs;
    }

    /**
     * Simple test of a project that has no module dependencies
     *
     * @throws java.io.IOException on exception
     */
    @Test
    public void testGenerateProject5() throws IOException {

        String projectName = "project5";

        Map<String, Version> databaseVersions = new HashMap<String, Version>();
        DatabaseGenerator gen = getDbGen(projectName, databaseVersions, new Version("1.0"), null);
        gen.generate();

        Assert.assertEquals(1, databaseVersions.size());
        Assert.assertEquals(new Version("1.0"), databaseVersions.get(projectName));
    }

    /**
     * Simple test of a project that has one module dependency
     *
     * @throws java.io.IOException on exception
     */
    @Test
    public void testGenerateProject4() throws IOException {

        String projectName = "project4";

        Map<String, Version> databaseVersions = new HashMap<String, Version>();
        DatabaseGenerator gen = getDbGen(projectName, databaseVersions, new Version("1.0"), null);
        gen.generate();

        Assert.assertEquals(2, databaseVersions.size());
        Assert.assertEquals(new Version("1.0"), databaseVersions.get(projectName));
        Assert.assertEquals(new Version("1.2"), databaseVersions.get("module1"));
    }

    /**
     * Complex project with many modules and all depend on each other with complex foreign key relationships
     *
     * @throws java.io.IOException on exception
     */
    @Test
    public void testGenerateProject1() throws IOException {
        String projectName = "project1";

        Map<String, Version> databaseVersions = new HashMap<String, Version>();
        DatabaseGenerator gen = getDbGen(projectName, databaseVersions, new Version("1.0"), null);
        gen.generate();

        Assert.assertEquals(5, databaseVersions.size());
        Assert.assertEquals(new Version("1.0"), databaseVersions.get(projectName));
        Assert.assertEquals(new Version("1.2"), databaseVersions.get("module1"));
        Assert.assertEquals(new Version("1.1"), databaseVersions.get("module2"));
        Assert.assertEquals(new Version("2.1"), databaseVersions.get("module3"));
        Assert.assertEquals(new Version("1.0"), databaseVersions.get("module4"));
    }

    /**
     * Helper method to instantiate a database generator
     *
     * @param projectName project name
     * @param databaseVersions map of database versions
     * @param projectCurrentVersion the project current version
     * @param projectDatabaseVersion the project database version
     * @return database generator
     */
    DatabaseGenerator getDbGen(String projectName, Map<String, Version> databaseVersions,
            Version projectCurrentVersion, Version projectDatabaseVersion) {

        List<ModuleJar> moduleJars = moduleJarService.resolveJars(new File("test/" + projectName + "/project.xml"));

        ProjectContext pCtx = new ProjectContext(projectName, projectCurrentVersion, projectDatabaseVersion);
        pCtx.setAlterDir(new File("test/" + projectName + "/db/alter"));
        pCtx.setBaseDir(new File("test/" + projectName + "/db/base"));
        pCtx.setSeedDir(new File("test/" + projectName + "/db/seed"));

        String dbName = "database_generator_test_" + projectName;

        return new DatabaseGenerator(getMySQL5Connection(dbName), moduleJars, databaseVersions, pCtx, moduleJarService);
    }
}
