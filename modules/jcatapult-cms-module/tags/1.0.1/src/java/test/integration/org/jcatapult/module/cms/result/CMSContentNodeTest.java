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
package org.jcatapult.module.cms.result;

import java.util.Locale;

import org.jcatapult.module.cms.BaseTest;
import org.jcatapult.module.cms.domain.CMSMode;
import org.jcatapult.module.cms.domain.ContentType;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.test.ControlTestRunner;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the CMSContentNode control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CMSContentNodeTest extends BaseTest {
    @Inject
    CMSContentNode cmsContentNode;

    @Test
    public void testDefault() {
        ControlTestRunner runner = new ControlTestRunner();
        runner.test(cmsContentNode).
            withActionInvocation(new DefaultActionInvocation(null, "/page", null, null)).
            withAttribute("name", "control-test-default").
            withBody("Default content").
            expect("<div class=\"cms-node\" id=\"control-test-default\">Default content</div>");
    }

    @Test
    public void testDatabase() {
        service.storeContent("localhost", "/page", "control-test-database", Locale.US, "Database content", ContentType.HTML, false, publisher);
        request.setUri("/page");

        ControlTestRunner runner = new ControlTestRunner();
        runner.test(cmsContentNode).
            withActionInvocation(new DefaultActionInvocation(null, "/page", null, null)).
            withAttribute("name", "control-test-database").
            withBody("Default content").
            expect("<div class=\"cms-node\" id=\"control-test-database\">Database content</div>");
    }

    @Test
    public void testEditable() {
        service.storeContent("localhost", "/page", "control-test-database", Locale.US, "Database content", ContentType.HTML, false, publisher);
        request.setUri("/page");
        request.getSession().setAttribute("cmsMode", CMSMode.EDIT);

        ControlTestRunner runner = new ControlTestRunner();
        runner.test(cmsContentNode).
            withActionInvocation(new DefaultActionInvocation(null, "/page", null, null)).
            withAttribute("name", "control-test-database").
            withBody("Default content").
            expect("<div class=\"cms-node cms-node-editable\" id=\"control-test-database\" onclick=\"top.CMS.edit_content_node('control-test-database');\">Database content</div>" +
                "<script type=\"text/javascript\">top.CMS.register_content_node('control-test-database', 'control-test-database', false, 'Database content');</script>");
    }

    @Test
    public void testEditableWithAnotherClass() {
        service.storeContent("localhost", "/page", "control-test-database", Locale.US, "Database content", ContentType.HTML, false, publisher);
        request.setUri("/page");
        request.getSession().setAttribute("cmsMode", CMSMode.EDIT);

        ControlTestRunner runner = new ControlTestRunner();
        runner.test(cmsContentNode).
            withActionInvocation(new DefaultActionInvocation(null, "/page", null, null)).
            withAttribute("class", "some-class").
            withAttribute("name", "control-test-database").
            withBody("Default content").
            expect("<div class=\"some-class cms-node cms-node-editable\" id=\"control-test-database\" onclick=\"top.CMS.edit_content_node('control-test-database');\">Database content</div>" +
                "<script type=\"text/javascript\">top.CMS.register_content_node('control-test-database', 'control-test-database', false, 'Database content');</script>");
    }
}