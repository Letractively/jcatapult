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

import org.inversoft.savant.context.SavantContext;
import org.inversoft.savant.context.SavantContextBuilder;
import org.inversoft.savant.dep.Artifact;
import org.inversoft.savant.dep.DependencyResolveMediator;
import org.jcatapult.dbmgr.domain.ModuleJar;
import static org.jcatapult.dbmgr.module.ModuleJarTools.*;

import com.google.inject.Inject;
import net.java.lang.StringTools;

/**
 * <p>
 * This class resolves all of the module's that the project uses and builds
 * a FIFO list based on the project's dependency graph.
 * </p>
 *
 * <p>
 * This process is accomplished via a depth-first traversal of the project's
 * Dependency artifact graph. Once a leaf graph-node is found, the jar is queried
 * for the presence of the following file:
 * </p>
 *
 * <pre>
 * META-INF/sql/base/tables.sql
 * </pre>
 *
 * <p>
 * If this file is found, then it's assumed that the jar file is a module and
 * has a database schema association.
 * </p>
 *
 * <h3>Explanation of 'Why use Depth-first Recursion?'</h3>
 * <p>
 * This class uses a depth-first recursion method to solve the issue of determining
 * which module JARs depend on another. The problem can be best described using an
 * example below:
 * </p>
 *
 * <p>
 * Lets say, for instance, that you have a module called <em>user</em> and a module
 * called <em>news</em> and the <em>news</em> module's domain model depends on the
 * <em>user</em> module's domain. For example, the user module has a table <em>users</em>
 * with a column id and the <em>news</em> module has a table <em>news</em> with column
 * <em>user_id</em> where <em>user_id</em> is a foreign key to <em>users.id</em>.
 * </p>
 *
 * <p>
 * Now, imagine you were trying to recreate this database: How would you know which
 * tables to ceate first? It would require you to have some sort of visibility into
 * the foreign key relationships between tables. For instance, say I try to recreate
 * the database exampled above.  If I try to create the <em>news</em> module's <em>news</em>
 * table before I create the <em>user</em> module's <em>user</em> table, the database
 * won't let me because of the foreign key relationship. How does one solve this problem?
 * </p>
 *
 * <p>
 * The depth-first savant method used in this class uses the following algorithm to
 * solve the problem:
 * </p>
 *
 * <ol>
 *  <li>Obtain a graph of all dependency artifacts where each top level artifact node
 *      is a direct dependency defined in the project's project.xml.</li>
 *  <li>Traverse through each top level artifact depth-first until a leaf node is found.</li>
 *  <li>If node qualifies as a module (described above), then add to queue and
 *      continue depth-first traversal</li>
 * </ol>
 *
 * @author  James Humphrey
 */
public class DefaultModuleJarService implements ModuleJarService {
    private static final Logger logger = Logger.getLogger(DefaultModuleJarService.class.getName());

    private SavantContextBuilder savantContextBuilder = null;

    @Inject
    public DefaultModuleJarService(SavantContextBuilder savantContextBuilder) {
        this.savantContextBuilder = savantContextBuilder;
    }

