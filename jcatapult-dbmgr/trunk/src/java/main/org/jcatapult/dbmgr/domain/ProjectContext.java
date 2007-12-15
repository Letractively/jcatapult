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
package org.jcatapult.dbmgr.domain;

import java.io.File;

import net.java.util.Version;

/**
 * Models project context.
 *
 * Every JCatapult project that contains a domain will have its version stored in the
 * {@link org.jcatapult.dbmgr.DatabaseManager#VERSION_TABLE} table.  If the project version is not yet
 * stored in the database, then the database version will be null
 *
 * User: jhumphrey
 * Date: Dec 13, 2007
 */
public class ProjectContext {
    private Version currentVersion;
    private Version databaseVersion;
    private String projectName;
    private String persistenceUnit = "punit";
    private boolean containsDomain = true;
    private File baseDir = new File("src/db/main/base");
    private File alterDir = new File("src/db/main/alter");;
    private File seedDir = new File("src/db/main/seed");;

    public ProjectContext(String projectName, Version currentVersion, Version databaseVersion) {
        this.projectName = projectName;
        this.currentVersion = currentVersion;
        this.databaseVersion = databaseVersion;
    }

    public Version getCurrentVersion() {
        return currentVersion;
    }

    public Version getDatabaseVersion() {
        return databaseVersion;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPersistenceUnit() {
        return persistenceUnit;
    }

    public void setPersistenceUnit(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    public boolean containsDomain() {
        return containsDomain;
    }

    public void setContainsDomain(boolean containsDomain) {
        this.containsDomain = containsDomain;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public File getAlterDir() {
        return alterDir;
    }

    public void setAlterDir(File alterDir) {
        this.alterDir = alterDir;
    }

    public File getSeedDir() {
        return seedDir;
    }

    public void setSeedDir(File seedDir) {
        this.seedDir = seedDir;
    }
}
