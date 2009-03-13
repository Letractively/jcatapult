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
 *
 */
package org.jcatapult.module.cms.action.admin.cms.content;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;

import org.jcatapult.module.cms.BaseIntegrationTest;
import org.jcatapult.module.cms.domain.ContentType;
import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class performs the integration test for the Fetch action.
 * </p>
 *
 * @author  Scaffolder
 */
public class FetchIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testGet() throws IOException, ServletException {
        EnhancedSecurityContext.login(publisher);

        contentService.storeContent("localhost", "/page", "callout", Locale.US, "Some content", ContentType.HTML, false, publisher);

        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/cms/content/fetch").
            withParameter("queries[0].global", "false").
            withParameter("queries[0].uri", "/page").
            withParameter("queries[0].name", "callout").
            get();

        String result = runner.response.getStream().toString();
        System.out.println("Result is " + result);
        assertTrue(result.contains("\"callout\": {"));
        assertTrue(result.contains("\"visible\": true"));
        assertTrue(result.contains("\"content\": \"Some content\""));
    }
}