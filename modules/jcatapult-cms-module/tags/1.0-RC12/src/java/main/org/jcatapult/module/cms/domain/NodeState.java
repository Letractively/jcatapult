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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.jcatapult.persistence.domain.AuditableImpl;

/**
 * <p>
 * This stores a single state for a node, which might be current or
 * historical.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@Table(name = "cms_node_states")
public class NodeState extends AuditableImpl {
    @Column
    private boolean visible;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cms_nodes_id")
    private Node node;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    @PreUpdate
    @PreRemove
    public void errorOnUpdateOrDelete() {
        throw new IllegalArgumentException("You cannot update or delete a NodeState instance. This " +
            "class can only be inserted.");
    }
}