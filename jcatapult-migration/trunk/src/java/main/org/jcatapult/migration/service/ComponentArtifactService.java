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
package org.jcatapult.migration.service;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.logging.Logger;

import org.jcatapult.migration.component.ComponentJarTools;
import org.jcatapult.migration.domain.Artifact;
import org.jcatapult.migration.domain.ComponentContext;
import org.jcatapult.migration.domain.ComponentJar;
import org.jcatapult.migration.domain.SQLScript;
import org.jcatapult.migration.domain.SQLScriptType;
import static org.jcatapult.migration.domain.SQLScriptType.*;

/**
 * Service to interact with JCatapult Component {@link org.jcatapult.migration.domain.Artifact} objects
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
public class ComponentArtifactService extends BaseArtifactService {

    private static final Logger logger = Logger.getLogger(ComponentArtifactService.class.getName());

    private ComponentContext cCtx;
    private ComponentJarService cjs;

    public ComponentArtifactService(ComponentContext cCtx, ComponentJarService cjs) {
        this.cCtx = cCtx;
        this.cjs = cjs;
    }

    public Artifact getArtifact() {

        // create an artifact
        Artifact a = new Artifact();
        a.setName(cCtx.getComponentJar().getComponentName());
        a.setCurrentVersion(cCtx.getComponentJar().getVersion());
        a.setDatabaseVersion(cCtx.getDatabaseVersion());
        a.setBaseScripts(getScripts(ComponentJar.DIR_BASE, BASE));
        a.setAlterScripts(getScripts(ComponentJar.DIR_ALTER, ALTER));
        a.setSeedScripts(getScripts(ComponentJar.DIR_SEED, SEED));

        return a;
    }

    /**
     * Helper method that iterates over jar entries to create {@link SQLScript} objects
     *
     * @param dirPath the directory path to get the sql scripts from
     * @param type the {@link SQLScriptType} of the sql script
     * @return set of {@link SQLScript} objects
     */
    private SortedSet<SQLScript> getScripts(String dirPath, SQLScriptType type) {
        SortedSet<SQLScript> sqlScripts = new TreeSet<SQLScript>();

        // iterate through all the component jar entries to create sql scripts
        List<JarEntry> jarEntries = cjs.getJarDirectorySQLEntries(cCtx.getComponentJar(), dirPath);
        for (JarEntry jarEntry : jarEntries) {
            SQLScript sqlScript = new SQLScript();

            // set the input stream.  if this throws an exception,
            // treat this as a critical error and throw a runtime exception.
            try {
                sqlScript.setInputStream(cCtx.getComponentJar().getJarFile().getInputStream(jarEntry));
            } catch (IOException e) {
                logger.severe("Unable to open input stream for component jar [" +
                    cCtx.getComponentJar().getFile().getAbsolutePath() + "] jar entry [" + jarEntry + "]");
                throw new RuntimeException(e);
            }

            // set the filename
            int index = jarEntry.getName().lastIndexOf("/");
            String filename = jarEntry.getName().substring(index + 1);
            sqlScript.setFilename(filename);
            sqlScript.setType(type);
            sqlScript.setVersion(extractVersion(sqlScript.getFilename()));

            // add script to set
            sqlScripts.add(sqlScript);
        }

        if (sqlScripts.isEmpty()) {
            logger.finest("Found no scripts of type [" + type + "] for artifact [" +
                cCtx.getComponentJar().getComponentName() + "]");
        } else {
            logger.finest("Found the following [" + type + "] scripts in artifact [" +
                cCtx.getComponentJar().getComponentName() + "] - " + sqlScripts);
        }

        return sqlScripts;
    }
}
