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
import java.util.Locale;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.jcatapult.persistence.domain.AuditableImpl;

/**
 * <p>
 * This class stores the content history for a specific locale.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Entity
@Table(name = "cms_locale_contents")
public class LocaleContent extends AuditableImpl {
    @Column(nullable = false)
    private Locale locale;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cms_nodes_id")
    private ContentNode contentNode;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "localeContent")
    private List<Content> contents = new ArrayList<Content>();

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public ContentNode getContentNode() {
        return contentNode;
    }

    public void setContentNode(ContentNode contentNode) {
        this.contentNode = contentNode;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    @PreUpdate
    @PreRemove
    public void errorOnUpdateOrDelete() {
        throw new IllegalArgumentException("You cannot update or delete a LocaleContent instance. " +
            "This class can only be inserted.");
    }
}