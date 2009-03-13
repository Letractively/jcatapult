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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.locale.annotation.CurrentLocale;
import org.jcatapult.module.cms.service.ContentService;
import org.jcatapult.mvc.action.annotation.Action;

import com.google.inject.Inject;

/**
 * <p>
 * This class is an action that retrieves a content node using the
 * name give, the name of the site from the HttpServletRequest's
 * getServerName method and the page URI from the getRequestURI
 * method.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action
public class Fetch {
    private final ContentService contentService;
    private final HttpServletRequest request;
    public final List<NodeResult> results = new ArrayList<NodeResult>();

    public Locale locale;
    public List<NodeQuery> queries;

    @Inject
    public Fetch(ContentService contentService, HttpServletRequest request, @CurrentLocale Locale locale) {
        this.contentService = contentService;
        this.request = request;
        this.locale = locale;
    }

    public String get() {
        String site = request.getServerName();

        for (NodeQuery nodeQuery : queries) {
            NodeResult result = new NodeResult();
            if (nodeQuery.global) {
                result.node = contentService.findGlobalContent(site, nodeQuery.name);
            } else {
                result.node = contentService.findPageContent(site, nodeQuery.uri, nodeQuery.name);
            }

            if (result.node != null) {
                result.content = result.node.getCurrentContents().get(locale);
                if (result.content == null) {
                    result.content = result.node.getCurrentContents().get(Locale.US);
                }
            }

            results.add(result);
        }

        return "success";
    }
}