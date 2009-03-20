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
package org.jcatapult.module.cms.action.admin.cms.content;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.module.cms.domain.ContentNode;
import org.jcatapult.module.cms.domain.ContentType;
import org.jcatapult.module.cms.service.ContentService;
import org.jcatapult.module.cms.service.CreateResult;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.security.SecurityContext;

import com.google.inject.Inject;

/**
 * <p>
 * This class is an action that stores a new piece of content.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action
public class Store {
    private final ContentService contentService;
    private final HttpServletRequest request;
    public final MessageStore messageStore;

    public boolean dynamic;
    public boolean global;

    // Total hack for the cms meta tag editor
    public String title;
    public String keywords;

    public String uri;
    @Required public String name;
    @Required public Locale locale;
    @Required public String content;
    @Required public ContentType contentType;

    public CreateResult<ContentNode> result;

    @Inject
    public Store(ContentService contentService, HttpServletRequest request, MessageStore messageStore) {
        this.contentService = contentService;
        this.request = request;
        this.messageStore = messageStore;
    }

    public String post() {
        Identifiable user = (Identifiable) SecurityContext.getCurrentUser();
        String site = request.getServerName();
        String page = global ? null : uri;

        result = contentService.storeContent(site, page, name, locale, content, contentType, dynamic, user);
        return "success";
    }
}
