/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.jcatapult.deployer.utils;

import net.java.util.Version;

/**
 * @author jhumphrey
 */
public class ArchiveUtils {

    /**
     * Extracts and returns the version from the archive.
     *
     * <pre>
     * &lt;archive> ::= &lt;project-name> "-" &lt;version> ".tar.gz"
     * &lt;project-name> ::= the name of the project taken from the root 'name' attribute in the project.xml
     * &lt;version> ::= &lt;number> "." &lt;number> ("." &lt;number>) ("-" &lt;snapshot>)
     * &lt;number> ::= [0-9]+
     * &lt;snapshot> ::= [A|B|M|RC][0-9]+
     * </pre>
     *
     * @param   archive The archive
     * @return A Version object
     */
    public static String getVersionFromArchive(String archive) {
        String strippedExt = archive.split(".tar.gz")[0];
        String[] tokens = archive.split("[0-9]+\\Q.\\E[0-9]+(\\Q.\\E[0-9]+(-[\\w]*)?)?");
        String archiveLabel = tokens[0].substring(0, tokens[0].length() - 1);
        int versionStartIndex = archiveLabel.length() + 1;
        return archive.substring(versionStartIndex, strippedExt.length());
    }
}
