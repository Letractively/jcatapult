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
package org.jcatapult.mvc.action.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcatapult.mvc.ObjectFactory;
import org.jcatapult.mvc.result.control.Control;

import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * <p>
 * This class is a hash that stores the {@link Control} classes so that they
 * can be used from the FreeMarker templates.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ControlHashModel implements TemplateHashModelEx {
    private final ObjectFactory objectFactory;
    private final Map<String, Class<? extends TemplateModel>> models;

    public ControlHashModel(ObjectFactory objectFactory, Map<String, Class<? extends TemplateModel>> models) {
        this.objectFactory = objectFactory;
        this.models = models;
    }

    public TemplateCollectionModel keys() throws TemplateModelException {
        return new SimpleCollection(keySet());
    }

    public int size() {
        return models.size();
    }

    public boolean isEmpty() {
        return models.isEmpty();
    }

    public TemplateModel get(String key) {
        Class<? extends TemplateModel> type = models.get(key);
        if (type != null) {
            return objectFactory.create(type);
        }

        return null;
    }

    public TemplateCollectionModel values() {
        return new SimpleCollection(valueCollection());
    }

    public Set<String> keySet() {
        return new HashSet<String>(models.keySet());
    }

    public Collection<?> valueCollection() {
        List<TemplateModel> all = new ArrayList<TemplateModel>();
        for (String name : models.keySet()) {
            all.add(objectFactory.create(models.get(name)));
        }

        return all;
    }
}