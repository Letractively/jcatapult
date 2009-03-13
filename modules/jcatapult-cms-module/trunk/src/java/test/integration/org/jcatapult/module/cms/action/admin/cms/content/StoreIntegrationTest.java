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
import javax.servlet.ServletException;

import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.module.cms.BaseIntegrationTest;
import org.jcatapult.mvc.test.WebappTestRunner;
import org.jcatapult.security.EnhancedSecurityContext;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class performs the integration test for the Store action.
 * </p>
 *
 * @author  Scaffolder
 */
public class StoreIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testStoreStaticPageScoped() throws IOException, ServletException {
        EnhancedSecurityContext.login(publisher);

        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/cms/content/store").
            withParameter("dynamic", "false").
            withParameter("global", "false").
            withParameter("uri", "/integration-page").
            withParameter("name", "callout").
            withParameter("locale", "en_US").
            withParameter("content", "The content").
            withParameter("contentType", "HTML").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();

        String result = runner.response.getStream().toString();
        assertTrue(result.contains("\"success\": true"));
        assertTrue(result.contains("\"created\": true"));
        assertTrue(result.contains("\"pending\": false"));
    }

    @Test
    public void testStoreErrors() throws IOException, ServletException {
        EnhancedSecurityContext.login(publisher);

        WebappTestRunner runner = new WebappTestRunner();
        runner.test("/admin/cms/content/store").
            withParameter("dynamic", "false").
            withParameter("global", "false").
            withParameter("uri", "/integration-page").
            withParameter("name", "callout").
            withParameter("locale", "en_US").
            withMock(EmailTransportService.class, EmailTestHelper.getService()).
            post();

        String result = runner.response.getStream().toString();
        assertTrue(result.contains("\"success\": false"));
        assertTrue(result.contains("\"errors\": {"));
        assertTrue(result.contains("\"content\": ["));
        assertFalse(result.contains("\"created\": true"));
        assertFalse(result.contains("\"pending\": false"));
    }
}