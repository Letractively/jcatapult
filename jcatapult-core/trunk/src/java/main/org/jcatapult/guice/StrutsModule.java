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
package org.jcatapult.guice;

import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.struts.action.annotation.ActionName;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.opensymphony.xwork2.ActionContext;

/**
 * <p>
 * This is the a module that sets up Struts injection points. Currently,
 * this only provides the ability to inject the current action name into
 * classes.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class StrutsModule extends AbstractModule {
    /**
     * Calls these methods in this order:
     *
     * <ol>
     * <li>{@link #configureActionName()}</li>
     * </ol>
     */
    @Override
    protected void configure() {
        if (ServletObjectsHolder.getServletContext() == null) {
            return;
        }

        configureActionName();
    }

    /**
     * Configures the action name.
     */
    protected void configureActionName() {
        // Bind the servlet context
        bind(String.class).annotatedWith(ActionName.class).toProvider(new Provider<String>() {
            public String get() {
                return ActionContext.getContext().getName();
            }
        });
    }
}