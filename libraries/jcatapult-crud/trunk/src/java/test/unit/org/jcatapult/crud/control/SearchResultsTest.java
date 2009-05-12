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
package org.jcatapult.crud.control;

import java.io.IOException;
import java.sql.SQLException;

import net.java.io.FileTools;
import org.jcatapult.crud.BaseTest;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.test.ControlTestRunner;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the results
 * </p>
 *
 * @author Brian Pontarelli
 */
@Ignore
public class SearchResultsTest extends BaseTest {
    @Inject public SearchResults searchResults;
    @Inject public TestAction action;

    @Test
    public void simple() throws SQLException, IOException {
        clearTable("User");
        for (int i = 0; i < 300; i++) {
            makeUser("user" + i, i);
        }

        // Simulate the setup
        action.prepare();
        action.searchCriteria.setPage(7);
        action.execute();
        
        ControlTestRunner runner = new ControlTestRunner();
        runner.test(searchResults).
            withActionInvocation(new DefaultActionInvocation(action, "/test", null, null)).
            withAttribute("name", "user").
            withAttribute("properties", "name,age").
//            expect("");
            expect(FileTools.read("src/java/test/unit/org/jcatapult/crud/control/result.txt").toString());
    }

    @Test
    public void simpleNonPersistent() throws SQLException, IOException {
        // Simulate the setup
        action.persistent = false;
        action.prepare();
        action.searchCriteria.setPage(7);
        action.execute();

        ControlTestRunner runner = new ControlTestRunner();
        runner.test(searchResults).
            withActionInvocation(new DefaultActionInvocation(action, "/test", null, null)).
            withAttribute("name", "user").
            withAttribute("properties", "name,age").
//            expect("");
            expect(FileTools.read("src/java/test/unit/org/jcatapult/crud/control/non-persistent-result.txt").toString());
    }
}
