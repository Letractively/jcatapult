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
package org.jcatapult.module.cms.action.admin.cms;

import java.io.IOException;
import javax.servlet.ServletException;

import org.jcatapult.module.cms.BaseIntegrationTest;
import org.jcatapult.module.cms.domain.CMSMode;
import org.jcatapult.mvc.test.RequestBuilder;
import org.jcatapult.mvc.test.WebappTestRunner;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class performs the integration test for the Exit action.
 * </p>
 *
 * @author  Scaffolder
 */
public class ExitIntegrationTest extends BaseIntegrationTest {
    @Test
    public void testStoreStaticPageScoped() throws IOException, ServletException {
        WebappTestRunner runner = new WebappTestRunner();
        RequestBuilder builder = runner.test("/admin/cms/exit");
        builder.get();

        String result = runner.response.getRedirect();
        assertEquals("/", result);
        assertSame(CMSMode.DISPLAY, builder.getRequest().getSession().getAttribute("cmsMode"));
    }
}