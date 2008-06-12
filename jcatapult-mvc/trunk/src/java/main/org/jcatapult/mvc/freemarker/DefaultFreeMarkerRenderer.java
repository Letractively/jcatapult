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
 */
package org.jcatapult.mvc.freemarker;

import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.jcatapult.container.FreeMarkerContainerTemplateLoader;

import com.google.inject.Singleton;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
@Singleton
public class DefaultFreeMarkerRenderer implements FreeMarkerRenderer {
    private final freemarker.template.Configuration configuration;

    public DefaultFreeMarkerRenderer(Configuration configuration) {
        // Setup FreeMarker
        String templatesLocation = configuration.getString("jcatapult.mvc.result.template.location", "/WEB-INF/result/template");
        if (templatesLocation == null) {
            templatesLocation = defaultLocation;
        }

        ClassTemplateLoader ctl = new ClassTemplateLoader(this.getClass(), templatesLocation);
        FreeMarkerContainerTemplateLoader watl = new FreeMarkerContainerTemplateLoader(containerResolver, templatesLocation);
        MultiTemplateLoader loader = new MultiTemplateLoader(new TemplateLoader[]{watl, ctl});
        freeMarkerConfiguration.setTemplateLoader(loader);

        boolean cache = configuration.getBoolean("jcatapult.email.templates.cache", false);
        if (!cache) {
            freeMarkerConfiguration.setTemplateUpdateDelay(Integer.MAX_VALUE);
        } else {
            int checkInterval = configuration.getInt("jcatapult.email.templates.check-interval", 2);
            freeMarkerConfiguration.setTemplateUpdateDelay(checkInterval);
        }
    }

    public void render(OutputStream stream, String template, Map<String, Object> parameters, Locale locale) {

    }
}