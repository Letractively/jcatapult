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

import java.util.ArrayList;
import java.util.Locale;

import org.easymock.EasyMock;
import org.jcatapult.module.cms.BaseTest;
import org.jcatapult.module.cms.domain.ContentNode;
import org.jcatapult.module.cms.service.ContentService;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the Fetch action.
 * </p>
 *
 * @author  Scaffolder
 */
public class FetchTest extends BaseTest {
    @Test
    public void testGetPage() {
        ContentNode node = new ContentNode();
        ContentService contentService = EasyMock.createStrictMock(ContentService.class);
        EasyMock.expect(contentService.findPageContent("localhost", "/page", "name")).andReturn(node);
        EasyMock.replay(contentService);

        Fetch action = new Fetch(contentService, request, Locale.US);
        action.queries = new ArrayList<NodeQuery>();
        action.queries.add(new NodeQuery());
        action.queries.get(0).global = false;
        action.queries.get(0).name = "name";
        action.queries.get(0).uri = "/page";

        String result = action.get();
        assertEquals("success", result);
        assertSame(node, action.results.get(0).node);
    }

    @Test
    public void testGetGlobal() {
        ContentNode node = new ContentNode();
        ContentService contentService = EasyMock.createStrictMock(ContentService.class);
        EasyMock.expect(contentService.findGlobalContent("localhost", "name")).andReturn(node);
        EasyMock.replay(contentService);

        Fetch action = new Fetch(contentService, request, Locale.US);
        action.queries = new ArrayList<NodeQuery>();
        action.queries.add(new NodeQuery());
        action.queries.get(0).global = true;
        action.queries.get(0).name = "name";
        action.queries.get(0).uri = "/page";

        String result = action.get();
        assertEquals("success", result);
        assertSame(node, action.results.get(0).node);
    }
}