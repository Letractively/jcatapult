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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class MemberAccessor extends Accessor {
    private final Field field;
    private final BeanPropertyInfo bpi;

    public MemberAccessor(MemberAccessor accessor) {
        super(accessor);
        this.field = accessor.field;
        this.bpi = accessor.bpi;
    }

    public MemberAccessor(Class<?> declaringClass, String name) {
        Map<String, BeanPropertyInfo> map = getPropMap(declaringClass);
        BeanPropertyInfo bpi = map.get(name);
        if (bpi == null || bpi.methods.get("get") == null) {
            this.bpi = null;
            this.field = findField(declaringClass, name);
        } else {
            this.field = null;
            this.bpi = bpi;
        }

        if (this.field == null && this.bpi == null) {
            throw new ExpressionException("Invalid property or field [" + name + "] for class [" + declaringClass + "]");
        }

        this.declaringClass = declaringClass;
        super.type = (bpi != null) ? bpi.genericType : field.getGenericType();
    }

    public boolean isIndexed() {
        return bpi != null && bpi.indexed;
    }

    public Object get(Object object) {
        if (bpi != null) {
            Method getter = bpi.methods.get("get");
            if (getter == null) {
                throw new ExpressionException("Missing getter for property [" + bpi.propertyName +
                    "] in class [" + declaringClass + "]");
            }
            return invokeMethod(getter, object);
        }

        return getField(field, object);
    }

    public void set(Object object, Object value) {
        if (bpi != null) {
            Method setter = bpi.methods.get("set");
            if (setter == null) {
                throw new ExpressionException("Missing setter for property [" + bpi.propertyName +
                    "] in class [" + declaringClass + "]");
            }
            invokeMethod(setter, object, convert(value));
        } else {
            setField(field, object, convert(value));
        }
    }

    public String toString() {
        return (bpi != null) ? bpi.toString() : "Field [" + field.toString() + "] in class [" +
            field.getDeclaringClass() + "]";
    }
}