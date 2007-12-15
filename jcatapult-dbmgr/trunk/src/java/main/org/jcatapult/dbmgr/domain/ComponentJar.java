package org.jcatapult.dbmgr.domain;

import java.io.File;
import java.util.jar.JarFile;

import net.java.util.Version;

/**
 * Immutable bean to model a component jar
 */
public class ComponentJar {

    // root directory where all the component sql and configuration files live
    public static final String DIR_ROOT = "META-INF";

    // path to the component.xml
    public static final String PATH_COMPONENT_XML = DIR_ROOT + "/component.xml";

    // directories to sql files
    public static final String DIR_BASE = DIR_ROOT + "/sql/base";
    public static final String DIR_ALTER = DIR_ROOT + "/sql/alter";
    public static final String DIR_SEED = DIR_ROOT + "/sql/seed";

    // path to the tables.sql file
    public static final String PATH_TABLES_SQL = DIR_BASE + "/tables.sql";

    private String componentName;
    private File file;
    private JarFile jarFile;
    private Version version;

    /**
     * Contructor for instantiation ComponentJar objects
     *
     * @param componentName the componentName of the component
     * @param file the jar file
     * @param version component jar version
     * @param jarFile jar file object
     */
    public ComponentJar(String componentName, File file, Version version, JarFile jarFile) {
        this.componentName = componentName;
        this.file = file;
        this.version = version;
        this.jarFile = jarFile;

    }

    public String getComponentName() {
        return componentName;
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
        return componentName;
    }
}
