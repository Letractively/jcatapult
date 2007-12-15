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

import java.util.SortedSet;

import net.java.util.Version;

/**
 * Domain bean object to model a jacatapult artifact
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
public class Artifact {
    private String name;
    private Version currentVersion;
    private Version databaseVersion;
    private Version nextVersion;
    private SortedSet<SQLScript> baseScripts;
    private SortedSet<SQLScript> alterScripts;
    private SortedSet<SQLScript> seedScripts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Version getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Version currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Version getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(Version databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public Version getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(Version nextVersion) {
        this.nextVersion = nextVersion;
    }

    public SortedSet<SQLScript> getBaseScripts() {
        return baseScripts;
    }

    public void setBaseScripts(SortedSet<SQLScript> baseScripts) {
        this.baseScripts = baseScripts;
    }

    public SortedSet<SQLScript> getAlterScripts() {
        return alterScripts;
    }

    public void setAlterScripts(SortedSet<SQLScript> alterScripts) {
        this.alterScripts = alterScripts;
    }

    public SortedSet<SQLScript> getSeedScripts() {
        return seedScripts;
    }

    public void setSeedScripts(SortedSet<SQLScript> seedScripts) {
        this.seedScripts = seedScripts;
    }
}
