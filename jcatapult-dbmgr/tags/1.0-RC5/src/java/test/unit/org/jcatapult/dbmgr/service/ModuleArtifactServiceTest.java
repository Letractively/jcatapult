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
import java.util.List;
import java.util.SortedSet;
import java.util.jar.JarFile;

import org.jcatapult.dbmgr.domain.Artifact;
import org.jcatapult.dbmgr.domain.ModuleContext;
import org.jcatapult.dbmgr.domain.ModuleJar;
import org.jcatapult.dbmgr.domain.SQLScript;
import static org.jcatapult.dbmgr.domain.SQLScriptType.*;
import org.jcatapult.dbmgr.module.ModuleJarTools;
import org.jcatapult.dbmgr.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import net.java.util.Version;
import com.google.inject.Inject;

/**
 * User: jhumphrey
 * Date: Dec 13, 2007
 */
public class ModuleArtifactServiceTest extends BaseTest {

    protected ModuleJarService cjs;

    @Inject
    public void setModuleJarService(ModuleJarService cjs) {
        this.cjs = cjs;
    }

    @Test
    public void testModuleService() throws IOException {
        File file = new File("target/jars/module1-1.2.jar");
        ModuleJar cj = new ModuleJar("module1", file, ModuleJarTools.getVersionFromJarFilename(file.getName()), new JarFile(file));
        ModuleContext cCtx = new ModuleContext(cj, new Version("0.0.0"));

        ArtifactService as = new ModuleArtifactService(cCtx, cjs);

        Artifact a = as.getArtifact();

        SortedSet<SQLScript> scriptSet;

        // assert on base scripts
        {
            scriptSet = a.getBaseScripts();
            Assert.assertEquals(1, scriptSet.size());

            List<SQLScript> scripts = new ArrayList<SQLScript>(scriptSet);
            SQLScript script = scripts.get(0);
            Assert.assertEquals("tables.sql", script.getFilename());
            Assert.assertEquals(BASE, script.getType());
            Assert.assertEquals(new Version("0.0.0"), script.getVersion());
        }

        // assert on alter scripts
        {
            scriptSet = a.getAlterScripts();
            Assert.assertEquals(2, scriptSet.size());

            List<SQLScript> scripts = new ArrayList<SQLScript>(scriptSet);
            SQLScript script;
            {
                script = scripts.get(0);
                Assert.assertEquals("1.0-alter.sql", script.getFilename());
                Assert.assertEquals(ALTER, script.getType());
                Assert.assertEquals(new Version("1.0"), script.getVersion());
            }
            {
                script = scripts.get(1);
                Assert.assertEquals("1.1-alter.sql", script.getFilename());
                Assert.assertEquals(ALTER, script.getType());
                Assert.assertEquals(new Version("1.1"), script.getVersion());
            }
        }

        // assert on seed scripts
        {
            scriptSet = a.getSeedScripts();
            Assert.assertEquals(3, scriptSet.size());

            List<SQLScript> scripts = new ArrayList<SQLScript>(scriptSet);
            SQLScript script;
            {
                script = scripts.get(0);
                Assert.assertEquals("1.0-seed.sql", script.getFilename());
                Assert.assertEquals(SEED, script.getType());
                Assert.assertEquals(new Version("1.0"), script.getVersion());
            }
            {
                script = scripts.get(1);
                Assert.assertEquals("1.1-seed.sql", script.getFilename());
                Assert.assertEquals(SEED, script.getType());
                Assert.assertEquals(new Version("1.1"), script.getVersion());
            }
            {
                script = scripts.get(2);
                Assert.assertEquals("1.2-seed.sql", script.getFilename());
                Assert.assertEquals(SEED, script.getType());
                Assert.assertEquals(new Version("1.2"), script.getVersion());
            }
        }
    }
}