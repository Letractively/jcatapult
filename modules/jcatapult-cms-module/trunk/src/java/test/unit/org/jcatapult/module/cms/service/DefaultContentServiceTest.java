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
package org.jcatapult.module.cms.service;

import java.util.List;
import java.util.Locale;

import org.jcatapult.module.cms.BaseTest;
import org.jcatapult.module.cms.domain.ActionType;
import org.jcatapult.module.cms.domain.ContentNode;
import org.jcatapult.module.cms.domain.ContentType;
import org.jcatapult.module.cms.domain.NodeAction;
import org.jcatapult.module.cms.domain.NodeActionStateType;
import org.jcatapult.module.cms.domain.NodeState;
import org.jcatapult.module.cms.domain.PageNode;
import org.jcatapult.module.cms.domain.SiteNode;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the DefaultContentService based primarily on the
 * use cases.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultContentServiceTest extends BaseTest {
    @Test
    public void testEditorCreatesSite() {
        CreateResult<SiteNode> result = service.createSite("editor-site", editor);
        assertNotNull(result);
        assertNotNull(result.getNode());
        assertTrue(result.isCreated());
        assertTrue(result.isPending());

        persistenceService.clearCache();
        List<SiteNode> siteNodes = persistenceService.queryAll(SiteNode.class,
            "select sn from SiteNode sn where sn.uid = ?1", "editor-site");
        assertEquals(1, siteNodes.size());

        SiteNode siteNode = siteNodes.get(0);
        assertNotNull(siteNode);

        List<NodeAction> actions = siteNode.getActions();
        assertEquals(1, actions.size());
        assertNull(actions.get(0).getContent());
        assertEquals(NodeActionStateType.PENDING, actions.get(0).getCurrentState());
        assertEquals(editor, actions.get(0).getUser());
        assertEquals(siteNode, actions.get(0).getNode());
        assertEquals(1, actions.get(0).getNodeActionStates().size());
        assertEquals(NodeActionStateType.PENDING, actions.get(0).getNodeActionStates().get(0).getState());
        assertEquals(ActionType.CREATE, actions.get(0).getType());

        List<NodeState> states = siteNode.getStates();
        assertEquals(0, states.size());

        assertFalse(siteNode.isVisible());
        assertEquals("editor-site", siteNode.getLocalName());
        assertEquals("editor-site", siteNode.getUid());
        assertEquals(0, siteNode.getGlobalContent().size());
        assertEquals(0, siteNode.getPages().size());
    }

    /**
     * This tests a publisher creating content on a static page.
     */
    @Test
    public void testPublisherCreateContentStaticPage() {
        CreateResult<ContentNode> result = service.storeContent("publisher-create-content-static-page", "/namespace/page",
            "nodeA", Locale.US, "The content A", ContentType.HTML, false, publisher);
        assertNotNull(result);
        assertNotNull(result.getNode());
        assertTrue(result.isCreated());
        assertFalse(result.isPending());
        assertTrue(result.isSuccess());

        persistenceService.clearCache();

        ContentNode node = persistenceService.queryFirst(ContentNode.class,
            "select cn from ContentNode cn where cn.uid = 'http://publisher-create-content-static-page/namespace/page<nodeA>'");
        assertNotNull(node);
        assertEquals("http://publisher-create-content-static-page/namespace/page", node.getParent().getUid());
        assertEquals("http://publisher-create-content-static-page", node.getParent().getParent().getUid());
        assertTrue(node.getParent() instanceof PageNode);
        assertTrue(node.getParent().getParent() instanceof SiteNode);
        assertEquals("nodeA", node.getLocalName());
        assertEquals(ContentType.HTML, node.getContentType());

        // Current content
        assertEquals(1, node.getCurrentContents().size());
        assertEquals("The content A", node.getCurrentContents().get(Locale.US).getContent());
        assertEquals(1, node.getCurrentContents().get(Locale.US).getStates().size());
        assertTrue(node.getCurrentContents().get(Locale.US).getStates().get(0).isVisible());

        // Historical content
        assertEquals(1, node.getLocaleContent().size());
        assertEquals(1, node.getLocaleContent().get(Locale.US).getContents().size());
        assertSame(node.getCurrentContents().get(Locale.US), node.getLocaleContent().get(Locale.US).getContents().get(0));

        // Actions
        assertEquals(1, node.getActions().size());
        assertEquals(NodeActionStateType.APPROVED, node.getActions().get(0).getCurrentState());
        assertEquals(1, node.getActions().get(0).getNodeActionStates().size());
        assertEquals(NodeActionStateType.APPROVED, node.getActions().get(0).getNodeActionStates().get(0).getState());
        assertEquals(publisher, node.getActions().get(0).getNodeActionStates().get(0).getUser());
        assertEquals(ActionType.CREATE, node.getActions().get(0).getType());
        assertEquals(publisher, node.getActions().get(0).getUser());
        assertNotNull(node.getActions().get(0).getContent());
        assertSame(node.getCurrentContents().get(Locale.US), node.getActions().get(0).getContent());
    }

    /**
     * This tests a publisher creating global static content.
     */
    @Test
    public void testPublisherCreateGlobalStaticContent() {
        CreateResult<ContentNode> result = service.storeContent("publisher-create-global-static-content", null,
            "nodeA", Locale.US, "The content A", ContentType.HTML, false, publisher);
        assertNotNull(result);
        assertNotNull(result.getNode());
        assertTrue(result.isCreated());
        assertFalse(result.isPending());
        assertTrue(result.isSuccess());

        persistenceService.clearCache();

        ContentNode node = persistenceService.queryFirst(ContentNode.class,
            "select cn from ContentNode cn where cn.uid = 'http://publisher-create-global-static-content<nodeA>'");
        assertNotNull(node);
        assertEquals("http://publisher-create-global-static-content", node.getParent().getUid());
        assertTrue(node.getParent() instanceof SiteNode);
        assertEquals("nodeA", node.getLocalName());
        assertEquals(ContentType.HTML, node.getContentType());

        // Current content
        assertEquals(1, node.getCurrentContents().size());
        assertEquals("The content A", node.getCurrentContents().get(Locale.US).getContent());
        assertEquals(1, node.getCurrentContents().get(Locale.US).getStates().size());
        assertTrue(node.getCurrentContents().get(Locale.US).getStates().get(0).isVisible());

        // Historical content
        assertEquals(1, node.getLocaleContent().size());
        assertEquals(1, node.getLocaleContent().get(Locale.US).getContents().size());
        assertSame(node.getCurrentContents().get(Locale.US), node.getLocaleContent().get(Locale.US).getContents().get(0));

        // Actions
        assertEquals(1, node.getActions().size());
        assertEquals(NodeActionStateType.APPROVED, node.getActions().get(0).getCurrentState());
        assertEquals(1, node.getActions().get(0).getNodeActionStates().size());
        assertEquals(NodeActionStateType.APPROVED, node.getActions().get(0).getNodeActionStates().get(0).getState());
        assertEquals(publisher, node.getActions().get(0).getNodeActionStates().get(0).getUser());
        assertEquals(ActionType.CREATE, node.getActions().get(0).getType());
        assertEquals(publisher, node.getActions().get(0).getUser());
        assertNotNull(node.getActions().get(0).getContent());
        assertSame(node.getCurrentContents().get(Locale.US), node.getActions().get(0).getContent());
    }
}