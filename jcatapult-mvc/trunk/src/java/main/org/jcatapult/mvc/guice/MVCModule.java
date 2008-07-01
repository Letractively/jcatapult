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

import org.jcatapult.mvc.action.result.ForwardResult;
import org.jcatapult.mvc.action.result.RedirectResult;
import org.jcatapult.mvc.parameter.convert.DefaultConverterProvider;
import org.jcatapult.mvc.parameter.convert.converters.BooleanConverter;
import org.jcatapult.mvc.parameter.convert.converters.CharacterConverter;
import org.jcatapult.mvc.parameter.convert.converters.CollectionConverter;
import org.jcatapult.mvc.parameter.convert.converters.FileConverter;
import org.jcatapult.mvc.parameter.convert.converters.NumberConverter;
import org.jcatapult.mvc.parameter.convert.converters.StringConverter;
import org.jcatapult.mvc.result.control.Button;
import org.jcatapult.mvc.result.control.Checkbox;
import org.jcatapult.mvc.result.control.File;
import org.jcatapult.mvc.result.control.Form;
import org.jcatapult.mvc.result.control.Hidden;
import org.jcatapult.mvc.result.control.Image;
import org.jcatapult.mvc.result.control.Password;
import org.jcatapult.mvc.result.control.Radio;
import org.jcatapult.mvc.result.control.Reset;
import org.jcatapult.mvc.result.control.Submit;
import org.jcatapult.mvc.result.control.Text;
import org.jcatapult.mvc.result.control.Textarea;
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
        configureControls();
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
        bind(CollectionConverter.class);

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
     * This binds the controls so that they can be resolved by the FreeMarker result handling and
     * dynamically added to the Map as directives under the jc key and using the class name as the
     * directive name.
     */
    protected void configureControls() {
        bind(Button.class);
        bind(Checkbox.class);
        bind(File.class);
        bind(Form.class);
        bind(Hidden.class);
        bind(Image.class);
        bind(Password.class);
        bind(Radio.class);
        bind(Reset.class);
        bind(Submit.class);
        bind(Text.class);
        bind(Textarea.class);

        requestStaticInjection(ForwardResult.class);
    }
}