    /**
     * Resolves jars using savant
     *
     * @param projectXml the project.xml
     * @return linked list of jar files
     */
    public LinkedList<ModuleJar> resolveJars(File projectXml) {

        // init savant runtime and context
        SavantContext sc = savantContextBuilder.build(projectXml);

        // resolve deps
        DependencyResolveMediator mediator = new DependencyResolveMediator();
        boolean depsResolved = mediator.mediate(sc, null, null, true);

        // throw a runtime exception if deps can't get resolved
        if (!depsResolved) {
            throw new RuntimeException("Unable to resolve dependencies.  Set 'org.inversoft.savant.level=FINEST' " +
                "in log file for detailed logging");
        }

        // get all the Savant Artifact objects oredered depth-first
        Queue<Artifact> arts = sc.getDepthFirstOrderedDependencies("default");

        // holds the resolved jar files
        LinkedList<ModuleJar> moduleJars = new LinkedList<ModuleJar>();

        // iterate on all the savant artifacts and create ModuleJar objects
        for (Artifact art : arts) {
            // if savant returns a null artifact file, this means that there were multiple version
            // of the same project and savant resolved to a higher version jar within the artifact graph.
            // As a result, we need to properly message this via a runtime exception
            File artFile = sc.getArtifactFile(art);

            if (artFile == null) {
                logger.warning(("Higher, compatible version of " + art.getGroup() + "/" + art.getProject() +
                    " found, not checking module status for [" + art + "]"));
                continue;
            }

            logger.finest("Checking artifact [" + art.getArtifactFile() + "] for module status");

            JarFile jf;
            try {
                jf = new JarFile(artFile); // might have to use this: new File(URLDecoder.decode(artFile, "UTF-8"))
            } catch (IOException e) {
                logger.warning("Artifact file [" + artFile.getAbsolutePath() + "] is not a jar file");
                continue;
            }

            JarEntry tablesSqlEntry = jf.getJarEntry(ModuleJar.PATH_TABLES_SQL);
            if (tablesSqlEntry != null) {

                // check for module.xml file
                JarEntry moduleXmlEntry = jf.getJarEntry(ModuleJar.PATH_MODULE_XML);
                if (moduleXmlEntry == null) {
                    throw new RuntimeException("jar file [" + artFile +
                        "] does not contain a " + ModuleJar.PATH_MODULE_XML + " file");
                }

                // if module.xml exists, make sure it contains the 'name' attribute.  This is needed because
                // it's used in the currentVersions table to store module version history
                String moduleName;
                try {
                    moduleName = parseModuleName(jf.getInputStream(moduleXmlEntry));
                } catch (IOException e) {
                    throw new RuntimeException("Unable to get input stream for [" + moduleXmlEntry + "]", e);
                }
                if (StringTools.isEmpty(moduleName)) {
                    throw new IllegalArgumentException("Found module.xml file in module [" + artFile.getName() + "] but it doesn't have a name attribute on the root element.");
                }

                logger.finest("Adding module jar [" + artFile.getAbsolutePath() + "]");
                ModuleJar moduleJar;
                try {
                    moduleJar = new ModuleJar(moduleName, artFile,
                        getVersionFromJarFilename(artFile.getName()), new JarFile(artFile));
                } catch (IOException e) {
                    throw new RuntimeException("Artifact file [" + artFile.getAbsolutePath() + "] is not a jar file", e);
                }
                moduleJars.add(moduleJar);
            } else {
                logger.finest("Skipping jar file [" + artFile.getAbsolutePath() + "].  Not a module or doesn't contain the jar entry [" + ModuleJar.PATH_TABLES_SQL + "]");
            }
            try {
                jf.close();
            } catch (IOException e) {
                logger.warning("Unable to close connection to jarfile [" + artFile.getAbsolutePath() + "]");
            }
        }

        if (moduleJars.isEmpty()) {
            logger.info("No modules found that contain domains");
        } else {
            logger.info("Resolved modules that contain domains (depth-first order): " + moduleJars);
        }

        return moduleJars;
    }

    /**
     * {@inheritDoc}
     */
    public List<JarEntry> getJarDirectorySQLEntries(ModuleJar moduleJar, String dirPath) {

        // add a forward slash if it doesn't already have one
        if (!dirPath.endsWith("/")) {
            dirPath += "/";
        }

        JarFile jarFile = moduleJar.getJarFile();

        List<JarEntry> entries = new ArrayList<JarEntry>();
        // make sure that the dirPath is a directory and exists
        if (jarFile.getEntry(dirPath) != null && jarFile.getEntry(dirPath).isDirectory()) {
            Enumeration<JarEntry> entryEnumeration = jarFile.entries();
            while (entryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = entryEnumeration.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.contains(dirPath) && !entryName.endsWith("/") && entryName.endsWith(".sql")) {
                    logger.finest("Found jar sql entry [" + entryName + "] within module [" +
                        moduleJar.getModuleName() + "]");
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
