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
import java.sql.Connection;
import java.util.jar.JarFile;

import org.jcatapult.dbmgr.database.DatabaseProviderFactory;
import org.jcatapult.dbmgr.domain.Artifact;
import org.jcatapult.dbmgr.domain.ComponentContext;
import org.jcatapult.dbmgr.domain.ComponentJar;
import org.jcatapult.dbmgr.domain.ProjectContext;
import org.jcatapult.dbmgr.service.ArtifactService;
import org.jcatapult.dbmgr.service.ComponentArtifactService;
import org.jcatapult.dbmgr.service.ProjectArtifactService;
import org.jcatapult.dbmgr.service.ComponentJarService;
import org.jcatapult.dbmgr.component.ComponentJarTools;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.java.util.Version;

/**
 * Base test for all unit test classes.  Provides some basic reusable utility methods as well
 * as initialization of Guice for DI usage within unit test classes
 *
 * User: jhumphrey
 * Date: Nov 30, 2007
 */
@Ignore
public abstract class BaseTest {

    protected Injector injector;

    @Before
    public void setupGuice() {
        injector = Guice.createInjector();
        injector.injectMembers(this);
    }

    /**
     * Helper method to return an artifact for component1
     *
     * @param componentName component name
     * @param componentVersion component version
     * @param dbVersion the database version
     * @param cjs {@link org.jcatapult.dbmgr.service.ComponentJarService}
     * @return artifact
     * @throws java.io.IOException on exception
     */
    protected Artifact getComponentArtifact(String componentName, String componentVersion, Version dbVersion, ComponentJarService cjs)
        throws IOException {
        File file = new File("target/jars/" + componentName + "-" + componentVersion + ".jar");
        ComponentJar cj = new ComponentJar(componentName, file, ComponentJarTools.getVersionFromJarFilename(file.getName()), new JarFile(file));
        ComponentContext cCtx = new ComponentContext(cj, dbVersion);

        ArtifactService as = new ComponentArtifactService(cCtx, cjs);

        return as.getArtifact();
    }

    /**
     * Helper method to return an artifact for project1
     *
     * @param projectName project name
     * @param dbVersion the database version
     * @return artifact
     */
    protected Artifact getProjectArtifact(String projectName, Version dbVersion) {
        ProjectContext pCtx = new ProjectContext(projectName, new Version("1.1"), dbVersion);
        pCtx.setAlterDir(new File("test/" + projectName + "/db/alter"));
        pCtx.setBaseDir(new File("test/" + projectName + "/db/base"));
        pCtx.setSeedDir(new File("test/" + projectName + "/db/seed"));

        ArtifactService as = new ProjectArtifactService(pCtx);

        return as.getArtifact();
    }

    /**
     * Helper method to get a database connection
     *
     * @param dbName the database name
     * @return Connection
     */
    protected Connection getMySQL5Connection(String dbName) {
        String dbURL = "jdbc:mysql://localhost:3306/" + dbName + "?user=dev&password=dev";
        String dbType = "mysql5";

        return DatabaseProviderFactory.getProvider(dbType, dbURL).getConnection();
    }
}
