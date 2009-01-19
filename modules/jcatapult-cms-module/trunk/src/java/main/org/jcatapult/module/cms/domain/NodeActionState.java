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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.persistence.domain.AuditableImpl;

/**
 * <p>
 * This class models the various states an action within the CMS goes through.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Entity
@Table(name = "cms_node_action_states")
public class NodeActionState extends AuditableImpl {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NodeActionStateType state;

    @Column
    private String comment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cms_node_actions_id")
    private NodeAction nodeAction;

    @ManyToOne(optional = false)
    @JoinColumn(name = "users_id")
    private DefaultUser user;

    public NodeActionStateType getState() {
        return state;
    }

    public void setState(NodeActionStateType state) {
        this.state = state;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public NodeAction getNodeAction() {
        return nodeAction;
    }

    public void setNodeAction(NodeAction nodeAction) {
        this.nodeAction = nodeAction;
    }

    public DefaultUser getUser() {
        return user;
    }

    public void setUser(DefaultUser user) {
        this.user = user;
    }

    @PreUpdate
    @PreRemove
    public void errorOnUpdateOrDelete() {
        throw new IllegalArgumentException("You cannot update or delete a NodeActionState instance. " +
            "This class can only be inserted.");
    }
}