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

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.logging.Logger;

import org.jcatapult.dbmgr.domain.Artifact;
import org.jcatapult.dbmgr.domain.ModuleContext;
import org.jcatapult.dbmgr.domain.ModuleJar;
import org.jcatapult.dbmgr.domain.SQLScript;
import org.jcatapult.dbmgr.domain.SQLScriptType;
import static org.jcatapult.dbmgr.domain.SQLScriptType.*;

/**
 * Service to interact with JCatapult Module {@link org.jcatapult.dbmgr.domain.Artifact} objects
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
public class ModuleArtifactService extends BaseArtifactService {

    private static final Logger logger = Logger.getLogger(ModuleArtifactService.class.getName());

    private ModuleContext moduleContext;
    private ModuleJarService moduleJarService;

    public ModuleArtifactService(ModuleContext moduleContext, ModuleJarService moduleJarService) {
        this.moduleContext = moduleContext;
        this.moduleJarService = moduleJarService;
    }

    public Artifact getArtifact() {

        // create an artifact
        Artifact a = new Artifact();
        a.setName(moduleContext.getModuleJar().getModuleName());
        a.setCurrentVersion(moduleContext.getModuleJar().getVersion());
        a.setDatabaseVersion(moduleContext.getDatabaseVersion());
        a.setBaseScripts(getScripts(ModuleJar.DIR_BASE, BASE));
        a.setAlterScripts(getScripts(ModuleJar.DIR_ALTER, ALTER));
        a.setSeedScripts(getScripts(ModuleJar.DIR_SEED, SEED));

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

        // iterate through all the module jar entries to create sql scripts
        List<JarEntry> jarEntries = moduleJarService.getJarDirectorySQLEntries(moduleContext.getModuleJar(), dirPath);
        for (JarEntry jarEntry : jarEntries) {
            SQLScript sqlScript = new SQLScript();

            // set the input stream.  if this throws an exception,
            // treat this as a critical error and throw a runtime exception.
            try {
                sqlScript.setInputStream(moduleContext.getModuleJar().getJarFile().getInputStream(jarEntry));
            } catch (IOException e) {
                logger.severe("Unable to open input stream for module jar [" +
                    moduleContext.getModuleJar().getFile().getAbsolutePath() + "] jar entry [" + jarEntry + "]");
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
                moduleContext.getModuleJar().getModuleName() + "]");
        } else {
            logger.finest("Found the following [" + type + "] scripts in artifact [" +
                moduleContext.getModuleJar().getModuleName() + "] - " + sqlScripts);
        }

        return sqlScripts;
    }
}
