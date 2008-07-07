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

import java.io.IOException;
import java.util.Queue;

import org.jcatapult.dbmgr.BaseTest;
import org.jcatapult.dbmgr.domain.SQLScript;
import org.junit.Assert;
import org.junit.Test;

import net.java.util.Version;
import com.google.inject.Inject;

/**
 * User: jhumphrey
 * Date: Dec 13, 2007
 */
public class ArtifactScriptVersionSortStrategyImplTest extends BaseTest {
    protected ModuleJarService cjs;

    @Inject
    public void setModuleJarService(ModuleJarService cjs) {
        this.cjs = cjs;
    }

//    @Test
//    public void testProject1HighestVersion() {
//        ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
//
//        Version highest = sortStrat.determineHighestScriptVersion(getProjectArtifact("project1", null));
//
//        Assert.assertEquals(new Version("1.1"), highest);
//    }

//    @Test
//    public void testModule1HighestVersion() throws IOException {
//        ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
//
//        Version highest = sortStrat.determineHighestScriptVersion(getModuleArtifact("module1", "1.2", new Version("0.0.0"), cjs));
//
//        Assert.assertEquals(new Version("1.2"), highest);
//    }

    @Test
    public void testProject1Sort() {

        // test with database version set to null
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getProjectArtifact("project1", null), false);

            Assert.assertEquals(4, scripts.size());

            Assert.assertEquals("tables.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.0-alter.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.0-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.1-alter.sql", scripts.remove().getFilename());
        }

        // test with database version set to 0
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getProjectArtifact("project1", new Version("0.0.0")), false);

            Assert.assertEquals(3, scripts.size());

            Assert.assertEquals("1.0-alter.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.0-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.1-alter.sql", scripts.remove().getFilename());
        }

        // test with database version set to 1.0
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getProjectArtifact("project1", new Version("1.0")), false);

            Assert.assertEquals(1, scripts.size());

            Assert.assertEquals("1.1-alter.sql", scripts.remove().getFilename());
        }

        // test with database version set to 1.2
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getProjectArtifact("project1", new Version("1.2")), false);

            Assert.assertEquals(0, scripts.size());
        }
    }

    @Test
    public void testModule1Sort() throws IOException {

        // test with database version set to null
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2", null, cjs), false);

            Assert.assertEquals(6, scripts.size());

            Assert.assertEquals("tables.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.0-alter.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.0-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.1-alter.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.1-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.2-seed.sql", scripts.remove().getFilename());
        }

        // test with database version set to 0
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2",
                new Version("0.0.0"), cjs), false);

            Assert.assertEquals(5, scripts.size());

            Assert.assertEquals("1.0-alter.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.0-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.1-alter.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.1-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.2-seed.sql", scripts.remove().getFilename());
        }

        // test with database version set to 1.0
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2", new Version("1.0"), cjs), false);

            Assert.assertEquals(3, scripts.size());

            Assert.assertEquals("1.1-alter.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.1-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.2-seed.sql", scripts.remove().getFilename());
        }

        // test with database version set to 1.1
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2", new Version("1.1"), cjs), false);

            Assert.assertEquals(1, scripts.size());

            Assert.assertEquals("1.2-seed.sql", scripts.remove().getFilename());
        }

        // test with database version set to 1.3
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2", new Version("1.3"), cjs), false);

            Assert.assertEquals(0, scripts.size());
        }
    }

    @Test
    public void testModule1SortSeedOnly() throws IOException {

        // test with database version set to null
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2", null, cjs), true);

            Assert.assertEquals(3, scripts.size());

            Assert.assertEquals("1.0-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.1-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.2-seed.sql", scripts.remove().getFilename());
        }

        // test with database version set to 0
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2",
                new Version("0.0.0"), cjs), true);

            Assert.assertEquals(3, scripts.size());

            Assert.assertEquals("1.0-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.1-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.2-seed.sql", scripts.remove().getFilename());
        }

        // test with database version set to 1.0
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2", new Version("1.0"), cjs), true);

            Assert.assertEquals(2, scripts.size());

            Assert.assertEquals("1.1-seed.sql", scripts.remove().getFilename());
            Assert.assertEquals("1.2-seed.sql", scripts.remove().getFilename());
        }

        // test with database version set to 1.1
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2", new Version("1.1"), cjs), true);

            Assert.assertEquals(1, scripts.size());

            Assert.assertEquals("1.2-seed.sql", scripts.remove().getFilename());
        }

        // test with database version set to 1.3
        {
            ArtifactScriptVersionSortStrategyImpl sortStrat = new ArtifactScriptVersionSortStrategyImpl();
            Queue<SQLScript> scripts = sortStrat.sort(getModuleArtifact("module1", "1.2", new Version("1.3"), cjs), true);

            Assert.assertEquals(0, scripts.size());
        }
    }
}
