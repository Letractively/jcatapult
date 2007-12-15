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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jcatapult.migration.domain.ComponentContext;
import org.jcatapult.migration.domain.ComponentJar;
import org.jcatapult.migration.domain.ProjectContext;
import org.jcatapult.migration.domain.Artifact;
import org.jcatapult.migration.service.ArtifactScriptVersionSortStrategyImpl;
import org.jcatapult.migration.service.ArtifactService;
import org.jcatapult.migration.service.ComponentArtifactService;
import org.jcatapult.migration.service.ProjectArtifactService;
import org.jcatapult.migration.service.ComponentJarService;
import org.jcatapult.migration.service.ArtifactScriptVersionSortStrategy;

import net.java.util.Version;

/**
 * Mediator for generating a project database from local project and component jar resources
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
public class DatabaseGenerator {
    private static final Logger logger = Logger.getLogger(DatabaseGenerator.class.getName());

    private Connection connection;
    private List<ComponentJar> componentJars;
    private Map<String, Version> databaseVersions;
    private ProjectContext pCtx;
    private ComponentJarService cjs;
    private boolean seedOnly = false;

    /**
     *
     *
     * @param connection The JDBC connection to use when connecting to the database.
     *          doesn't. This is used to determine if hibernate should be used to create the tables.
     *          If the project doesn't have any domain objects than we must assume that all the domain
     *          objects come from the classpath. If this is true, than we can check to see if there
     *          are create scripts in the path.
     * @param componentJars a list of all component jars
     * @param databaseVersions the versions of all componenets current stored in the database
     * @param pCtx {@link ProjectContext} object that contains information related to the project
     * @param cjs {@link org.jcatapult.migration.service.ComponentJarService}
     */
    public DatabaseGenerator(Connection connection, List<ComponentJar> componentJars,
        Map<String, Version> databaseVersions, ProjectContext pCtx, ComponentJarService cjs) {
        this.connection = connection;
        this.componentJars = componentJars;
        this.databaseVersions = databaseVersions;
        this.pCtx = pCtx;
        this.cjs = cjs;
    }

    public void generate() throws IOException {

        boolean created = false;

        // If the local project contains a table sql file in the directory specified by 'baseDir'
        // then we can't use Hibernate and must be sql scripts to create the database
        File tablesSqlFile = new File(pCtx.getBaseDir(), "tables.sql");
        if (tablesSqlFile.exists()) {
            createComponentTables();
            createProjectTables();
        } else {
            // if the project doesn't contain a domain, then we can't use Hibernate to create component tables
            if (!pCtx.containsDomain()) {
                createComponentTables();
                created = true;
            }

            // if we still haven't created a database, then we know that we must use Hibernate
            if (!created) {
                logger.info("Using Hibernate to create database schema");
                Map<String, String> params = new HashMap<String, String>();
                params.put("hibernate.hbm2ddl.auto", "create");
                EntityManagerFactory emf = Persistence.createEntityManagerFactory(pCtx.getPersistenceUnit(), params);
                EntityManager em = emf.createEntityManager();
                em.close();
                emf.close();

                seedOnly = true;

                createComponentTables();
                createProjectTables();
            }
        }
    }

    /**
     * Creates components tables
     *
     * @throws IOException if there's a problem during table generation
     */
    private void createComponentTables() throws IOException {
        for (ComponentJar componentJar : componentJars) {
            logger.info("Creating database schema for component [" + componentJar.getComponentName() + "]");
            ComponentContext cCtx = new ComponentContext(componentJar,
                databaseVersions.get(componentJar.getComponentName()));
            generateTable(new ComponentArtifactService(cCtx, cjs));
        }
    }

    /**
     * Creates project tables
     *
     * @throws IOException if there's a problem during table generation
     */
    private void createProjectTables() throws IOException {
        logger.info("Creating database schema for project [" + pCtx.getProjectName() + "]");
        generateTable(new ProjectArtifactService(pCtx));

    }

    /**
     * Helper method to generate tables
     *
     * @param as {@link org.jcatapult.migration.service.ArtifactService}
     * @throws IOException if there's a problem during table generation
     */
    private void generateTable(ArtifactService as) throws IOException {
        Artifact a = as.getArtifact();
        ArtifactScriptVersionSortStrategy sortStrat = new ArtifactScriptVersionSortStrategyImpl();
        TableGenerator tg = new TableGenerator(sortStrat, a, connection);
        tg.generate(seedOnly);
        databaseVersions.put(a.getName(), a.getCurrentVersion());
    }
}
