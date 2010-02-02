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
import java.util.List;
import java.util.Queue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jcatapult.dbmgr.BaseTest;
import org.jcatapult.dbmgr.module.ModuleJarTools;
import org.jcatapult.dbmgr.domain.ModuleJar;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Inject;
import net.java.util.Version;

/**
 * User: jhumphrey
 * Date: Nov 30, 2007
 */
public class ModuleJarServiceImplTest extends BaseTest {
    private DefaultModuleJarService cjs;

    @Inject
    public void setModuleResolver(DefaultModuleJarService cr) {
        this.cjs = cr;
    }

    @Test
    public void testProject1() {
        verifyQueue(cjs.resolveJars(new File("test/project1/project.xml")));
    }

    @Test
    public void testProject2() {
        verifyQueue(cjs.resolveJars(new File("test/project2/project.xml")));
    }

    @Test
    public void testProject3() {
        verifyQueue(cjs.resolveJars(new File("test/project3/project.xml")));
    }

    @Test
    public void testMajor() {
        String fn = "moduleX-1.0.jar";
        Version v = ModuleJarTools.getVersionFromJarFilename(fn);

        Assert.assertEquals("1.0.0", v.toString());
    }

    @Test
    public void testMinor() {
        String fn = "moduleX-1.1.jar";
        Version v = ModuleJarTools.getVersionFromJarFilename(fn);

        Assert.assertEquals("1.1.0", v.toString());
    }

    @Test
    public void testPatch() {
        String fn = "moduleX-1.1.1.jar";
        Version v = ModuleJarTools.getVersionFromJarFilename(fn);

        Assert.assertEquals("1.1.1", v.toString());
    }

    @Test
    public void testSnapshot() {
        String fn = "moduleX-1.1.1-snapshot.jar";
        Version v = ModuleJarTools.getVersionFromJarFilename(fn);

        Assert.assertEquals("1.1.1-snapshot", v.toString());
    }

    @Test
    public void testGettingDirectoryEntries() throws IOException {
        File file = new File("target/jars/module1-1.2.jar");
        ModuleJar cj = new ModuleJar("module1", file, ModuleJarTools.getVersionFromJarFilename(file.getName()), new JarFile(file));
        List<JarEntry> entries = cjs.getJarDirectorySQLEntries(cj, ModuleJar.DIR_ALTER);

        Assert.assertEquals(2, entries.size());
        Assert.assertEquals(ModuleJar.DIR_ALTER + "/1.0-alter.sql", entries.get(0).getName());
        Assert.assertEquals(ModuleJar.DIR_ALTER + "/1.1-alter.sql", entries.get(1).getName());
    }

    private void verifyQueue(Queue<ModuleJar> moduleJars) {

        ModuleJar c1 = moduleJars.remove();
        ModuleJar c2 = moduleJars.remove();
        ModuleJar c3 = moduleJars.remove();
        ModuleJar c4 = moduleJars.remove();

        Assert.assertEquals("module1", c1.getModuleName());
        Assert.assertEquals("module2", c2.getModuleName());
        Assert.assertEquals("module3", c3.getModuleName());
        Assert.assertEquals("module4", c4.getModuleName());

        Assert.assertEquals("module1-1.2.jar", c1.getFile().getName());
        Assert.assertEquals("module2-1.1.jar", c2.getFile().getName());
        Assert.assertEquals("module3-2.1.jar", c3.getFile().getName());
        Assert.assertEquals("module4-1.0.jar", c4.getFile().getName());
    }
}
