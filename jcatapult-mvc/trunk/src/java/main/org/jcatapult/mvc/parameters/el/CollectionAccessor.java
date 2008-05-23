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

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.util.Map;
import java.util.Collection;

/**
 * <p>
 * This class models a collection accessor during expression evaluation.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CollectionAccessor extends Accessor {
    String index;

    public CollectionAccessor(Accessor accessor, String index) {
        super(accessor);
        this.index = index;
        super.type = componentType(super.type);
    }

    protected Type componentType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Map.class == rawType) {
                return parameterizedType.getActualTypeArguments()[1];
            } else if (Collection.class == rawType) {
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

    public Object get(Object object) {
        return getValueFromCollection(object, index);
    }

    public void set(Object object, Object value) {
        setValueIntoCollection(object, index, convert(value));
    }
}