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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.jcatapult.dbmgr.domain.ComponentJar;
import static org.jcatapult.dbmgr.component.ComponentJarTools.getVersionFromJarFilename;
import static org.jcatapult.dbmgr.component.ComponentJarTools.parseComponentName;

import com.google.inject.Inject;
import net.java.lang.StringTools;
import net.java.savant.SavantRuntime;
import net.java.savant.context.SavantContext;
import net.java.savant.context.SavantContextBuilder;
import net.java.savant.dep.Artifact;

/**
 * <p>This class resolves dependency jars to componentJars to create a FIFO list of jar files.</p>
 *
 * <p>This process is accomplished via a depth-first traversal of the project's Dependency artifact graph.
 * Once a leaf graph-node is found, the jar is queried for the presence of the following file:</p>
 *
 * <p>META-INF/sql/create/tables.sql</p>
 *
 * <p>If this file is found, then it's assumed that the jar file is a component and has a
 * database schema association.</p>
 *
 * <p>Explanation of 'Why use Depth-first Recursion?'
 * This class uses a depth-first recursion method to solve the issue of determining which
 * componentJars depend on another.  The problem can be best described using an example below:</p>
 *
 * <p>Lets say, for instance, that you have a component called 'A' and a component called 'B'
 * and B's domain model depends on A's.  For example, component A has a table X with a column X.1
 * and component B has a table Y with column Y.1 where Y.1 is a foreign key to X.1.</p>
 *
 * <p>Now, imagine you were trying to recreate this database: How would you know which tables to ceate first?
 * It would require you to have some sort of visibility into the foreign key relationships between tables.
 * For instance, say I try to recreate the database exampled above.  If I try to create component B's Y table
 * before I create component A's X table, the database won't let you because Y has a foreign key to X.  So,
 * how does one solve this problem?</p>
 *
 * <p>The depth-first savant method used in this class uses the following algorithm to solve the problem:</p>
 * <ul>
 *  <li>1. Obtain a graph of all dependency artifacts where each top level artifact node is a direct
 *      dependency defined in the project's project.xml.</li>
 *  <li>2. Traverse through each top level artifact depth-first until a leaf node is found.</li>
 *  <li>3. If node qualifies as a component (described above), then add to queue and continue depth-first traversal</li>
 *
 * User: jhumphrey
 * Date: Nov 30, 2007
 */
public class ComponentJarServiceImpl implements ComponentJarService {

    private static final Logger logger = Logger.getLogger(ComponentJarServiceImpl.class.getName());

    private SavantContextBuilder scb = null;

    @Inject
    public ComponentJarServiceImpl(SavantContextBuilder scb) {
        this.scb = scb;
    }

    /**
     * Resolves jars using savant
     *
     * @param projectXml the project.xml
     * @param dependenciesId the dependencies id
     * @return linked list of jar files
     */
    public LinkedList<ComponentJar> resolveJars(File projectXml, String dependenciesId) {

        // init savant runtime and context
        SavantRuntime sr = new SavantRuntime(projectXml.getParentFile());
        SavantContext sc = scb.build(sr, projectXml.getAbsolutePath());

        // resolve deps
        boolean depsResolved = sc.resolveDependencies(null, null, null, true);

        // throw a runtime exception if deps can't get resolved
        if (!depsResolved) {
            throw new RuntimeException("Unable to resolve dependencies.  Set 'net.java.savant.level=FINEST' in log file for detailed logging");
        }

        // get all the Savant Artifact objects oredered depth-first
        Queue<Artifact> arts = sc.getDepthFirstOrderedDependencies(dependenciesId);

        // holds the resolved jar files
        LinkedList<ComponentJar> componentJars = new LinkedList<ComponentJar>();

        // iterate on all the savant artifacts and create ComponentJar objects
        for (Artifact art : arts) {
            File artFile = sc.getArtifactFile(art);

            JarFile jf = null;
            try {
                jf = new JarFile(artFile); // might have to use this: new File(URLDecoder.decode(artFile, "UTF-8"))
            } catch (IOException e) {
                logger.warning("Artifact file [" + artFile.getAbsolutePath() + "] is not a jar file");
            }

            JarEntry tablesSqlEntry = jf.getJarEntry(ComponentJar.PATH_TABLES_SQL);
            if (tablesSqlEntry != null) {

                // check for component.xml file
                JarEntry componentXmlEntry = jf.getJarEntry(ComponentJar.PATH_COMPONENT_XML);
                if (componentXmlEntry == null) {
                    throw new RuntimeException("jar file [" + artFile +
                        "] does not contain a " + ComponentJar.PATH_COMPONENT_XML + " file");
                }

                // if component.xml exists, make sure it contains the 'name' attribute.  This is needed because
                // it's used in the currentVersions table to store component version history
                String componentName;
                try {
                    componentName = parseComponentName(jf.getInputStream(componentXmlEntry));
                } catch (IOException e) {
                    throw new RuntimeException("Unable to get input stream for [" + componentXmlEntry + "]", e);
                }
                if (StringTools.isEmpty(componentName)) {
                    throw new IllegalArgumentException("Found component.xml file in component [" + artFile.getName() + "] but it doesn't have a name attribute on the root element.");
                }

                logger.finest("Adding component jar [" + artFile.getAbsolutePath() + "]");
                ComponentJar componentJar;
                try {
                    componentJar = new ComponentJar(componentName, artFile,
                        getVersionFromJarFilename(artFile.getName()), new JarFile(artFile));
                } catch (IOException e) {
                    throw new RuntimeException("Artifact file [" + artFile.getAbsolutePath() + "] is not a jar file", e);
                }
                componentJars.add(componentJar);
            } else {
                logger.finest("Skipping jar file [" + artFile.getAbsolutePath() + "].  Not a component or doesn't contain the jar entry [" + ComponentJar.PATH_TABLES_SQL + "]");
            }
            try {
                jf.close();
            } catch (IOException e) {
                logger.warning("Unable to close connection to jarfile [" + artFile.getAbsolutePath() + "]");
            }
        }

        if (componentJars.isEmpty()) {
            logger.info("No components found that contain domains");
        } else {
            logger.info("Resolved components that contain domains (depth-first order): " + componentJars);
        }

        return componentJars;
    }

    /**
     * {@inheritDoc}
     */
    public List<JarEntry> getJarDirectorySQLEntries(ComponentJar componentJar, String dirPath) {

        // add a forward slash if it doesn't already have one
        if (!dirPath.endsWith("/")) {
            dirPath += "/";
        }

        JarFile jarFile = componentJar.getJarFile();

        List<JarEntry> entries = new ArrayList<JarEntry>();
        // make sure that the dirPath is a directory and exists
        if (jarFile.getEntry(dirPath) != null && jarFile.getEntry(dirPath).isDirectory()) {
            Enumeration<JarEntry> entryEnumeration = jarFile.entries();
            while (entryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = entryEnumeration.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.contains(dirPath) && !entryName.endsWith("/") && entryName.endsWith(".sql")) {
                    logger.finest("Found jar sql entry [" + entryName + "] within component [" +
                        componentJar.getComponentName() + "]");
                    entries.add(jarEntry);
                }
            }
        } else {
            logger.info("Jar file [" + jarFile.getName() + "] does not contain any sql files in directory [" +
                dirPath + "]");
        }

        return entries;
    }
}
