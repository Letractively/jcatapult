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
package org.jcatapult.dbmgr.component;

import java.io.InputStream;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import net.java.util.Version;

/**
 * Contains utility methods for operating on component jar files.
 *
 * User: jhumphrey
 * Date: Dec 10, 2007
 */
public class ComponentJarTools {

    private static final Logger logger = Logger.getLogger(ComponentJarTools.class.getName());

    /**
     * Extracts the component version from the jar filename.
     *
     * componentName-componentVersion.ext
     *
     * @param filename the name of the component jar
     * @return a Version object
     */
    public static Version getVersionFromJarFilename(String filename) {
        String strippedExt = filename.split(".jar")[0];

        logger.finest("Filename with extension stripped [" + strippedExt + "]");

        String[] tokens = filename.split("[0-9]+\\Q.\\E[0-9]+(\\Q.\\E[0-9]+(-[\\w]*)?)?");

        String jarLabel = tokens[0].substring(0, tokens[0].length() - 1);

        logger.finest("Jar Label [" + jarLabel + "]");

        int versionStartIndex = jarLabel.length() + 1;

        String versionStr = filename.substring(versionStartIndex, strippedExt.length());

        logger.finest("Version string [" + versionStr + "]");

        Version v = new Version(versionStr);

        logger.fine("Converted jar filename [" + filename + "] to Version [" + v.toString() + "]");

        return v;
    }

    /**
     * Parses the component.xml file for the 'name' attribute
     *
     * @param inputStream the input stream to the component.xml file
     * @return component name
     */
    public static String parseComponentName(InputStream inputStream) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = builder.parse(inputStream);
            return dom.getDocumentElement().getAttribute("name");
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse component.xml file", e);
        }
    }
}
