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
package org.jcatapult.mvc.guice;

import org.jcatapult.mvc.action.result.ForwardResult;
import org.jcatapult.mvc.action.result.RedirectResult;
import org.jcatapult.mvc.parameters.convert.converters.BooleanConverter;
import org.jcatapult.mvc.parameters.convert.converters.CharacterConverter;
import org.jcatapult.mvc.parameters.convert.converters.FileConverter;
import org.jcatapult.mvc.parameters.convert.converters.NumberConverter;
import org.jcatapult.mvc.parameters.convert.converters.StringConverter;

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
    }

    /**
     * Binds all the default results.
     */
    protected void configureResults() {
        bind(ForwardResult.class);
        bind(RedirectResult.class);
    }
}