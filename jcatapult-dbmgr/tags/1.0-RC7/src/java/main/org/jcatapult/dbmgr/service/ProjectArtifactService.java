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
package org.jcatapult.dbmgr.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.jcatapult.dbmgr.domain.Artifact;
import org.jcatapult.dbmgr.domain.ProjectContext;
import org.jcatapult.dbmgr.domain.SQLScript;
import org.jcatapult.dbmgr.domain.SQLScriptType;
import static org.jcatapult.dbmgr.domain.SQLScriptType.*;
import org.jcatapult.dbmgr.utils.ScriptUtils;

/**
 * Service to interact with JCatapult Project {@link org.jcatapult.dbmgr.domain.Artifact} objects
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
public class ProjectArtifactService extends BaseArtifactService {
    private static final Logger logger = Logger.getLogger(ProjectArtifactService.class.getName());

    private ProjectContext projectContext;

    public ProjectArtifactService(ProjectContext projectContext) {
        this.projectContext = projectContext;
    }

    /**
     * {@inheritDoc}
     */
    public Artifact getArtifact() {

        Artifact a = new Artifact();
        a.setCurrentVersion(projectContext.getCurrentVersion());
        a.setDatabaseVersion(projectContext.getDatabaseVersion());
        a.setName(projectContext.getProjectName());
        a.setBaseScripts(getScripts(projectContext.getBaseDir(), BASE));
        a.setAlterScripts(getScripts(projectContext.getAlterDir(), ALTER));
        a.setSeedScripts(getScripts(projectContext.getSeedDir(), SEED));

        return a;
    }

    private SortedSet<SQLScript> getScripts(File dirPath, SQLScriptType type) {

        SortedSet<SQLScript> sqlScripts = new TreeSet<SQLScript>();

        // if the supplied dir doesn't exist, then return an empty set
        if (!dirPath.exists()) {
            return sqlScripts;
        }

        // get all sql files in the dir
        File[] sqlFiles = dirPath.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }
        });

        // iterate through all the sql files
        for (File sqlFile : sqlFiles) {
            SQLScript sqlScript = new SQLScript();

            // set the input stream.  if this throws an exception,
            // treat this as a critical error and throw a runtime exception.
            try {
                sqlScript.setInputStream(sqlFile.toURI().toURL().openStream());
            } catch (IOException e) {
                logger.severe("Unable to open input stream for project sql file [" + sqlFile.getAbsolutePath() + "]");
                throw new RuntimeException(e);
            }

            sqlScript.setFilename(sqlFile.getName());
            sqlScript.setType(type);
            sqlScript.setVersion(ScriptUtils.extractVersionFromFileName(sqlScript.getFilename()));

            // add to set
            sqlScripts.add(sqlScript);

        }

        if (sqlScripts.isEmpty()) {
            logger.finest("Found no scripts of type [" + type + "] for artifact [" + projectContext.getProjectName() + "] - " +
                sqlScripts);
        } else {
            logger.finest("Found the following [" + type + "] scripts in artifact [" + projectContext.getProjectName() + "] - " +
                sqlScripts);
        }

        return sqlScripts;
    }
}
