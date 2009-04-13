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
package org.jcatapult.module.cms.result;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.jcatapult.module.cms.domain.CMSMode;
import org.jcatapult.module.cms.domain.Content;
import org.jcatapult.module.cms.domain.ContentNode;
import org.jcatapult.module.cms.service.ContentService;
import org.jcatapult.mvc.result.control.AbstractComponentControl;
import org.jcatapult.mvc.result.control.Body;
import org.jcatapult.mvc.result.control.annotation.ControlAttribute;
import org.jcatapult.mvc.result.control.annotation.ControlAttributes;

import com.google.inject.Inject;

/**
 * <p>
 * This class renders the content node both for the consumer and for
 * the admin.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ControlAttributes(
    required = {@ControlAttribute(name = "name")},
    optional = {@ControlAttribute(name = "global", types = {boolean.class, Boolean.class})}
)
public class CMSContentNode extends AbstractComponentControl {
    private final ContentService contentService;
    private String name;
    private CMSMode mode;

    @Inject
    public CMSContentNode(ContentService contentService) {
        this.contentService = contentService;
    }

    @Override
    public void renderStart(Writer writer, Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
        HttpSession session = request.getSession(false);
        mode = CMSMode.DISPLAY;
        if (session != null && session.getAttribute("cmsMode") != null) {
            mode = (CMSMode) session.getAttribute("cmsMode");
        }

        super.renderStart(writer, attributes, dynamicAttributes);
    }

    protected String startTemplateName() {
        return null;
    }

    protected String endTemplateName() {
        return "cms-content-node.ftl";
    }

    @Override
    protected void addAdditionalAttributes() {
        String classAttribute = (String) attributes.get("class");
        classAttribute = (classAttribute == null) ? "cms-node" : classAttribute + " cms-node";
        if (mode == CMSMode.EDIT) {
            classAttribute = classAttribute + " cms-node-editable";
        }
        attributes.put("class", classAttribute);

        this.name = (String) attributes.remove("name");
        if (!attributes.containsKey("id")) {
            attributes.put("id", name);
        }

        super.addAdditionalAttributes();
    }

    @Override
    protected Map<String, Object> makeParameters() {
        Map<String, Object> parameters = super.makeParameters();
        parameters.put("inEditMode", mode == CMSMode.EDIT);
        parameters.put("name", name);
        return parameters;
    }

    @Override
    public void renderBody(Writer writer, Body body) {
        String site = request.getServerName();
        ContentNode node;
        if (attributes.containsKey("global") && (Boolean) attributes.get("global")) {
            node = contentService.findGlobalContent(site, name);
        } else {
            String uri = request.getRequestURI();
            node = contentService.findPageContent(site, uri, name);
        }

        Content content;
        if (node == null || !node.isVisible()) {
            // Get the body contents if there is no content node
            StringWriter strWriter = new StringWriter();
            if (body != null) {
                body.render(strWriter);
            }

            content = new Content();
            content.setContent(strWriter.toString());
        } else {
            content = node.getCurrentContents().get(locale);
            if (content == null) {
                content = node.getCurrentContents().get(Locale.US);
            }
        }

        // Add the content and remake the root
        parameters.put("content", content);
        this.root = makeRoot();
    }
}