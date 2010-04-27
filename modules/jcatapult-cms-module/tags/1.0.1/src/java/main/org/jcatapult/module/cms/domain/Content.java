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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.jcatapult.persistence.domain.AuditableImpl;

/**
 * <p>
 * This is the domain class for storing the data of a ContentNode.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@Table(name = "cms_contents")
public class Content extends AuditableImpl {
    @Column(nullable = false)
    private Locale locale;

    @Lob @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, length = 8000000)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cms_nodes_id")
    private ContentNode contentNode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cms_locale_contents_id")
    private LocaleContent localeContent;

    @OneToOne(optional = false)
    @JoinColumn(name = "cms_node_actions_id")
    private NodeAction nodeAction;

    @OneToMany(mappedBy = "content", cascade = {CascadeType.ALL})
    private List<ContentState> states = new ArrayList<ContentState>();

    /**
     * @return  The locale of this content.
     */
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContentNode getContentNode() {
        return contentNode;
    }

    public void setContentNode(ContentNode contentNode) {
        this.contentNode = contentNode;
    }

    public LocaleContent getLocaleContent() {
        return localeContent;
    }

    public void setLocaleContent(LocaleContent localeContent) {
        this.localeContent = localeContent;
    }

    public NodeAction getNodeAction() {
        return nodeAction;
    }

    public void setNodeAction(NodeAction nodeAction) {
        this.nodeAction = nodeAction;
    }

    public List<ContentState> getStates() {
        return states;
    }

    public void setStates(List<ContentState> states) {
        this.states = states;
    }

    @PreUpdate
    @PreRemove
    public void errorOnUpdateOrDelete() {
        throw new IllegalArgumentException("You cannot update or delete a Content instance. This " +
            "class can only be inserted.");
    }
}