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

import java.io.IOException;
import java.sql.Connection;

import org.jcatapult.migration.database.DatabaseProviderFactory;
import org.jcatapult.migration.service.ArtifactScriptVersionSortStrategyImpl;
import org.jcatapult.migration.service.ComponentJarService;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * User: jhumphrey
 * Date: Dec 13, 2007
 */
public class TableGeneratorTest extends BaseTest {

    protected ComponentJarService cjs;

    @Inject
    public void setComponentJarService(ComponentJarService cjs) {
        this.cjs = cjs;
    }

    @Test
    public void testTableGenProject5() throws IOException {
        TableGenerator tg = new TableGenerator(new ArtifactScriptVersionSortStrategyImpl(),
            getProjectArtifact("project5", null), getMySQL5Connection("table_generator_test_project5"));
        tg.generate(false);
    }

    @Test
    public void testTableComponent1() throws IOException {

        String dbURL = "jdbc:mysql://localhost:3306/table_generator_test_component1" + "?user=dev&password=dev";
        String dbType = "mysql5";

        Connection c = DatabaseProviderFactory.getProvider(dbType, dbURL).getConnection();

        TableGenerator tg = new TableGenerator(new ArtifactScriptVersionSortStrategyImpl(),
            getComponentArtifact("component1", "1.2", null, cjs), c);
        tg.generate(false);
    }
}
