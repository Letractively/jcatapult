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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.ScopeAnnotation;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.inject.Inject;
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
public class GuiceObjectFactory extends ObjectFactory {
    static final Logger logger = Logger.getLogger(GuiceObjectFactory.class.getName());

    List<Module> modules;
    volatile Injector injector;
    List<ProvidedInterceptor> interceptors = new ArrayList<ProvidedInterceptor>();
    Set<Class<?>> boundClasses = new HashSet<Class<?>>();

    @Override
    public boolean isNoArgConstructorRequired() {
        return false;
    }

    @Inject(value = "guice.modules", required = true)
    public void setModuleClassNames(String moduleClassNames) {
        String[] moduleClassNamesArray = moduleClassNames.split(",");
        modules = new ArrayList<Module>(moduleClassNamesArray.length);

        for (String moduleClassName : moduleClassNamesArray) {
            moduleClassName = moduleClassName.trim();
            try {
                // Instantiate user's module.
                Class<?> moduleClass = Class.forName(moduleClassName);
                if (!Module.class.isAssignableFrom(moduleClass)) {
                    throw new RuntimeException("Invalid Guice module class [" + moduleClassName + "]. The class must " +
                        "implement [com.google.inject.Module].");
                }

                logger.info("Configuring guice module [" + moduleClass.getName() + "]");
                modules.add((Module) moduleClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
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
        synchronized(this) {
            // We can only bind each class once.
            if (!boundClasses.contains(clazz)) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Adding [" + clazz.getName() + "] to bindings.");
                }

                try {
                    // Calling these methods now helps us detect ClassNotFoundErrors
                    // early.
                    clazz.getDeclaredFields();
                    clazz.getDeclaredMethods();

                    boundClasses.add(clazz);
                } catch (Throwable t) {
                    // Struts should still work even though some classes aren't in the
                    // classpath. It appears we always get the exception here when
                    // this is the case.
                    return clazz;
                }

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Setting injector to [null]");
                }

                injector = null;
            }
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
        // If a reload is need, do it.
        Injector local = injector;
        if (local == null) {
            synchronized(this) {
                if (injector == null) {
                    logger.fine("Injector needs reload");
                    if (!boundClasses.contains(clazz)) {
                        logger.info("Requested bean class [" + clazz.getName() + "] not in bindings. Adding.");
                        boundClasses.add(clazz);
                    }

                    local = createInjector();
                } else {
                    local = injector;
                }
            }
        }

        // See if it is bound and if not, recreate the injector adding it to the list to be bound
        List<?> bindings = local.findBindingsByType(TypeLiteral.get(clazz));
        if (bindings == null || bindings.size() == 0) {
            synchronized(this) {
                logger.info("Injector exists, but has no binding for class [" + clazz.getName() +
                    "]. Adding binding and re-creating injector.");
                boundClasses.add(clazz);
                local = createInjector();
            }
        }

        return local.getInstance(clazz);
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

        ProvidedInterceptor providedInterceptor = new ProvidedInterceptor(interceptorConfig, interceptorRefParams,
            interceptorClass);
        synchronized(this) {
            interceptors.add(providedInterceptor);
            injector = null;
        }

        return providedInterceptor;
    }

    private Injector createInjector() {
        try {
            logger.info("Creating injector...");
            this.injector = Guice.createInjector(new AbstractModule() {
                protected void configure() {
                    // Install default servlet bindings.
                    install(new ServletModule());

                    // Install user's modules
                    for (Module module : modules) {
                        logger.info("Installing module [" + module.getClass().getName() + "]");
                        install(module);
                    }

                    // Tell the injector about all the action classes, etc., so it
                    // can validate them at startup.
                    for (Class<?> boundClass : boundClasses) {
                        // TODO: Set source from Struts XML.
                        bind(boundClass);
                    }

                    // Validate the interceptor class.
                    for (ProvidedInterceptor interceptor : interceptors) {
                        interceptor.validate(binder());
                    }
                }
            });

            // Inject interceptors.
            for (ProvidedInterceptor interceptor : interceptors) {
                interceptor.inject();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }

        logger.info("Injector created successfully.");
        return injector;
    }

    Interceptor superBuildInterceptor(InterceptorConfig interceptorConfig, Map interceptorRefParams)
    throws ConfigurationException {
        return super.buildInterceptor(interceptorConfig, interceptorRefParams);
    }

    class ProvidedInterceptor implements Interceptor {
        final InterceptorConfig config;
        final Map params;
        final Class<? extends Interceptor> interceptorClass;
        Interceptor delegate;

        ProvidedInterceptor(InterceptorConfig config, Map params,
                Class<? extends Interceptor> interceptorClass) {
            this.config = config;
            this.params = params;
            this.interceptorClass = interceptorClass;
        }

        void validate(Binder binder) {
            // TODO: Set source from Struts XML.
            if (hasScope(interceptorClass)) {
                binder.addError("Scoping interceptors is not currently supported."
                    + " Please remove the scope annotation from "
                    + interceptorClass.getName() + ".");
            }

            // Make sure it implements Interceptor.
            if (!Interceptor.class.isAssignableFrom(interceptorClass)) {
                binder.addError(interceptorClass.getName() + " must implement "
                    + Interceptor.class.getName() + ".");
            }
        }

        void inject() {
            delegate = superBuildInterceptor(config, params);
        }

        public void destroy() {
            delegate.destroy();
        }

        public void init() {
            throw new AssertionError();
        }

        public String intercept(ActionInvocation invocation) throws Exception {
            return delegate.intercept(invocation);
        }
    }

    /**
     * Returns true if the given class has a scope annotation.
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