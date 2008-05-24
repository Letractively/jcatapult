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
package org.jcatapult.mvc.parameters.el;

import java.util.List;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class Context {
    private final List<Atom> atoms;

    private Class<?> type;
    private Object object;
    private Accessor accessor;
    private int index;

    public Context(List<Atom> atoms) {
        this.atoms = atoms;
    }

    public void init(Object object) {
        this.object = object;
        this.type = object.getClass();
    }

    public boolean skip() {
        return accessor != null && accessor.isIndexed();
    }

    public void initAccessor(String name) {
        // This is the indexed case, so the name is the index to the method
        if (accessor != null && accessor.isIndexed()) {
            accessor = new IndexedAccessor((MemberAccessor) accessor, name);
        } else if (ObjectTools.isCollection(object) || object.getClass().isArray()) {
            accessor = new CollectionAccessor(accessor, name);
        } else {
            accessor = new MemberAccessor(type, name);
        }
    }

    public Object getCurrentValue() {
        return accessor.get(object);
    }

    public void setCurrentValue(Object value) {
        accessor.set(object, value);
    }

    public Object createValue() {
        // Peek at the next atom, in case this is an array
        Object key = hasNext() ? peek().getName(): null;
        Object value = accessor.createValue(key);
        accessor.set(object, value);
        return value;
    }

    public Atom peek() {
        return atoms.get(index);
    }

    public Atom next() {
        return atoms.get(index++);
    }

    public boolean hasNext() {
        return index < atoms.size();
    }

    public Class<?> getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }

    public Accessor getAccessor() {
        return accessor;
    }

    public int getIndex() {
        return index;
    }
}