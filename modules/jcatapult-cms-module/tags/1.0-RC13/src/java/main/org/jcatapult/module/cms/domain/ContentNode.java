/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
 */
package org.jcatapult.module.cms.domain;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * <p>
 * This class is a Node that models a content node on a page.
 * This will always be associated with the {@link PageNode}
 * it belongs to, even if it is deleted.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@DiscriminatorValue("content")
public class ContentNode extends Node {
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = true)
    Node parent;

    @OneToMany(cascade = {CascadeType.ALL})
    @MapKey(name = "locale")
    @JoinTable(name = "cms_current_contents", inverseJoinColumns = {@JoinColumn(name = "cms_contents_id")})
    private Map<Locale, Content> currentContents = new HashMap<Locale, Content>();

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "contentNode")
    @MapKey(name = "locale")
    @OrderBy("id asc")
    private Map<Locale, LocaleContent> localeContent = new HashMap<Locale, LocaleContent>();

    /**
     * @return  The type of content stored in the node.
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Sets the type of content stored in the node.
     *
     * @param   contentType The type of content.
     */
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

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

    public Map<Locale, Content> getCurrentContents() {
        return currentContents;
    }

    public void setCurrentContents(Map<Locale, Content> currentContents) {
        this.currentContents = currentContents;
    }

    public Map<Locale, LocaleContent> getLocaleContent() {
        return localeContent;
    }

    public void setLocaleContent(Map<Locale, LocaleContent> localeContent) {
        this.localeContent = localeContent;
    }
}