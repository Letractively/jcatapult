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
import java.util.Set;
import javax.persistence.PersistenceException;

import org.jcatapult.module.cms.domain.ActionType;
import org.jcatapult.module.cms.domain.Content;
import org.jcatapult.module.cms.domain.ContentNode;
import org.jcatapult.module.cms.domain.ContentRole;
import org.jcatapult.module.cms.domain.ContentState;
import org.jcatapult.module.cms.domain.ContentType;
import org.jcatapult.module.cms.domain.LocaleContent;
import org.jcatapult.module.cms.domain.Node;
import org.jcatapult.module.cms.domain.NodeAction;
import org.jcatapult.module.cms.domain.NodeActionState;
import org.jcatapult.module.cms.domain.NodeActionStateType;
import org.jcatapult.module.cms.domain.NodeState;
import org.jcatapult.module.cms.domain.PageNode;
import org.jcatapult.module.cms.domain.SiteNode;
import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.txn.annotation.Transactional;
import org.jcatapult.security.UserAdapter;

import com.google.inject.Inject;
import net.java.lang.reflect.ReflectionTools;

/**
 * <p>
 * This class implements the ContentService interface using JPA via the
 * {@link PersistenceService} from JCatapult Persistence library.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultContentService implements ContentService {
    private final PersistenceService persistenceService;
    private final UserAdapter userAdapater;

    @Inject
    public DefaultContentService(PersistenceService persistenceService, UserAdapter userAdapater) {
        this.persistenceService = persistenceService;
        this.userAdapater = userAdapater;
    }

    /**
     * {@inheritDoc}
     */
    public SiteNode findSite(String site) {
        String uid = makeUID(site, null, null);
        return persistenceService.queryFirst(SiteNode.class, "select sn from SiteNode sn where sn.uid = ?1", uid);
    }

    /**
     * {@inheritDoc}
     */
    public PageNode findPage(String site, String uri) {
        String uid = makeUID(site, uri, null);
        return persistenceService.queryFirst(PageNode.class, "select pn from PageNode pn where pn.uid = ?1", uid);
    }

    /**
     * {@inheritDoc}
     */
    public ContentNode findGlobalContent(String site, String name) {
        String uid = makeUID(site, null, name);
        return persistenceService.queryFirst(ContentNode.class, "select cn from ContentNode cn where cn.uid = ?1", uid);
    }

    /**
     * {@inheritDoc}
     */
    public ContentNode findPageContent(String site, String uri, String name) {
        String uid = makeUID(site, uri, name);
        return persistenceService.queryFirst(ContentNode.class, "select cn from ContentNode cn where cn.uid = ?1", uid);
    }

    /**
     * {@inheritDoc}
     */
    public CreateResult<SiteNode> createSite(String site, Identifiable user) throws PersistenceException {
        CreateResult<SiteNode> result;
        if (canPublish(user)) {
            result = createNodeApproved(SiteNode.class, site, null, null, null, user);
        } else {
            result = createNodePending(SiteNode.class, site, null, null, null, user);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public CreateResult<PageNode> createPage(String site, String uri, Identifiable user) {
        CreateResult<SiteNode> siteResult = createNodeApproved(SiteNode.class, site, null, null, null, user);
        SiteNode siteNode = siteResult.getNode();
        CreateResult<PageNode> result;
        if (canPublish(user)) {
            result = createNodeApproved(PageNode.class, site, uri, null, siteNode, user);
        } else {
            result = createNodePending(PageNode.class, site, uri, null, siteNode, user);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional()
    public CreateResult<ContentNode> storeContent(String site, String uri, String name, Locale locale,
            String content, ContentType type, boolean dynamic, Identifiable user) {
        CreateResult<SiteNode> siteResult = createNodeApproved(SiteNode.class, site, null, null, null, user);
        Node parent = siteResult.getNode();
        if (uri != null) {
            CreateResult<PageNode> pageResult = createNodeApproved(PageNode.class, site, uri, null, parent, user);
            parent = pageResult.getNode();
        }

        CreateResult<ContentNode> result;
        if (canPublish(user)) {
            result = createNodeApproved(ContentNode.class, site, uri, name, parent, user);
            ContentNode node = result.getNode();
            if (node.getContentType() != null && node.getContentType() != type) {
                throw new IllegalArgumentException("You can't change the content type of an existing " +
                    "content node.");
            }

            // Set the type
            node.setContentType(type);

            // If the node wasn't created, still add an update action to it for the content update
            NodeAction action;
            if (!result.isCreated()) {
                action = makeAction(ActionType.UPDATE, NodeActionStateType.APPROVED, node, user);
                action.setDescription(makeDescription(ActionType.UPDATE, ContentNode.class, site, uri, name));
                node.getActions().add(action);
            } else {
                action = node.getActions().get(node.getActions().size() - 1);
            }

            // Fetch or create the locale holder
            LocaleContent localeContent;
            if (node.getLocaleContent().get(locale) != null) {
                 localeContent = node.getLocaleContent().get(locale);
            } else {
                localeContent = new LocaleContent();
                localeContent.setContentNode(node);
                localeContent.setLocale(locale);
                persistenceService.persist(localeContent);
                node.getLocaleContent().put(locale, localeContent);
            }

            Content newContent = new Content();
            newContent.setContent(content);
            newContent.setContentNode(node);
            newContent.setLocale(locale);
            newContent.setLocaleContent(localeContent);
            newContent.setNodeAction(action);
            localeContent.getContents().add(newContent);
            node.getCurrentContents().put(locale, newContent);

            ContentState state = new ContentState();
            state.setContent(newContent);
            state.setVisible(true);
            newContent.getStates().add(state);
        } else {
            if (dynamic) {
                result = createNodePending(ContentNode.class, site, uri, name, parent, user);
            } else {
                result = createNodeApproved(ContentNode.class, site, uri, name, parent, user);
            }
            ContentNode node = result.getNode();
            if (node.getContentType() != null && node.getContentType() != type) {
                throw new IllegalArgumentException("You can't change the content type of an existing " +
                    "content node.");
            }

            // Set the type
            node.setContentType(type);

            // If the node wasn't created or the node was approved because it isn't dynamic, still
            // add an update action to it for the content update
            NodeAction action;
            if (!result.isCreated() || !dynamic) {
                action = makeAction(ActionType.UPDATE, NodeActionStateType.PENDING, node, user);
                action.setDescription(makeDescription(ActionType.UPDATE, ContentNode.class, site, uri, name));
                node.getActions().add(action);
            } else {
                action = node.getActions().get(node.getActions().size() - 1);
            }

            // Fetch or create the locale holder
            LocaleContent localeContent;
            if (node.getLocaleContent().get(locale) != null) {
                 localeContent = node.getLocaleContent().get(locale);
            } else {
                localeContent = new LocaleContent();
                localeContent.setContentNode(node);
                localeContent.setLocale(locale);
                persistenceService.persist(localeContent);
                node.getLocaleContent().put(locale, localeContent);
            }

            // If this user has another pending change to the same node, remove that one and add this
            // one. This occurs when the user makes many edits to a node before it is approved
            List<NodeAction> actions = node.getActions();
            for (NodeAction nodeAction : actions) {
                if (nodeAction.getCurrentState() == NodeActionStateType.PENDING &&
                        nodeAction.getUserId() == user.getId() && nodeAction.getContent() != null &&
                        nodeAction.getContent().getContentNode().getId() == node.getId() &&
                        nodeAction.getContent().getLocale().equals(locale)) {
                    archiveAction(nodeAction, user, null);
                }
            }

            Content newContent = new Content();
            newContent.setContent(content);
            newContent.setContentNode(node);
            newContent.setLocale(locale);
            newContent.setLocaleContent(localeContent);
            newContent.setNodeAction(action);
            localeContent.getContents().add(newContent);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public DeleteResult<SiteNode> deleteSite(String site, Identifiable user) {
        DeleteResult<SiteNode> result;
        if (canPublish(user)) {
            result = deleteNodeApproved(SiteNode.class, site, null, null, user);
        } else {
            result = deleteNodePending(SiteNode.class, site, null, null, user);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public DeleteResult<PageNode> deletePage(String site, String uri, Identifiable user) {
        SiteNode siteNode = findSite(site);
        if (siteNode == null) {
            return new DeleteResult<PageNode>(null, false, false);
        }

        DeleteResult<PageNode> result;
        if (canPublish(user)) {
            result = deleteNodeApproved(PageNode.class, site, uri, null, user);
        } else {
            result = deleteNodePending(PageNode.class, site, uri, null, user);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public DeleteResult<ContentNode> deleteContent(String site, String uri, String name, Identifiable user) {
        PageNode pageNode = findPage(site, uri);
        if (pageNode == null) {
            return new DeleteResult<ContentNode>(null, false, false);
        }

        DeleteResult<ContentNode> result;
        if (canPublish(user)) {
            result = deleteNodeApproved(ContentNode.class, site, uri, name, user);
        } else {
            result = deleteNodePending(ContentNode.class, site, uri, name, user);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public ApproveResult approve(NodeAction action, Identifiable user, String comment) {
        Node node = action.getNode();
        if (action.getCurrentState() != NodeActionStateType.PENDING) {
            return new ApproveResult(node, false);
        }

        // Archive the old approved action
        List<NodeAction> actions = node.getActions();
        for (NodeAction nodeAction : actions) {
            if (nodeAction.getCurrentState() == NodeActionStateType.APPROVED) {
                archiveAction(nodeAction, user, comment);
            }
        }

        action.setCurrentState(NodeActionStateType.APPROVED);

        NodeActionState actionState = new NodeActionState();
        actionState.setNodeAction(action);
        actionState.setState(NodeActionStateType.APPROVED);
        actionState.setUserId(user.getId());
        actionState.setComment(comment);
        action.getNodeActionStates().add(actionState);

        if (action.getType() == ActionType.CREATE || action.getType() == ActionType.UPDATE) {
            node.setVisible(true);

            NodeState state = new NodeState();
            state.setNode(node);
            state.setVisible(true);
            node.getStates().add(state);

            if (node instanceof ContentNode) {
                ContentNode contentNode = (ContentNode) node;

                // Get the content that is associated with this node action and approve it
                Content content = persistenceService.queryFirst(Content.class,
                    "select c from Content c where c.nodeAction = ?1", action);
                if (content == null) {
                    throw new IllegalStateException("Can't find Content for action with an ID of [" +
                        action.getId() + "] that is tied to a ContentNode");
                }

                ContentState contentState = new ContentState();
                contentState.setVisible(true);
                contentState.setContent(content);
                content.getStates().add(contentState);

                contentNode.getCurrentContents().put(content.getLocale(), content);
            }
        } else if (action.getType() == ActionType.DELETE) {
            node.setVisible(false);

            NodeState state = new NodeState();
            state.setNode(node);
            state.setVisible(false);
            node.getStates().add(state);

            if (node instanceof ContentNode) {
                ContentNode contentNode = (ContentNode) node;

                // Delete all the current contents for the node
                contentNode.getCurrentContents().clear();

                // Get the content that is associated with this node action and approve it
                Content content = persistenceService.queryFirst(Content.class,
                    "select c from Content c where c.nodeAction = ?1", action);
                if (content == null) {
                    throw new IllegalStateException("Can't find Content for action with an ID of [" +
                        action.getId() + "] that is tied to a ContentNode");
                }

                ContentState contentState = new ContentState();
                contentState.setVisible(false);
                contentState.setContent(content);
                content.getStates().add(contentState);
            }
        }

        return new ApproveResult(node, true);
    }

    /**
     * {@inheritDoc}
     */
    public RejectResult reject(NodeAction action, Identifiable user, String comment) {
        Node node = action.getNode();
        if (action.getCurrentState() != NodeActionStateType.PENDING) {
            return new RejectResult(node, false);
        }

        action.setCurrentState(NodeActionStateType.REJECTED);

        NodeActionState actionState = new NodeActionState();
        actionState.setNodeAction(action);
        actionState.setState(NodeActionStateType.REJECTED);
        actionState.setUserId(user.getId());
        actionState.setComment(comment);
        action.getNodeActionStates().add(actionState);

        return new RejectResult(node, true);
    }

    /**
     * {@inheritDoc}
     */
    public DeclineResult decline(NodeAction action, Identifiable user, String comment) {
        Node node = action.getNode();
        if (action.getCurrentState() != NodeActionStateType.PENDING) {
            return new DeclineResult(node, false);
        }

        action.setCurrentState(NodeActionStateType.DECLINED);

        NodeActionState actionState = new NodeActionState();
        actionState.setNodeAction(action);
        actionState.setState(NodeActionStateType.DECLINED);
        actionState.setUserId(user.getId());
        actionState.setComment(comment);
        action.getNodeActionStates().add(actionState);

        return new DeclineResult(node, true);
    }

    private <T extends Node> T findNode(Class<T> type, String site, String uri, String name) {
        String uid = makeUID(site, uri, name);
        return persistenceService.queryFirst(type, "select n from " + type.getSimpleName() + " n where n.uid = ?1", uid);
    }

    private String makeUID(String site, String uri, String name) {
        return "http://" + site + (uri == null ? "" : uri) + (name == null ? "" : "<" + name + ">");
    }

    private String makeLocalName(String site, String uri, String name) {
        if (name != null) {
            return name;
        }

        if (uri != null) {
            return uri;
        }

        return site;
    }

    private boolean canPublish(Identifiable user) {
        Set<String> roles = userAdapater.getRoles(user);
        return roles.contains("admin") || roles.contains(ContentRole.publisher.toString());
    }

    private NodeAction makeAction(ActionType type, NodeActionStateType state, Node node, Identifiable user) {
        NodeAction action = new NodeAction();
        action.setCurrentState(state);
        action.setNode(node);
        action.setType(type);
        action.setUserId(user.getId());

        NodeActionState actionState = new NodeActionState();
        actionState.setNodeAction(action);
        actionState.setState(state);
        actionState.setUserId(user.getId());
        action.getNodeActionStates().add(actionState);

        return action;
    }

    private <T extends Node> CreateResult<T> createNodeApproved(Class<T> type, String site, String uri,
            String name, Node parent, Identifiable user) {
        CreateResult<T> result;
        T existing = findNode(type, site, uri, name);
        if (existing == null) {
            existing = ReflectionTools.instantiate(type);
            existing.setLocalName(makeLocalName(site, uri, name));
            existing.setUid(makeUID(site, uri, name));
            existing.setVisible(true);
            existing.setParent(parent);

            NodeAction nodeAction = makeAction(ActionType.CREATE, NodeActionStateType.APPROVED, existing, user);
            nodeAction.setDescription(makeDescription(ActionType.CREATE, type, site, uri, name));
            existing.getActions().add(nodeAction);

            NodeState state = new NodeState();
            state.setNode(existing);
            state.setVisible(true);
            existing.getStates().add(state);

            persistenceService.persist(existing);

            result = new CreateResult<T>(existing, true, false);
        } else if (!existing.isVisible()) {
            existing.setVisible(true);

            // If there is a pending create or update, approve it
            List<NodeAction> actions = existing.getActions();
            for (NodeAction action : actions) {
                if ((action.getType() == ActionType.CREATE || action.getType() == ActionType.UPDATE) &&
                        action.getCurrentState() == NodeActionStateType.PENDING) {
                    approve(action, user, null);
                    return new CreateResult<T>(existing, true, false);
                }
            }

            // If there isn't a pending create or update, make a new action
            NodeAction nodeAction = makeAction(ActionType.UPDATE, NodeActionStateType.APPROVED, existing, user);
            nodeAction.setDescription(makeDescription(ActionType.UPDATE, type, site, uri, name));
            existing.getActions().add(nodeAction);

            // Make a state
            NodeState state = new NodeState();
            state.setNode(existing);
            state.setVisible(true);
            existing.getStates().add(state);

            persistenceService.persist(existing);

            result = new CreateResult<T>(existing, true, false);
        } else {
            result = new CreateResult<T>(existing, false, false);
        }

        return result;
    }

    private <T extends Node> CreateResult<T> createNodePending(Class<T> type, String site, String uri,
            String name, Node parent, Identifiable user) {
        CreateResult<T> result;
        T existing = findNode(type, site, uri, name);
        if (existing == null) {
            existing = ReflectionTools.instantiate(type);
            existing.setLocalName(site);
            existing.setUid(site);
            existing.setVisible(false);
            existing.setParent(parent);

            NodeAction nodeAction = makeAction(ActionType.CREATE, NodeActionStateType.PENDING, existing, user);
            nodeAction.setDescription(makeDescription(ActionType.CREATE, type, site, uri, name));
            existing.getActions().add(nodeAction);

            persistenceService.persist(existing);

            result = new CreateResult<T>(existing, true, true);
        } else if (!existing.isVisible()) {
            existing.setVisible(true);

            // If there is a pending create or update, bail
            List<NodeAction> actions = existing.getActions();
            for (NodeAction action : actions) {
                if ((action.getType() == ActionType.CREATE || action.getType() == ActionType.UPDATE) &&
                        action.getCurrentState() == NodeActionStateType.PENDING) {
                    return new CreateResult<T>(existing, false, true);
                }
            }

            // If there isn't a pending create or update, make a new action
            NodeAction nodeAction = makeAction(ActionType.UPDATE, NodeActionStateType.PENDING, existing, user);
            nodeAction.setDescription(makeDescription(ActionType.UPDATE, type, site, uri, name));
            existing.getActions().add(nodeAction);

            persistenceService.persist(existing);

            result = new CreateResult<T>(existing, true, true);
        } else {
            result = new CreateResult<T>(existing, false, false);
        }

        return result;
    }

    private <T extends Node> DeleteResult<T> deleteNodeApproved(Class<T> type, String site, String uri,
            String name, Identifiable user) {
        DeleteResult<T> result;
        T existing = findNode(type, site, uri, name);
        if (existing == null) {
            result = new DeleteResult<T>(null, false, false);
        } else if (!existing.isVisible()) {
            result = new DeleteResult<T>(existing, true, false);
        } else {
            existing.setVisible(false);

            NodeAction action = makeAction(ActionType.DELETE, NodeActionStateType.APPROVED, existing, user);
            existing.getActions().add(action);

            // Make a state
            NodeState state = new NodeState();
            state.setNode(existing);
            state.setVisible(false);
            existing.getStates().add(state);

            persistenceService.persist(existing);

            result = new DeleteResult<T>(existing, true, false);
        }

        return result;
    }

    private <T extends Node> DeleteResult<T> deleteNodePending(Class<T> type, String site, String uri,
            String name, Identifiable user) {
        DeleteResult<T> result;
        T existing = findNode(type, site, uri, name);
        if (existing == null) {
            result = new DeleteResult<T>(null, false, false);
        } else if (!existing.isVisible()) {
            result = new DeleteResult<T>(existing, true, false);
        } else {
            NodeAction action = makeAction(ActionType.DELETE, NodeActionStateType.PENDING, existing, user);
            existing.getActions().add(action);

            persistenceService.persist(existing);

            result = new DeleteResult<T>(existing, true, false);
        }

        return result;
    }

    private String makeDescription(ActionType actionType, Class<? extends Node> type, String site,
            String uri, String name) {
        StringBuilder build = new StringBuilder();
        if (actionType == ActionType.CREATE) {
            build.append("Created");
        } else if (actionType == ActionType.UPDATE) {
            build.append("Updated");
        } else {
            build.append("Deleted");
        }

        if (type == SiteNode.class) {
            build.append(" site named [").append(site).append("]");
        } else if (type == PageNode.class) {
            build.append(" page named [").append(uri).append("] for site [").append(site).append("]");
        } else {
            build.append(" content named [").append(name).append("] on page [").append(uri).
                append("] for site [").append(site).append("]");
        }

        return build.toString();
    }

    private void archiveAction(NodeAction action, Identifiable user, String comment) {
        action.setCurrentState(NodeActionStateType.ARCHIVED);

        NodeActionState state = new NodeActionState();
        state.setNodeAction(action);
        state.setState(NodeActionStateType.ARCHIVED);
        state.setUserId(user.getId());
        state.setComment(comment);
        action.getNodeActionStates().add(state);
    }
}