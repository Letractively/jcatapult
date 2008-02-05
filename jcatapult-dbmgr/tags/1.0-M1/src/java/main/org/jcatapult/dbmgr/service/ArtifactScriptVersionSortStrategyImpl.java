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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.logging.Logger;

import org.jcatapult.dbmgr.domain.Artifact;
import org.jcatapult.dbmgr.domain.SQLScript;

import net.java.util.Version;

/**
 * Concrete implementation that provides a strategy for sorting {@link org.jcatapult.dbmgr.domain.SQLScript}
 * object by their version
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
public class ArtifactScriptVersionSortStrategyImpl implements ArtifactScriptVersionSortStrategy {

    private static final Logger logger = Logger.getLogger(ArtifactScriptVersionSortStrategyImpl.class.getName());

    /**
     * {@inheritDoc}
     */
    public Queue<SQLScript> sort(Artifact artifact, boolean seedOnly) {

        Queue<SQLScript> scripts = new LinkedList<SQLScript>();

        Version databaseVersion = artifact.getDatabaseVersion();

        // only add the base scripts if the database version is null
        if (databaseVersion == null && !seedOnly) {
            // currently, there's only ever 1 script in the base scripts set.  so, just get the last
            SQLScript baseScript = artifact.getBaseScripts().last();
            scripts.add(baseScript);
        }

        SortedSet<SQLScript> alterScripts = artifact.getAlterScripts();
        SortedSet<SQLScript> seedScripts = artifact.getSeedScripts();

        // setup alter iterator and script
        Iterator<SQLScript> alterIter = null;
        SQLScript alterScript = null;
        if (alterScripts != null) {
            alterIter = alterScripts.iterator();
            alterScript = alterIter.hasNext() ? alterIter.next() : null;
        }

        // setup seed iterator and script
        Iterator<SQLScript> seedIter = null;
        SQLScript seedScript = null;
        if (seedScripts != null) {
            seedIter = seedScripts.iterator();
            seedScript = seedIter.hasNext() ? seedIter.next() : null;
        }

        while (alterScript != null || seedScript != null) {
            while (seedScript != null && (alterScript == null || seedScript.compareTo(alterScript) < 0)) {
                if (databaseVersion == null || seedScript.getVersion().compareTo(databaseVersion) > 0) {
                    logger.finest("Sorted script [" + seedScript.getFilename() + "] for artifact [" +
                        artifact.getName() + "]");
                    scripts.add(seedScript);
                }
                seedScript = seedIter.hasNext() ? seedIter.next() : null;
            }

            while (alterScript != null && (seedScript == null || alterScript.compareTo(seedScript) <= 0)) {
                if (!seedOnly && (databaseVersion == null || alterScript.getVersion().compareTo(databaseVersion) > 0)) {
                    logger.finest("Sorted script [" + alterScript.getFilename() + "] for artifact [" +
                        artifact.getName() + "]");
                    scripts.add(alterScript);
                }
                alterScript = alterIter.hasNext() ? alterIter.next() : null;
            }
        }

        return scripts;
    }

    /**
     * Not currently used but kept just in case it's needed
     */
    private Version determineHighestScriptVersion(Artifact artifact) {
        // since the artifact contains a sorted set of sql scripts, and SQLScript implements comparable
        // with Version comparable delegate, we're guaranteed to always have the highest versioned
        // script as the last index in the set.  Also, because the base script set only ever contains
        // the tables.sql script, and this script is versionless, we can leave it out of the compare operation
        //
        // default each highest to Version.ZERO if the script set is empty

        Version highestAlterScript = Version.ZERO;
        if (!artifact.getAlterScripts().isEmpty()) {
            highestAlterScript = artifact.getAlterScripts().last().getVersion();
        }

        Version highestSeedScript = Version.ZERO;
        if (!artifact.getSeedScripts().isEmpty()) {
            highestSeedScript = artifact.getSeedScripts().last().getVersion();
        }

        // little tricky, but easy once explained...
        // if the highest version is an alter script, then return that version.  If it's not, then
        // we can safely assume that the seed script is either higher or equal, in both cases
        // it's safe to just return the seed script version
        return highestAlterScript.compareTo(highestSeedScript) > 0 ? highestAlterScript : highestSeedScript;
    }
}