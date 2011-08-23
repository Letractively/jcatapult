/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

import org.w3c.dom.Document;

/**
 * <p> This class grabs the project name from the project.xml file. </p>
 *
 * @author Brian Pontarelli
 */
public class ProjectTools {

  /**
   * Loads the project name from the project.xml file.
   *
   * @return The project name.
   */
  public static String loadProjectName() {
    // Open project.xml and grab the project name for the JDBC connections
    File f = new File("project.xml");
    Document dom;
    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      dom = builder.parse(f);
    } catch (Exception e) {
      throw new AssertionError("Unable to locate the project.xml for the project. Make sure " +
        "you are running ant tests from inside the project and that your project has this " +
        "file.");
    }

    return dom.getDocumentElement().getAttribute("name").trim();
  }
}
