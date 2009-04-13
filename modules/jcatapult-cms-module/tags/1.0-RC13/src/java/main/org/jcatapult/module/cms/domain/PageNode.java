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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * <p>
 * This class is a Node that models a page on a website. This will
 * always be associated with the {@link SiteNode} that it belongs
 * to, even if the page is deleted.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@DiscriminatorValue("page")
public class PageNode extends Node {
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "parent")
    private Set<ContentNode> content = new HashSet<ContentNode>();

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "parent")
    private Set<PageNode> children = new HashSet<PageNode>();

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = true)
    Node parent;

    /**
     * @return  The parent of this node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets the parent of this node.
     *
     * @param   parent The parent node of this node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * @return  The content of this page.
     */
    public Set<ContentNode> getContent() {
        return content;
    }

    /**
     * Sets the the content of this page.
     *
     * @param   content The content.
     */
    public void setContent(Set<ContentNode> content) {
        this.content = content;
    }

    /**
     * @return  The child pages.
     */
    public Set<PageNode> getChildren() {
        return children;
    }

    /**
     * Sets the child pages.
     *
     * @param   children The child pages.
     */
    public void setChildren(Set<PageNode> children) {
        this.children = children;
    }
}