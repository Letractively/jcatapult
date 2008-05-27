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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.List;

import org.jcatapult.mvc.parameters.convert.ConverterRegistry;

import static net.java.lang.ObjectTools.*;

/**
 * <p>
 * This class models a collection accessor during expression evaluation.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CollectionAccessor extends Accessor {
    String index;
    MemberAccessor memberAccessor;

    public CollectionAccessor(ConverterRegistry converterRegistry, Accessor accessor, String index,
            MemberAccessor memberAccessor) {
        super(converterRegistry, accessor);
        this.index = index;
        super.type = componentType(super.type);
        this.memberAccessor = memberAccessor;
    }

    /**
     * @return  The memberAccessor member variable.
     */
    public MemberAccessor getMemberAccessor() {
        return memberAccessor;
    }

    protected Type componentType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Map.class.isAssignableFrom(rawType)) {
                return parameterizedType.getActualTypeArguments()[1];
            } else if (Collection.class.isAssignableFrom(rawType)) {
                return parameterizedType.getActualTypeArguments()[0];
            } else {
                throw new ExpressionException("Unknown collection type [" + type + "]");
            }
        } else if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }

        Class<?> rawType = (Class<?>) type;
        if (Map.class == type || Collection.class == type) {
            throw new ExpressionException("The method or member [" + toString() + "] returns a simple " +
                "Map or Collection. Unable to determine the type to create for Map or Collection. " +
                "Please make this method generic so that the correct type can be determined.");
        } else if (rawType.isArray()) {
            return rawType.getComponentType();
        }

        return rawType;
    }

    /**
     * @return  Always false. The reason is that since this retrieves from a Collection, we want
     *          it to look like a non-indexed property so that the context will invoke the method.
     */
    public boolean isIndexed() {
        return false;
    }

    public Object get(Context context) {
        try {
            return getValueFromCollection(this.object, index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void set(String[] values, Context context) {
        set(convert(values, context), context);
    }

    public void set(Object value, Context context) {
        object = pad(object, context);
        setValueIntoCollection(object, index, value);
    }

    /**
     * Adds padding to the array or list so that it can hold the item being inserted.
     *
     * @param   object The object to pad. If this isn't a List or an array, this method does nothing
     *          and just returns the Object.
     * @param   context The current context.
     * @return  The padded list or array.
     */
    private Object pad(Object object, Context context) {
        if (object instanceof List) {
            List list = ((List) object);
            int length = list.size();
            int indexInt = Integer.parseInt(index);
            if (length <= indexInt) {
                for (int i = length; i <= indexInt; i++) {
                    list.add(null);
                }
            }
        } else if (isArray(object)) {
            int length = Array.getLength(object);
            int indexInt = Integer.parseInt(index);
            if (length <= indexInt) {
                System.out.println("Padding " + length + " to " + indexInt);
                Object newArray = Array.newInstance(object.getClass().getComponentType(), indexInt + 1);
                System.arraycopy(object, 0, newArray, 0, length);
                object = newArray;

                // Set the new array into the member
                memberAccessor.update(newArray, context);
            }
        }

        return object;
    }
}