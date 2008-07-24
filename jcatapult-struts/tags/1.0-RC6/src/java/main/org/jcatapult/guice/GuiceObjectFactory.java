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
package org.jcatapult.guice;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.logging.Logger;

import com.google.inject.ScopeAnnotation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * <p>
 * This class is a Guice Object Factory for Struts 2. To use it
 * need to add the following configuration to your struts.xml or
 * struts.properties file:
 * </p>
 *
 * <pre>
 * struts.objectFactory=jcatapult
 * guice.modules=com.foo.Module1,com.bar.Module2
 * </pre>
 *
 * <p>
 * The <strong>struts.objectFactory</strong> tells Struts to use
 * this ObjectFactory to create all the beans. The <strong>
 * guice.modules</strong> is a comma separated list of modules
 * that are used to construct the Guice Injector.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings({"unchecked"})
public class GuiceObjectFactory extends ObjectFactory {
    static final Logger logger = Logger.getLogger(GuiceObjectFactory.class.getName());

    @Override
    public boolean isNoArgConstructorRequired() {
        return false;
    }

    /**
     * Adds the given class to the set of classes to be used within the Injector.
     *
     * @param   name The name of the class to add to the set to be bound in the injector.
     * @return  The Class object.
     * @throws  ClassNotFoundException If the class doesn't exist.
     */
    public Class getClassInstance(String name) throws ClassNotFoundException {
        Class<?> clazz = super.getClassInstance(name);
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Unable to get class instance for interface [" +
                clazz.getName() + "]");
        }

        return clazz;
    }

    /**
     * Constructs the injector and sets the needsReload flag to false atomically. This will prevent
     * double loads of the injector.
     *
     * @param   clazz The class to fetch from the injector.
     * @param   extraContext Not used.
     * @return  The bean if it exists.
     */
    public Object buildBean(Class clazz, Map extraContext) {
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Unable to build bean instance for interface [" +
                clazz.getName() + "]");
        }

        Object obj = GuiceContainer.getInjector().getInstance(clazz);
        super.injectInternalBeans(obj);
        return obj;
    }

    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map interceptorRefParams)
    throws ConfigurationException {
        // Ensure the interceptor class is present.
        Class<? extends Interceptor> interceptorClass;
        try {
            interceptorClass = getClassInstance(interceptorConfig.getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (interceptorClass.isInterface()) {
            throw new IllegalArgumentException("Unable to build interceptor for interface [" +
                interceptorClass.getName() + "]");
        }

        if (hasScope(interceptorClass)) {
            throw new RuntimeException("Scoping interceptors is not currently supported. Please " +
                "remove the scope annotation from [" + interceptorClass.getName() + "].");
        }

        Interceptor interceptor = (Interceptor) buildBean(interceptorClass, interceptorRefParams);
        super.injectInternalBeans(interceptor);
        return interceptor;
    }

    /**
     * @param   interceptorClass The interceptor class to check to scope annotations.
     * @return  True if the given class has a scope annotation.
     */
    private static boolean hasScope(Class<? extends Interceptor> interceptorClass) {
        for (Annotation annotation : interceptorClass.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(ScopeAnnotation.class)) {
                return true;
            }
        }
        return false;
    }
}