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

import java.util.Locale;

import org.easymock.EasyMock;
import org.jcatapult.module.cms.BaseTest;
import org.jcatapult.module.cms.domain.ContentNode;
import org.jcatapult.module.cms.domain.ContentType;
import org.jcatapult.module.cms.service.ContentService;
import org.jcatapult.module.cms.service.CreateResult;
import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.security.EnhancedSecurityContext;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the Store action.
 * </p>
 *
 * @author  Scaffolder
 */
public class StoreTest extends BaseTest {
    @Test
    public void testPostPage() {
        Identifiable user = new Identifiable() {
            public Integer getId() {
                return 1;
            }

            public void setId(Integer id) {
            }
        };
        EnhancedSecurityContext.login(user);

        CreateResult<ContentNode> createResult = new CreateResult<ContentNode>(null, true, false);
        ContentService contentService = EasyMock.createStrictMock(ContentService.class);
        EasyMock.expect(contentService.storeContent("localhost", "/page", "name", Locale.US, "The content", ContentType.HTML, true, user)).andReturn(createResult);
        EasyMock.replay(contentService);

        Store action = new Store(contentService, request, null);
        action.content = "The content";
        action.dynamic = true;
        action.global = false;
        action.uri = "/page";
        action.locale = Locale.US;
        action.name = "name";
        action.contentType = ContentType.HTML;

        String result = action.post();
        assertEquals("success", result);
        assertSame(createResult, action.result);
    }

    @Test
    public void testPostGlobal() {
        Identifiable user = new Identifiable() {
            public Integer getId() {
                return 1;
            }

            public void setId(Integer id) {
            }
        };
        EnhancedSecurityContext.login(user);

        CreateResult<ContentNode> createResult = new CreateResult<ContentNode>(null, true, false);
        ContentService contentService = EasyMock.createStrictMock(ContentService.class);
        EasyMock.expect(contentService.storeContent("localhost", null, "name", Locale.US, "The content", ContentType.HTML, false, user)).andReturn(createResult);
        EasyMock.replay(contentService);

        Store action = new Store(contentService, request, null);
        action.content = "The content";
        action.dynamic = false;
        action.global = true;
        action.locale = Locale.US;
        action.name = "name";
        action.contentType = ContentType.HTML;

        String result = action.post();
        assertEquals("success", result);
        assertSame(createResult, action.result);
    }
}