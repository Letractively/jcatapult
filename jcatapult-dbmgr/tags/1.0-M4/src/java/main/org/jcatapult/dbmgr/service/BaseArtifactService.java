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

import java.util.logging.Logger;

import net.java.util.Version;

/**
 * Base interface for all {@link org.jcatapult.dbmgr.service.ArtifactService} classes
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
public abstract class BaseArtifactService implements ArtifactService {

    private static final Logger logger = Logger.getLogger(BaseArtifactService.class.getName());

    /**
     * Extracts the version number from the sql seed and patch scripts
     *
     * @param filename the sql filename
     * @return Version object
     */
    protected Version extractVersion(String filename) {
        String versionStr = filename.split("-", 2)[0];

        // since tables.sql is versionless, assign Version.ZERO as it's version.  This will insure
        // that it's always at the front of the sorted set of sql scripts (if applicable)
        if (versionStr.equals("tables.sql")) {
            Version v = Version.ZERO;
            logger.finest("The tables.sql script is versionless and, therefore, has been assigned version [" + v + "]");
            return v;
        }
        logger.finest("Extracted version string [" + versionStr + "] from sql script filename [" + filename + "]");
        return new Version(versionStr);
    }
}
