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

import java.io.File;
import java.util.jar.JarFile;

import net.java.util.Version;

/**
 * Immutable bean to model a module jar
 */
public class ModuleJar {

    // root directory where all the module sql and configuration files live
    public static final String DIR_ROOT = "META-INF";

    // path to the module.xml
    public static final String PATH_MODULE_XML = DIR_ROOT + "/module.xml";

    @Deprecated
    public static final String PATH_COMPONENT_XML = DIR_ROOT + "/component.xml";

    // directories to sql files
    public static final String DIR_BASE = DIR_ROOT + "/sql/base";
    public static final String DIR_ALTER = DIR_ROOT + "/sql/alter";
    public static final String DIR_SEED = DIR_ROOT + "/sql/seed";

    // path to the tables.sql file
    public static final String PATH_TABLES_SQL = DIR_BASE + "/tables.sql";

    private String moduleName;
    private File file;
    private JarFile jarFile;
    private Version version;

    /**
     * Contructor for instantiation ModuleJar objects
     *
     * @param moduleName the moduleName of the module
     * @param file the jar file
     * @param version module jar version
     * @param jarFile jar file object
     */
    public ModuleJar(String moduleName, File file, Version version, JarFile jarFile) {
        this.moduleName = moduleName;
        this.file = file;
        this.version = version;
        this.jarFile = jarFile;

    }

    public String getModuleName() {
        return moduleName;
    }

    public File getFile() {
        return file;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return moduleName;
    }
}
