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
package org.jcatapult.migration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jcatapult.migration.database.DbTools;
import org.w3c.dom.Document;

import net.java.lang.URLClassLoaderResolver;
import net.java.sql.ScriptExecutor;
import net.java.util.Version;

/**
 * <p>
 * This class is used to apply a set of run files to a database.
 * This requires that the database connection has already been established
 * and that the database is correctly setup to handle the SQL statements
 * from the run files.
 * </p>
 *
 * <p>
 * Patch files are located on the file system in a specific directory.
 * Each file within that directory might be executed, depending on how
 * this patcher is constructed.
 * </p>
 *
 * <p>
 * The run looks are the last version, which is essentially the last
 * run file that was executed on the current database. This allows
 * a production or staging database to be patched by figuring out what
 * the version of the database in that environment is and passing that
 * value into the patcher. The patcher will then only apply run files
 * that are newer than that version.
 * </p>
 *
 * <p>
 * Patch files must have a specific naming scheme in order to determine
 * what their currentVersions are. The name must be in this form:
 * </p>
 *
 * <pre>
 * version-name.sql
 * </pre>
 *
 * <p>
 * For example, <code>14-update-user-table.sql</code> could be a run
 * file. The version must be an integer value that is parsable be the
 * Integer class. This version will determine what the version of
 * the database is after the patcher has run all of the run files as
 * well as whice run files to run and the order to run them in.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class PatcherSeeder {
    private static final Logger logger = Logger.getLogger("DatabaseMigrator");
    private final Connection connection;
    private final String applicationName;
    private final File patchDir;
    private final File seedDir;
    private final boolean patch;

    private final Map<String, Version> versions;

    /**
     * Creates a new database patcher that will use the given connection, run SQL directory and
     * last version number.
     *
     * @param   connection The JDBC connection to use when patching.
     * @param   applicationName The name of this project used to determine currentVersions.
     * @param   patchDir The directory on the file system that contains the patch files.
     * @param   seedDir The directory on the file system that contains the seed files.
     * @param   patch Determines if the patch files should be run. If the database loader is using
     *          Hibernate to create the tables, the patch files should not be executed.
     * @param   versions The versions from the database.
     * @throws  IllegalArgumentException If the run SQL directory is not valid.
     */
    public PatcherSeeder(Connection connection, String applicationName, File patchDir, File seedDir,
            boolean patch, Map<String, Version> versions) {
        if (patchDir.isFile()) {
            throw new IllegalArgumentException("Invalid run SQL directory [" + patchDir.getAbsolutePath() +
                "]. That is a file.");
        }

        this.connection = connection;
        this.applicationName = applicationName;
        this.patchDir = patchDir;
        this.seedDir = seedDir;
        this.patch = patch;
        this.versions = versions;
    }

    /**
     * Runs the run files using the method described in the class comment.
     *
     * @throws  IOException If anything goes wrong while patching.
     * @throws  SQLException If loading or updating the currentVersions table failed.
     */
    public void patchSeed() throws IOException, SQLException {
        logger.info("Patching existing database");
        DbTools.disableKeys(connection);

        try {
            Map<String, String> componentJarMapping = new HashMap<String, String>();
            Enumeration<URL> componentURLs = this.getClass().getClassLoader().getResources("META-INF/component.xml");
            while (componentURLs.hasMoreElements()) {
                URL url = componentURLs.nextElement();
                String jarPath = determineJarPath(url);
                if (jarPath == null) {
                    logger.warning("Found a component.xml file at [" + url + "] but it was not in a JAR, so it was skipped");
                    continue;
                }

                // Find tables.sql
                JarFile jf = new JarFile(new File(URLDecoder.decode(jarPath, "UTF-8")));
                JarEntry je = jf.getJarEntry("META-INF/sql/create/tables.sql");
                if (je == null) {
                    logger.warning("Found a component.xml file at [" + url + "] but the JAR is missing the META-INF/sql files required, so it was skipped");
                    continue;
                }
                jf.close();

                String componentName = parseComponentName(url);
                if (componentName == null) {
                    throw new IllegalArgumentException("Found component.xml file at [" + url + "] but it doesn't have a name attribute on the root element.");
                }

                componentJarMapping.put(jarPath, componentName);
                logger.info("Found component at [" + jarPath + "] named [" + componentName + "]");
            }

            // Load all the patch files
            Map<String, Version> newVersions = new HashMap<String, Version>(versions);
            Map<String, Struct> classPathPatchStructs = null;
            Map<String, Struct> localPatchStructs = null;
            if (patch) {
                classPathPatchStructs = loadClasspath("META-INF/sql/patch", componentJarMapping, newVersions);
                localPatchStructs = loadLocal(patchDir, newVersions);
            }

            Map<String, Struct> classPathSeedStructs = loadClasspath("META-INF/sql/seed", componentJarMapping, newVersions);
            Map<String, Struct> localSeedStructs = loadLocal(seedDir, newVersions);

            // Now run everything in order
            run(classPathPatchStructs, classPathSeedStructs);
            run(localPatchStructs, localSeedStructs);

            // Update versions
            for (String name : newVersions.keySet()) {
                Version previous = versions.get(name);
                Version current = newVersions.get(name);
                if (previous == null || current.compareTo(previous) > 0) {
                    versions.put(name, current);
                }
            }
        } finally {
            // Finally enable the keys again
            DbTools.enableKeys(connection);
        }
    }

    private String parseComponentName(URL url) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = builder.parse(url.openStream());
            return dom.getDocumentElement().getAttribute("name");
        } catch (Exception e) {
            // Bail!
            throw new RuntimeException("Unable to parse component.xml file", e);
        }
    }

    /**
     * Loads all the local SQL files.
     *
     * @param   dir The directory where the SQL files to run are located.
     * @param   newVersions A Map that will store the latest version being loaded.
     * @return  The Map of Struct classes.
     * @throws  IOException If the run failed.
     * @throws  SQLException If updating the currentVersions table failed.
     */
    private Map<String, Struct> loadLocal(File dir, Map<String, Version> newVersions) throws IOException, SQLException {
        if (!dir.exists()) {
            return new HashMap<String, Struct>();
        }

        Version version = determineNextVersion(applicationName);
        Map<String, Struct> structs = new HashMap<String, Struct>();
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }
        });

        Struct struct = new Struct();
        struct.componentName = applicationName;
        struct.version = version;
        for (File file : files) {
            String path = file.toURI().toString();
            int index = path.lastIndexOf("/");
            String numberStr = path.substring(index + 1).split("-", 2)[0];
            Version fileVersion = new Version(numberStr);
            if (fileVersion.compareTo(version) >= 0) {
                struct.urls.put(fileVersion, file.toURI().toURL());
            }

            // Update the new versions
            Version highest = newVersions.get(struct.componentName);
            if (highest == null || highest.compareTo(fileVersion) < 0) {
                newVersions.put(struct.componentName, fileVersion);
            }
        }

        if (struct.urls.size() > 0) {
            structs.put(applicationName, struct);
        }

        return structs;
    }

    /**
     * Loads all the SQL files from the classpath located in the given path.
     *
     * @param   path The path to locate and run all the SQL files.
     * @param   componentJarMapping Used to translate between the JAR in the classpath and a component
     *          name.
     * @param   newVersions A Map that will store the latest version being loaded.
     * @return  A Map of component names to Struct instances.
     * @throws  IOException If the patch fails.
     * @throws  SQLException If updating the currentVersions table failed.
     */
    private Map<String, Struct> loadClasspath(String path, Map<String, String> componentJarMapping, Map<String, Version> newVersions)
    throws IOException, SQLException {
        URLClassLoaderResolver resolver = new URLClassLoaderResolver();
        resolver.find(new URLClassLoaderResolver.NameEndsWith(".sql"), true, path);

        Map<String, Struct> jarInfo = new HashMap<String, Struct>();
        Set<URL> urls = resolver.getMatches();
        for (URL url : urls) {
            String jarPath = determineJarPath(url);
            Struct struct = jarInfo.get(jarPath);
            if (struct == null) {
                struct = new Struct();
                struct.componentName = componentJarMapping.get(jarPath);
                struct.version = determineNextVersion(struct.componentName);
                jarInfo.put(jarPath, struct);
            }

            String urlStr = url.toString();
            int index = urlStr.lastIndexOf("/");
            String numberStr = urlStr.substring(index + 1).split("-", 2)[0];
            Version version = new Version(numberStr);
            if (version.compareTo(struct.version) >= 0) {
                struct.urls.put(version, url);
            }

            // Update the new versions
            Version highest = newVersions.get(struct.componentName);
            if (highest == null || highest.compareTo(version) < 0) {
                newVersions.put(struct.componentName, version);
            }
        }

        return jarInfo;
    }

    private void run(Map<String, Struct> patchStructs, Map<String, Struct> seedStructs)
    throws IOException, SQLException {
        Set<String> componentNames = new HashSet<String>();
        if (patchStructs != null) {
            componentNames.addAll(patchStructs.keySet());
        }
        componentNames.addAll(seedStructs.keySet());

        for (String componentName : componentNames) {

            // If there are patches for this component, set them up
            Struct patch = null;
            Iterator<Version> patchVersions = null;
            Version patchVersion = null;
            if (patchStructs != null) {
                patch = patchStructs.get(componentName);
                if (patch != null) {
                    patchVersions = patch.urls.keySet().iterator();
                    patchVersion = patchVersions.hasNext() ? patchVersions.next() : null;
                }
            }

            // If the component has seed files, grab the version
            Struct seed = seedStructs.get(componentName);
            Iterator<Version> seedVersions = null;
            Version seedVersion = null;
            if (seed != null) {
                seedVersions = seed.urls.keySet().iterator();
                seedVersion = seedVersions.hasNext() ? seedVersions.next() : null;
            }

            while (patchVersion != null || seedVersion != null) {
                while (seedVersion != null && (patchVersion == null || seedVersion.compareTo(patchVersion) < 0)) {
                    run(seed.urls.get(seedVersion));
                    seedVersion = seedVersions.hasNext() ? seedVersions.next() : null;
                }

                while (patchVersion != null && (seedVersion == null || patchVersion.compareTo(seedVersion) <= 0)) {
                    run(patch.urls.get(patchVersion));
                    patchVersion = patchVersions.hasNext() ? patchVersions.next() : null;
                }
            }
        }
    }

    private void run(URL url) throws IOException {
        logger.info("Running SQL script at [" + url + "]");
        ScriptExecutor executor = new ScriptExecutor(connection);
        executor.execute(url);
    }

    private String determineJarPath(URL url) {
        String urlStr = url.toString();
        int index = urlStr.indexOf('!');
        if (index <= 0) {
            return null;
        }

        // Strip the file: url part and the !/path part
        int prefixLength = urlStr.startsWith("jar:file:") ? 9 : 5;
        return urlStr.substring(prefixLength, index);
    }

    /**
     * Determines the current version for a component and figures out the next version. This is
     * accomplished by incrementing the patch number. This is important because it will cause the
     * patcher only to execute SQL scripts that contain currentVersions AFTER the current version.
     *
     * @param   componentName The name of the component.
     * @return  The next Version of the component from the <strong>version</strong> database table
     *          as described in the method commnt or a new version of <strong>0.0</strong> if there
     *          isn't a version for the component in the database.
     */
    private Version determineNextVersion(String componentName) {
        Version version = versions.get(componentName);
        if (version == null) {
            version = Version.ZERO;
        }

        return new Version(version.getMajor(), version.getMinor(), version.getPatch() + 1);
    }

    class Struct {
        SortedMap<Version, URL> urls = new TreeMap<Version, URL>();
        String componentName;
        Version version;
    }
}