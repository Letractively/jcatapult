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
import java.util.Queue;
import java.util.logging.Logger;

import org.jcatapult.migration.domain.Artifact;
import org.jcatapult.migration.domain.SQLScript;
import org.jcatapult.migration.service.ArtifactScriptVersionSortStrategy;

import net.java.sql.ScriptExecutor;

/**
 * Generates tables for a particular {@link org.jcatapult.migration.domain.Artifact}
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
public class TableGenerator {

    private static final Logger logger = Logger.getLogger(TableGenerator.class.getName());

    private ArtifactScriptVersionSortStrategy versionSortStrat;
    private Artifact artifact;
    private Connection connection;

    /**
     * Constructor
     *
     * @param artifactScriptVersionSortStrategy {@link org.jcatapult.migration.service.ArtifactScriptVersionSortStrategy} strategy to be used to
     * sort artifact scripts
     * @param artifact {@link org.jcatapult.migration.domain.Artifact} to generate tables for
     * @param connection connection to the database to execute the scripts in
     */
    public TableGenerator(ArtifactScriptVersionSortStrategy artifactScriptVersionSortStrategy, Artifact artifact,
        Connection connection) {
        this.versionSortStrat = artifactScriptVersionSortStrategy;
        this.artifact = artifact;
        this.connection = connection;
    }

    /**
     * Generates the tables for a particular artifact
     *
     * @param seedOnly true if executing seed scripts only, false otherwise
     * @throws IOException thrown if there is a problem during sql script execution
     */
    public void generate(boolean seedOnly) throws IOException {
        logger.info("Generating tables for artifact [" + artifact.getName() + "]");

        Queue<SQLScript> sqlScripts = versionSortStrat.sort(artifact, seedOnly);

        logger.info("Preparing the following script queue for execution for artifact [" + artifact.getName() + "] version [" +
            artifact.getCurrentVersion() + "]: " + sqlScripts);

        for (SQLScript sqlScript : sqlScripts) {
            executeScript(sqlScript);
        }

    }

    /**
     * Delegator to execute a sql script
     *
     * @param sqlScript the sql script
     * @throws IOException thrown if there's a problem during script execution
     */
    private void executeScript(SQLScript sqlScript) throws IOException {
        ScriptExecutor executor = new ScriptExecutor(connection);

        logger.info("Executing script [" + sqlScript.getFilename() + "]");

        executor.execute(sqlScript.getInputStream(), sqlScript.getFilename());
    }
}
