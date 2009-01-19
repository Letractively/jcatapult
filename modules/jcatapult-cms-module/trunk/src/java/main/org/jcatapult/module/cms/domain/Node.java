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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import org.jcatapult.persistence.domain.AuditableImpl;

/**
 * <p>
 * This class is the domain object for a node within the CMS. Each
 * node is identified by a unique identifier (uid) that is unique
 * across the entire database.  This unique identifier is also the
 * hierarchical name. For example, if the node's local name
 * is <strong>bar</strong> and its parent's local name is
 * <strong>foo</strong>, and that node's parent is the root node
 * (i.e. the site node), then the UID of this node is
 * <strong>/foo/bar</strong>.
 * </p>
 *
 * <h2>Persistent</h2>
 * <p>
 * All Nodes are forever persistent. Once they are created, they
 * can <strong>NEVER</strong> be deleted. No matter how many
 * deletion and creation actions a node goes through, the
 * record in the database will never be deleted. It will only ever
 * be non-visible.
 * </p>
 *
 * <h2>Abstract</h2>
 * <p>
 * This class is abstract. This was a calculated decision because
 * it forces the domain out of a highly abstract and sometimes
 * slower model such as JCR and into a specific model targetted
 * directly towards web based CMS.
 * </p>
 *
 * <h2>Actions</h2>
 * <p>
 * All nodes have the ability to have actions performed on them.
 * The current set of actions that can be performed on a given node
 * are:
 * </p>
 *
 * <ul>
 * <li>Create</li>
 * <li>Update</li>
 * <li>Delete</li>
 * </ul>
 *
 * <p>
 * These are the basic manipulation actions that can be performed
 * on a node. I would doubt if too many more actions ever appear.
 * </p>
 * <p>
 * Actions are linear and do not model any type of branching or
 * forking at all. When a user takes an action it is immediately
 * appended to the list of actions for the node.
 * </p>
 *
 * <h3>Action states</h3>
 * <p>
 * An single action can go through a life-cycle (AKA workflow). This
 * allows a user to take an action, but that action not to take
 * immediate effect from the users perspective. An example would be
 * that someone might create a page, but that page might not become
 * visible to a user until it has been approved.
 * </p>
 * <p>
 * This workflow is <strong>mandatory</strong>. This means that you
 * can't manipulate the database directly or persist CMS entities
 * without using the workflow because most of the logic in the CMS
 * depends on the states and actions of the nodes.
 * </p>
 *
 * <h3>States<h3>
 * <p>
 * When actions are approved and the changes are made visible to
 * the user of the website, nodes sometimes change their states.
 * For example, if someone deletes a page and that delete is approved
 * the page is no longer visible to users. This means that actions
 * sometimes change the states of the nodes.
 * </p>
 * <p>
 * However, states have two different handlings, the current state
 * and the list of historical states. This is the only way to allow
 * someone to view the website at some time in the past because you
 * need to have the ability to ask the questions, 'was this node
 * visible on May 20th, 2003?'
 * </p>
 *
 * @author Brian Pontarelli
 */
@Entity
@Table(name = "cms_nodes")
@DiscriminatorColumn(name = "type")
public abstract class Node extends AuditableImpl {
    @Column(nullable = false, unique = true)
    String uid;

    @Column(nullable = false)
    String localName;

    @OneToMany(mappedBy = "node", cascade = {CascadeType.ALL})
    List<NodeAction> actions = new ArrayList<NodeAction>();

    @OneToMany(mappedBy = "node", cascade = {CascadeType.ALL})
    List<NodeState> states = new ArrayList<NodeState>();

    @Column()
    boolean visible;

    /**
     * @return  The unique identifier for the node. This is unique across a single CMS database.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the unique identifier for the node. This is unique across a single CMS database.
     *
     * @param   uid The unique identifier
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return  The local name of the node. This is how the node fits into the hierarchy.
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Sets the local name of the node. This is how the node fits into the hierarchy.
     *
     * @param   localName The local name of the node.
     */
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    /**
     * @return  The parent of this node.
     */
    public abstract Node getParent();

    /**
     * Sets the parent of this node.
     *
     * @param   parent The parent node of this node.
     */
    public abstract void setParent(Node parent);


    /**
     * @return  The list of all the actions ever taken on this node. This list also contains all of
     *          the various states each action went through (i.e. pending, approved, archived, etc).
     */
    public List<NodeAction> getActions() {
        return actions;
    }

    /**
     * Sets the list of all the actions ever taken on this node. This list also contains all of
     * the various states each action went through (i.e. pending, approved, archived, etc).
     *
     * @param   actions The actions.
     */
    public void setActions(List<NodeAction> actions) {
        this.actions = actions;
    }

    /**
     * @return  The list of historical states that the node had. This list must be comprehensive and
     *          perfect for historical viewing of a website to work properly.
     */
    public List<NodeState> getStates() {
        return states;
    }

    /**
     * Sets the list of historical states that the node had. This list must be comprehensive and
     * perfect for historical viewing of a website to work properly.
     *
     * @param   states The states.
     */
    public void setStates(List<NodeState> states) {
        this.states = states;
    }

    /**
     * @return  If the node is currently visible at the exact moment the node was fetched from the
     *          database. It is possible that the node was immediately deleted after it was fetched
     *          and this will be true, but the node is in fact not-visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets if the node is currently visible at the exact moment the node was fetched from the
     * database. It is possible that the node was immediately deleted after it was fetched and this
     * will be true, but the node is in fact not-visible.
     *
     * @param   visible The visible flag.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @PreRemove
    public void errorOnDelete() {
        throw new IllegalArgumentException("You cannot delete a Node instance. This class can only " +
            "be inserted or updated.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        return uid.equals(node.uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    /**
     * Prints out the UID.
     *
     * @return The UID.
     */
    public String toString() {
        return uid;
    }
}