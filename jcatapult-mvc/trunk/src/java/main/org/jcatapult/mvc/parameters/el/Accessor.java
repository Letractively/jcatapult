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

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcatapult.mvc.parameters.convert.ConversionException;
import org.jcatapult.mvc.parameters.convert.Converter;
import org.jcatapult.mvc.parameters.convert.ConverterRegistry;

import static net.java.lang.reflect.ReflectionTools.*;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public abstract class Accessor {
    Type type;
    Class<?> declaringClass;

    protected Accessor() {
    }

    public Accessor(Accessor accessor) {
        this.type = accessor.type;
        this.declaringClass = accessor.declaringClass;
    }

    public abstract boolean isIndexed();

    public abstract Object get(Object object);

    public abstract void set(Object object, Object value);

    /**
     * Creates a new instance of the current type.
     *
     * @param   key This is only used when creating arrays. It is the next atom, which is always
     *          the size of the array.
     * @return  The new value.
     */
    protected Object createValue(Object key) {
        Class<?> typeClass = typeToClass(type);
        Object value;
        if (Map.class == typeClass) {
            value = new HashMap();
        } else if (List.class == typeClass) {
            value = new ArrayList();
        } else if (Set.class == typeClass) {
            value = new HashSet();
        } else if (Queue.class == typeClass) {
            value = new LinkedList();
        } else if (Deque.class == typeClass) {
            value = new ArrayDeque();
        } else if (SortedSet.class == typeClass) {
            value = new TreeSet();
        } else if (typeClass.isArray()) {
            if (key == null) {
                throw new ExpressionException("Attempting to create an array, but there isn't an index " +
                    "available to determine the size of the array");
            }

            value = Array.newInstance(typeClass.getComponentType(), Integer.parseInt(key.toString()) + 1);
        } else {
            value = instantiate(typeClass);
        }

        return value;
    }

    /**
     * Converts the type to a Class. If the type is parameterized, gets the raw type. Otherwise,
     * just casts.
     *
     * @param   type The type.
     * @return  The class.
     */
    protected Class<?> typeToClass(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }

        return (Class<?>) type;
    }

    /**
     * Converts the given value parameter (parameter) to a type that is accepted by the set method of
     * this property. This method attempts to convert the value regardless of the value being null.
     * However, this method short circuits and returns the value unchanged if value is runtime
     * assignable to the type of this BaseBeanProperty.
     *
     * @param   value The value object to convert.
     * @return  The value parameter converted to the correct type.
     * @throws org.jcatapult.mvc.parameters.convert.ConversionException If there was a problem converting the parameter.
     */
    protected Object convert(final Object value) throws ConversionException {
        Object newValue = value;

        // The converter does this, but pre-emptively checking these conditions will speed up conversion times
        Class<?> typeClass = typeToClass(type);
        if (value != null && !typeClass.isInstance(value)) {
            Converter converter = ConverterRegistry.lookup(typeClass);
            if (converter == null) {
                throw new ConversionException("No type converter found for the type [" + typeClass.getName() + "]");
            }

            newValue = converter.convert(value, typeClass, null, null);
        }

        return newValue;
    }

    public String toString() {
        return "declaring class [" + declaringClass + "]";
    }
}