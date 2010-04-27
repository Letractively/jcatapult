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
import javax.persistence.OneToMany;

/**
 * <p>
 * This class is a Node that models a website.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@DiscriminatorValue("site")
@Entity
public class SiteNode extends Node {
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "parent")
    private Set<PageNode> pages = new HashSet<PageNode>();

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "parent")
    private Set<ContentNode> globalContent = new HashSet<ContentNode>();

    /**
     * @return  The parent of this node, always null.
     */
    public Node getParent() {
        return null;
    }

    /**
     * Blows chunks.
     *
     * @param   parent The parent node of this node.
     */
    public void setParent(Node parent) {
        if (parent != null) {
            throw new UnsupportedOperationException("Can't set a parent for a site.");
        }
    }

    public Set<PageNode> getPages() {
        return pages;
    }

    public void setPages(Set<PageNode> pages) {
        this.pages = pages;
    }

    public Set<ContentNode> getGlobalContent() {
        return globalContent;
    }

    public void setGlobalContent(Set<ContentNode> globalContent) {
        this.globalContent = globalContent;
    }
}