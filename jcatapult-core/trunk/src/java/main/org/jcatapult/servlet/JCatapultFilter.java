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
package org.jcatapult.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.guice.ConfigurationModule;
import org.jcatapult.jpa.EntityManagerContext;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Named;

/**
 * <p>
 * This is the main Servlet filter for the JCatapult framework. This
 * will setup the {@link ServletContextHolder}, the {@link EntityManagerContext}
 * and any other JCatapult configuration that is needed.
 * </p>
 *
 * <p>
 * This filter can optionally perform a number of other tasks as well.
 * First, it can create a Guice injector and place it into the ServletContext
 * under the key <strong>guiceInjector</strong>. This can be turned on using
 * the JCatapult configuration file and setting the property <strong>jcatapult.filter.guice.init</strong>
 * to true. In order to specify the Guice modules to use, the property
 * <strong>jcatapult.filter.guice.modules</strong> should contain a comma separated list
 * of modules.
 * </p>
 *
 * <p>
 * This filter can also initialize and WILL initialize JPA on startup. This
 * will create an EntityManagerFactory using either a persistence unit
 * named <strong>punit</strong> or using the value of the <strong>jcatapult.filter.jpa.unit</strong>
 * JCatapult configuration property. You can turn off JPA setup using the
 * <strong>jcatapult.filter.jpa.enabled</strong> boolean property. If this parameter
 * is set to <strong>false</strong>, JPA will not be setup.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JCatapultFilter implements Filter {

    private static final Logger logger = Logger.getLogger(JCatapultFilter.class.getName());

    public static final String ORIGINAL_REQUEST_URI = "ORIGINAL_REQUEST_URI";

    private EntityManagerFactory emf;
    private boolean initGuice;
    private String guiceModules;
    private boolean jpaEnabled;
    private String persistentUnit;

    /**
     * Constructs the filter and then injects the configuration properties using Guice and the JCatapult
     * configuration files. This is done in the constructor so that the this is more easily tested.
     */
    public JCatapultFilter() {
        // Inject the JCatapult configuration
        Injector injector = Guice.createInjector(new ConfigurationModule());
        injector.injectMembers(this);
    }

    /**
     * The boolean flag controlled by the jcatapult configuration property named
     * <strong>jcatapult.filter.guice.init</strong> that determines if the filter will setup Guice or
     * not.
     *
     * @param   initGuice If true guice will be setup, false it will not.
     */
    @Inject(optional = true)
    public void setInitGuice(@Named("jcatapult.filter.guice.init") boolean initGuice) {
        this.initGuice = initGuice;
    }

    /**
     * The comma separated list of guice modules to use if guice is being setup by the filter. This
     * is controlled by the jcatapult configuration property named <strong>jcatapult.filter.guice.modules</strong>.
     *
     * @param   guiceModules A comma separated list of guice modules to use.
     */
    @Inject(optional = true)
    public void setGuiceModules(@Named("jcatapult.filter.guice.modules") String guiceModules) {
        this.guiceModules = guiceModules;
    }

    /**
     * The boolean flag controlled by the jcatapult configuration property named
     * <strong>jcatapult.filter.jpa.enabled</strong> that controls whether or not JPA will be
     * initialized during init and then setup during each request.
     *
     * @param   jpaEnabled If true, JPA will be setup, false it will not.
     */
    @Inject
    public void setJpaEnabled(@Named("jcatapult.filter.jpa.enabled") boolean jpaEnabled) {
        this.jpaEnabled = jpaEnabled;
    }

    /**
     * The name of the JPA persistent unit to use if JPA is being setup. This is controlled by the
     * jcatapult configuration property named <strong>jcatapult.filter.jpa.unit</strong>.
     *
     * @param   persistentUnit The name of the persistent unit.
     */
    @Inject
    public void setPersistentUnit(@Named("jcatapult.filter.jpa.unit") String persistentUnit) {
        this.persistentUnit = persistentUnit;
    }

    /**
     * First this adds the ServletContext to the holder so that is available anywhere in the web
     * application. Next this checks for the <em>persistentUnit</em> configuration option and defaults
     * to <em>punit</em>. This also constructs the EntityManagerFactory that will be used to create
     * the EntityManagers from.
     *
     * @param   filterConfig The filter config to get the init params from.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.finest("Initializing JCatapultFilter");
        ServletContextHolder.setServletContext(filterConfig.getServletContext());

        if (jpaEnabled) {
            emf = Persistence.createEntityManagerFactory(persistentUnit);
        }

        // setup guice and set it into the servlet context
        initGuice();
    }

    protected void initGuice() throws ServletException {
        logger.finest("Initializing Guice [" + initGuice + "]");
        if (!initGuice) {
            return;
        }

        if (guiceModules == null) {
            throw new ServletException("JCatapult filter has Guice initialization turned on but " +
                "no modules were specified. Use the jcatapult.filter.guice.modules configuration " +
                "property to setup some Guice modules.");
        }

        List<Module> modules = new ArrayList<Module>();
        String[] moduleNames = guiceModules.split(",");

        for (String moduleName : moduleNames) {
            try {
                Class moduleClass = Class.forName(moduleName);
                if (!Module.class.isAssignableFrom(moduleClass)) {
                    throw new ServletException("Invalid Guice module class [" + moduleName + "]");
                }

                Module module = (Module) moduleClass.newInstance();
                modules.add(module);
                logger.finest("Adding module [" + module.getClass().getName() + "] to the guice injector");
            } catch (ClassNotFoundException e) {
                throw new ServletException(e);
            } catch (IllegalAccessException e) {
                throw new ServletException(e);
            } catch (InstantiationException e) {
                throw new ServletException(e);
            }
        }
        Injector injector = Guice.createInjector(modules);
        ServletContextHolder.getServletContext().setAttribute("guiceInjector", injector);
    }

    /**
     * Creates an EntityManager, stores it in the context and then clears the context in a finally
     * block.
     *
     * @param   request Passed down chain.
     * @param   response Passed down chain.
     * @param   chain The chain.
     * @throws  IOException If the chain throws an exception.
     * @throws  ServletException If the chain throws an exception.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {

        if (request.getAttribute(ORIGINAL_REQUEST_URI) == null) {
            request.setAttribute(ORIGINAL_REQUEST_URI, ((HttpServletRequest) request).getRequestURI());
        }

        if (jpaEnabled) {
            EntityManager entityManager = emf.createEntityManager();
            try {
                // Grab the EntityManager for this request. This is lightweight so don't freak out that
                // this will use resources. It doesn't grab a JDBC connection until it has to.
                EntityManagerContext.set(entityManager);

                // Proceed down the chain
                chain.doFilter(request, response);
            } finally {
                // Clear out the context just to be safe.
                EntityManagerContext.remove();
                entityManager.close();
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Closes the EntityManagerFactory and removes the context from the holder.
     */
    public void destroy() {
        if (jpaEnabled) {
            emf.close();
        }
    }
}