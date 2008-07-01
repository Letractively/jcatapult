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
package org.jcatapult.mvc.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * <p>
 * This class is a builder that helps create a test HTTP request that
 * is sent to the MVC.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class RequestBuilder {
    private final String uri;
    private final Map<String, List<String>> parameters = new HashMap<String, List<String>>();
    private final WebappTestRunner test;
    private final List<Module> modules = new ArrayList<Module>();
    private Locale locale = Locale.getDefault();

    public RequestBuilder(String uri, WebappTestRunner test) {
        this.uri = uri;
        this.test = test;
    }

    /**
     * Sets an HTTP request parameter. This can be called multiple times with the same name it it will
     * create a list of values for the HTTP parameter.
     *
     * @param   name The name of the parameter.
     * @param   value The parameter value.
     * @return  This.
     */
    public RequestBuilder withParameter(String name, String value) {
        List<String> list = parameters.get(name);
        if (list == null) {
            list = new ArrayList<String>();
            parameters.put(name, list);
        }

        list.add(value);
        return this;
    }

    /**
     * Sets the locale that will be used.
     *
     * @param   locale The locale.
     * @return  This.
     */
    public RequestBuilder withLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Adds a mocked out service, action, etc to this request. This helps if the action needs to be
     * tested with different configuration or for error handling.
     *
     * @param   iface The interface to mock out.
     * @param   impl The mocked out implementation of the interface.
     * @return  This.
     */
    public <T> RequestBuilder withMock(final Class<T> iface, final T impl) {
        modules.add(new Module() {
            public void configure(Binder binder) {
                binder.bind(iface).toInstance(impl);
            }
        });

        return this;
    }

    /**
     * Sends the HTTP request to the MVC as a POST.
     *
     * @throws  IOException If the MVC throws an exception.
     * @throws  ServletException If the MVC throws an exception.
     */
    public void post() throws IOException, ServletException {
        test.run(this, true);
    }

    /**
     * Sends the HTTP request to the MVC as a GET.
     *
     * @throws  IOException If the MVC throws an exception.
     * @throws  ServletException If the MVC throws an exception.
     */
    public void get() throws IOException, ServletException {
        test.run(this, false);
    }

    public String getUri() {
        return uri;
    }

    public Locale getLocale() {
        return locale;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public List<Module> getModules() {
        return modules;
    }
}