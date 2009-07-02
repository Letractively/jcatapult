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
package org.jcatapult.module.cms.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.jcatapult.persistence.domain.AuditableImpl;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
@Entity
@Table(name = "cms_node_actions")
public class NodeAction extends AuditableImpl {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType type;

    @Column(length = 4000)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cms_nodes_id")
    private Node node;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "nodeAction")
    private Content content;

    @OneToMany(mappedBy = "nodeAction", cascade = {CascadeType.ALL})
    private List<NodeActionState> nodeActionStates = new ArrayList<NodeActionState>();

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false)
    private NodeActionStateType currentState;

    @Column(name = "user_id", nullable = false)
    private int userId;

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public List<NodeActionState> getNodeActionStates() {
        return nodeActionStates;
    }

    public void setNodeActionStates(List<NodeActionState> nodeActionStates) {
        this.nodeActionStates = nodeActionStates;
    }

    public NodeActionStateType getCurrentState() {
        return currentState;
    }

    public void setCurrentState(NodeActionStateType currentState) {
        this.currentState = currentState;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @PreUpdate
    @PreRemove
    public void errorOnUpdateOrDelete() {
        throw new IllegalArgumentException("You cannot update or delete a NodeAction instance. This " +
            "class can only be inserted.");
    }
}