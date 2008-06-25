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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcatapult.mvc.ObjectFactory;
import org.jcatapult.mvc.result.control.Control;

import freemarker.template.TemplateDirectiveModel;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ControlMap implements Map<String, TemplateDirectiveModel> {
    private final ObjectFactory objectFactory;
    private final Map<String, Class<? extends Control>> controls;

    public ControlMap(ObjectFactory objectFactory, Map<String, Class<? extends Control>> controls) {
        this.objectFactory = objectFactory;
        this.controls = controls;
    }

    public int size() {
        return controls.size();
    }

    public boolean isEmpty() {
        return controls.isEmpty();
    }

    public boolean containsKey(Object key) {
        return controls.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return containsValue(value);
    }

    public TemplateDirectiveModel get(Object key) {
        Class<? extends Control> type = controls.get(key);
        if (type != null) {
            return objectFactory.create(type);
        }

        return null;
    }

    public TemplateDirectiveModel put(String key, TemplateDirectiveModel value) {
        throw new UnsupportedOperationException("Putting not allowed into the jc directive map");
    }

    public TemplateDirectiveModel remove(Object key) {
        throw new UnsupportedOperationException("Removing not allowed into the jc directive map");
    }

    public void putAll(Map<? extends String, ? extends TemplateDirectiveModel> m) {
        throw new UnsupportedOperationException("Putting not allowed into the jc directive map");
    }

    public void clear() {
        throw new UnsupportedOperationException("Clearing not allowed into the jc directive map");
    }

    public Set<String> keySet() {
        return controls.keySet();
    }

    public Collection<TemplateDirectiveModel> values() {
        List<TemplateDirectiveModel> all = new ArrayList<TemplateDirectiveModel>();
        for (String name : controls.keySet()) {
            all.add(objectFactory.create(controls.get(name)));
        }

        return all;
    }

    public Set<Entry<String, TemplateDirectiveModel>> entrySet() {
        Map<String, TemplateDirectiveModel> all = new HashMap<String, TemplateDirectiveModel>();
        for (String name : controls.keySet()) {
            all.put(name, objectFactory.create(controls.get(name)));
        }

        return all.entrySet();
    }
}