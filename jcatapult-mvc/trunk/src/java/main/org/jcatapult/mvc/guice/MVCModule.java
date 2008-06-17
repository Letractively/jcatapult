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
package org.jcatapult.mvc.guice;

import java.util.Locale;

import org.jcatapult.mvc.action.result.ForwardResult;
import org.jcatapult.mvc.action.result.RedirectResult;
import org.jcatapult.mvc.locale.DefaultLocaleStore;
import org.jcatapult.mvc.locale.annotation.CurrentLocale;
import org.jcatapult.mvc.parameter.convert.DefaultConverterProvider;
import org.jcatapult.mvc.parameter.convert.converters.BooleanConverter;
import org.jcatapult.mvc.parameter.convert.converters.CharacterConverter;
import org.jcatapult.mvc.parameter.convert.converters.FileConverter;
import org.jcatapult.mvc.parameter.convert.converters.NumberConverter;
import org.jcatapult.mvc.parameter.convert.converters.StringConverter;
import org.jcatapult.mvc.scope.ActionSessionScope;
import org.jcatapult.mvc.scope.ContextScope;
import org.jcatapult.mvc.scope.FlashScope;
import org.jcatapult.mvc.scope.RequestScope;
import org.jcatapult.mvc.scope.SessionScope;

import com.google.inject.AbstractModule;

/**
 * <p>
 * This class is the main Guice Module that sets up the JCatapult
 * MVC.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MVCModule extends AbstractModule {
    protected void configure() {
        configureConverters();
        configureResults();
        configureScopes();
        configureLocale();
    }

    /**
     * Binds all of the default type converters.
     */
    protected void configureConverters() {
        bind(BooleanConverter.class);
        bind(CharacterConverter.class);
        bind(FileConverter.class);
        bind(NumberConverter.class);
        bind(StringConverter.class);

        // Inject the registry so that the Class to Class mapping is setup
        requestStaticInjection(DefaultConverterProvider.class);
    }

    /**
     * Binds all the default results.
     */
    protected void configureResults() {
        bind(ForwardResult.class);
        bind(RedirectResult.class);
    }

    /**
     * Binds all the default scopes.
     */
    protected void configureScopes() {
        bind(RequestScope.class);
        bind(SessionScope.class);
        bind(ActionSessionScope.class);
        bind(FlashScope.class);
        bind(ContextScope.class);
    }

    /**
     * Sets up the Locale so that it can be injected when the {@link CurrentLocale} annotation is
     * used. The injection is handled by the {@link org.jcatapult.mvc.locale.DefaultLocaleStore} class.
     */
    protected void configureLocale() {
        bind(Locale.class).annotatedWith(CurrentLocale.class).toProvider(DefaultLocaleStore.class);
    }
}