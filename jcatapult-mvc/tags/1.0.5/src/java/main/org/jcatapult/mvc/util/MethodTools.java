/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.mvc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * <p>
 * This class provides some helpers for common tasks regarding methods.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MethodTools {
    /**
     * Invokes all of the methods that have the given annotation on the given Object.
     *
     * @param   obj The object to invoke the methods on.
     * @param   annotation The annotation.
     */
    public static void invokeAllWithAnnotation(Object obj, Class<? extends Annotation> annotation) {
        Class<?> actionClass = obj.getClass();
        Method[] methods = actionClass.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(annotation) != null) {
                try {
                    method.invoke(obj);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to call method [" + method + "] with annotation [" +
                        annotation.getSimpleName() + "]", e);
                }
            }
        }
    }
}
