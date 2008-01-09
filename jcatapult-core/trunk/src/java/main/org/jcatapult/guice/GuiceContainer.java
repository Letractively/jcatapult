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

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import net.java.lang.ClassClassLoaderResolver;

/**
 * <p>
 * This class is a singleton that allows Guice to be configured by JCatapult
 * and then used in places like Struts or anywhere else in the container. This
 * should be one of the only places that is a singleton in the entire JCatapult
 * system and within any application since it allows for classes to be injected
 * after construction (like JSP tag libraries) and the like.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class GuiceContainer {
    private static final Logger logger = Logger.getLogger(GuiceContainer.class.getName());
    private static String[] guiceModules;
    private static String[] guiceExcludeModules;
    private static boolean loadFromClasspath;
    private static Injector injector;

    /**
     * @return  Retrieves the injector that is setup via the {@link #initialize()} method.
     */
    public static Injector getInjector() {
        return injector;
    }

    /**
     * The comma separated list of guice modules to use if guice is being setup by the filter. This
     * is controlled by the jcatapult configuration property named <strong>jcatapult.guice.modules</strong>.
     *
     * @param   guiceModules A comma separated list of guice modules to use.
     */
    @Inject(optional = true)
    public static void setGuiceModules(@Named("jcatapult.guice.modules") String guiceModules) {
        GuiceContainer.guiceModules = guiceModules.split("\\W*,\\W*");
    }

    /**
     * The comma separated list of guice modules to exclude if the GuiceContainer is configured to search the
     * classpath for modules and implement the leaf node convention. This is controlled by the JCatapult
     * configuration property named <strong>jcatapult.guice.exclude.modules</strong>.
     *
     * @param   guiceExcludeModules A comma separated list of guice modules to exclude.
     */
    @Inject(optional = true)
    public static void setExcludeGuiceModules(@Named("jcatapult.guice.exclude.modules") String guiceExcludeModules) {
        GuiceContainer.guiceExcludeModules = guiceExcludeModules.split("\\W*,\\W*");
    }

    /**
     * The flag that determines if Guice should search the classpath for modules and implement the leaf node
     * convention of finding and creating modules for the injector. This is controlled by the JCatapult
     * configuration property named <strong>jcatapult.guice.load.from.classpath</strong>.
     *
     * @param   loadFromClasspath A flag that determines the mode for the Container.
     */
    @Inject()
    public static void setLoadFromClasspath(@Named("jcatapult.guice.load.from.classpath") boolean loadFromClasspath) {
        GuiceContainer.loadFromClasspath = loadFromClasspath;
    }

    /**
     * Please do not invoke this method unless you know what you are doing. This initializes Guice and
     * does it once only so that synchronization is not used. This is called by the JCatapultFilter
     * in its constructor and should cover all cases.
     */
    public static final void initialize() {
        logger.info("Initializing JCatapult's Guice support");

        // Inject the JCatapult configuration
        Guice.createInjector(new ConfigurationModule(), new AbstractModule() {
            protected void configure() {
                requestStaticInjection(GuiceContainer.class);
            }
        });

        if (loadFromClasspath) {
            loadFromClasspath();
        } else {
            loadFromConfiguration();
        }
    }

    private static void loadFromConfiguration() {
        if (GuiceContainer.guiceModules == null) {
            throw new IllegalStateException("JCatapult has Guice initialization set to use the JCatapult " +
                "configuration file named [jcatapult.properties] but no modules were specified in that file. " +
                "Use the jcatapult.guice.modules configuration property in that file to setup your Guice modules.");
        }

        List<Module> modules = new ArrayList<Module>();
        for (String moduleName : GuiceContainer.guiceModules) {
            try {
                Class moduleClass = Class.forName(moduleName);
                if (!Module.class.isAssignableFrom(moduleClass)) {
                    throw new IllegalArgumentException("Invalid Guice module class [" + moduleName + "]");
                }

                Module module = (Module) moduleClass.newInstance();
                modules.add(module);
                logger.finest("Adding module [" + module.getClass().getName() + "] to the guice injector");
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        GuiceContainer.injector = Guice.createInjector(modules);
    }

    private static void loadFromClasspath() {
        ClassClassLoaderResolver resolver = new ClassClassLoaderResolver();
        resolver.findByLocators(new ClassClassLoaderResolver.IsA(Module.class), false, GuiceContainer.guiceExcludeModules, "guice");
        Set<Class<?>> moduleClasses = resolver.getMatches();
        Set<Class<?>> matches = new HashSet<Class<?>>();

        for (Class<?> moduleClass : moduleClasses) {
            // Remove any instances of this classes parents from the matches
            Class<?> parent = moduleClass.getSuperclass();
            while (Module.class.isAssignableFrom(parent)) {
                matches.remove(parent);
            }

            matches.add(moduleClass);
        }

        Module[] modules = new Module[matches.size()];
        int index = 0;
        for (Class<?> match : matches) {
            try {
                modules[index++] = (Module) match.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        GuiceContainer.injector = Guice.createInjector(modules);
    }
}