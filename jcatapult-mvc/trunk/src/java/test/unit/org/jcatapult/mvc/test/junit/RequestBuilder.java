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
package org.jcatapult.mvc.test.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.IOException;
import javax.servlet.ServletException;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class RequestBuilder {
    private final String uri;
    private final Map<String, List<String>> parameters = new HashMap<String, List<String>>();
    private final WebappActionTest test;
    private Locale locale = Locale.getDefault();

    public RequestBuilder(String uri, WebappActionTest test) {
        this.uri = uri;
        this.test = test;
    }

    public RequestBuilder withParameter(String name, String value) {
        List<String> list = parameters.get(name);
        if (list == null) {
            list = new ArrayList<String>();
            parameters.put(name, list);
        }

        list.add(value);
        return this;
    }

    public RequestBuilder withLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public void post() throws IOException, ServletException {
        test.run(this, true);
    }

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
}