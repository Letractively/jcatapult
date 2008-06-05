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
package org.jcatapult.mvc.parameters.el;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.jcatapult.mvc.parameters.convert.ConverterRegistry;

import static net.java.lang.reflect.ReflectionTools.*;

/**
 * <p>
 * This
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MemberAccessor extends Accessor {
    private static final Map<String, MethodVerifier> verifiers = new HashMap<String, MethodVerifier>();
    private static Map<Class, Map<String, PropertyInfo>> cache = new WeakHashMap<Class, Map<String, PropertyInfo>>();
    private static final Method ERROR;

    static {
        try {
            ERROR = Object.class.getMethod("hashCode");
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Bad, bad!");
        }

        verifiers.put("is", new GetMethodVerifier());
        verifiers.put("get", new GetMethodVerifier());
        verifiers.put("set", new SetMethodVerifier());
    }

    /**
     * Pulls all of the fields and java bean properties from the given klass and returns the names.
     *
     * @param   klass The klass to pull the names from.
     * @return  The names of all the fields and java bean properties.
     */
    public static Set<String> getAllMembers(Class<?> klass) {
        Field[] fields = klass.getFields();
        Map<String, PropertyInfo> map = getPropMap(klass);

        // Favor properties
        Set<String> names = new HashSet<String>();
        for (Field field : fields) {
            names.add(field.getName());
        }

        names.addAll(map.keySet());
        return names;
    }

    final Field field;
    final PropertyInfo propertyInfo;

    public MemberAccessor(ConverterRegistry converterRegistry, MemberAccessor accessor) {
        super(converterRegistry, accessor);
        this.field = accessor.field;
        this.propertyInfo = accessor.propertyInfo;
    }

    public MemberAccessor(ConverterRegistry converterRegistry, Class<?> declaringClass, String name) {
        super(converterRegistry);
        Map<String, PropertyInfo> map = getPropMap(declaringClass);
        PropertyInfo bpi = map.get(name);
        if (bpi == null || bpi.getMethods().get("get") == null) {
            this.propertyInfo = null;
            this.field = findField(declaringClass, name);
        } else {
            this.field = null;
            this.propertyInfo = bpi;
        }

        if (this.field == null && this.propertyInfo == null) {
            throw new ExpressionException("Invalid property or field [" + name + "] for class [" + declaringClass + "]");
        }

        this.declaringClass = declaringClass;
        super.type = (bpi != null) ? bpi.getGenericType() : field.getGenericType();
    }

    public boolean isIndexed() {
        return propertyInfo != null && propertyInfo.isIndexed();
    }

    public Object get(Context context) {
        if (propertyInfo != null) {
            Method getter = propertyInfo.getMethods().get("get");
            if (getter == null) {
                throw new ExpressionException("Missing getter for property [" + propertyInfo.getName() +
                    "] in class [" + declaringClass + "]");
            }
            return invokeMethod(getter, this.object);
        }

        return getField(field, this.object);
    }

    public void set(String[] values, Context context) {
        set(convert(values, context), context);
    }

    public void set(Object value, Context context) {
        if (propertyInfo != null) {
            Method setter = propertyInfo.getMethods().get("set");
            if (setter == null) {
                throw new ExpressionException("Missing setter for property [" + propertyInfo.getName() +
                    "] in class [" + declaringClass + "]");
            }
            invokeMethod(setter, object, value);
        } else {
            setField(field, object, value);
        }
    }

    /**
     * @return  Returns this.
     */
    public MemberAccessor getMemberAccessor() {
        return this;
    }

    public String toString() {
        return (propertyInfo != null) ? propertyInfo.toString() : "Field [" + field.toString() + "] in class [" +
            field.getDeclaringClass() + "]";
    }

    /**
     * Loads or fetches from the cache a Map of {@link PropertyInfo} objects keyed into the Map
     * by the property name they correspond to.
     *
     * @param   beanClass The bean class to grab the property map from.
     * @return  The Map or null if there were no properties.
     */
    private static Map<String, PropertyInfo> getPropMap(Class<?> beanClass) {
        Map<String, PropertyInfo> propMap;
        synchronized (cache) {
            // Otherwise look for the property Map or create and store
            propMap = cache.get(beanClass);
            if (propMap == null) {
                propMap = new HashMap<String, PropertyInfo>();
            } else {
                return propMap;
            }

            boolean errorMethods = false;
            Method[] methods = beanClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                PropertyName name = getPropertyNames(method);
                if (name == null) {
                    continue;
                }

                PropertyInfo info = propMap.get(name.getName());
                boolean constructed = false;
                if (info == null) {
                    info = new PropertyInfo();
                    info.setName(name.getName());
                    info.setKlass(beanClass);
                    constructed = true;
                }

                // Unify get and is
                String prefix = name.getPrefix();
                if (prefix.equals("is")) {
                    prefix = "get";
                }

                Method existingMethod = info.getMethods().get(prefix);
                if (existingMethod != null) {
                    info.getMethods().put(name.getPrefix(), ERROR);
                    errorMethods = true;
                    continue;
                }

                MethodVerifier verifier = verifiers.get(name.getPrefix());
                if (verifier != null) {
                    String error = verifier.isValid(method, info);
                    if (error != null) {
                        continue;
                    }
                } else {
                    continue;
                }

                info.getMethods().put(name.getPrefix(), method);
                info.setType(verifier.determineType(method));
                info.setGenericType(verifier.determineGenericType(method));
                info.setIndexed(verifier.isIndexed(method));

                if (constructed) {
                    propMap.put(name.getName(), info);
                }
            }

            if (errorMethods) {
                Set keys = propMap.keySet();
                for (Iterator i = keys.iterator(); i.hasNext();) {
                    String s = (String) i.next();
                    PropertyInfo info = propMap.get(s);
                    Set entries = info.getMethods().entrySet();
                    for (Iterator i2 = entries.iterator(); i2.hasNext();) {
                        Map.Entry entry = (Map.Entry) i2.next();
                        if (entry.getValue() == ERROR) {
                            i2.remove();
                        }
                    }

                    if (info.getMethods().size() == 0) {
                        i.remove();
                    }
                }
            }

            cache.put(beanClass, Collections.unmodifiableMap(propMap));
        }

        return propMap;
    }

    /**
     * <p>
     * Using the given Method, it returns the name of the java bean property and the prefix of the
     * method.
     * </p>
     *
     * <h3>Examples:</h3>
     * <pre>
     * getFoo -> get, foo
     * getX -> get, x
     * getURL -> get, URL
     * handleBar -> handle, bar
     * </pre>
     *
     * @param   method The method to translate.
     * @return  The property names or null if this was not a valid property Method.
     */
    private static PropertyName getPropertyNames(Method method) {
        String name = method.getName();
        char[] ca = name.toCharArray();
        int startIndex = -1;
        for (int i = 0; i < ca.length; i++) {
            char c = ca[i];
            if (Character.isUpperCase(c) && i == 0) {
                break;
            } else if (Character.isUpperCase(c)) {
                startIndex = i;
                break;
            }
        }

        if (startIndex == -1) {
            return null;
        }

        String propertyName = Introspector.decapitalize(name.substring(startIndex));
        String prefix = name.substring(0, startIndex);
        return new PropertyName(prefix, propertyName);
    }
}