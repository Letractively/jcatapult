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
package org.jcatapult.dbmgr.module;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import net.java.util.Version;

/**
 * Contains utility methods for operating on module jar files.
 */
public class ModuleJarTools {
    /**
     * Extracts the module version from the jar filename.
     *
     * <pre>
     * moduleName-moduleVersion.ext
     * </pre>
     *
     * @param   filename The name of the module jar
     * @return  A Version object
     */
    public static Version getVersionFromJarFilename(String filename) {
        String strippedExt = filename.split(".jar")[0];
        String[] tokens = filename.split("[0-9]+\\Q.\\E[0-9]+(\\Q.\\E[0-9]+(-[\\w]*)?)?");
        String jarLabel = tokens[0].substring(0, tokens[0].length() - 1);
        int versionStartIndex = jarLabel.length() + 1;
        String versionStr = filename.substring(versionStartIndex, strippedExt.length());
        return new Version(versionStr);
    }

    /**
     * Parses the module.xml file for the 'name' attribute
     *
     * @param   inputStream The input stream to the module.xml file
     * @return  The module name
     */
    public static String parseModuleName(InputStream inputStream) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = builder.parse(inputStream);
            return dom.getDocumentElement().getAttribute("name");
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse module.xml file", e);
        }
    }
}
