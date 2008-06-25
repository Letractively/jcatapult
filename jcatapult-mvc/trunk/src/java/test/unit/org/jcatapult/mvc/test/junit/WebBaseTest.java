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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.jcatapult.guice.GuiceContainer;
import org.jcatapult.mvc.test.MockHttpServletRequest;
import org.jcatapult.mvc.test.MockHttpServletResponse;
import org.jcatapult.mvc.test.MockHttpSession;
import org.jcatapult.mvc.test.MockServletContext;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.junit.Before;
import org.junit.Ignore;

/**
 * <p>
 * This class is a JUnit 4 base test for MVC testing.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class WebBaseTest {
    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;
    protected MockServletContext context;

    /**
     * A @Before method that injects this class.
     */
    @Before
    public void setup() {
        this.request = makeRequest();
        ServletObjectsHolder.setServletRequest(request);

        this.response = makeResponse();
        ServletObjectsHolder.setServletResponse(response);

        this.context = makeContext();
        ServletObjectsHolder.setServletContext(context);

        GuiceContainer.inject();
        GuiceContainer.initialize();
        GuiceContainer.getInjector().injectMembers(this);
    }

    /**
     * @return  Makes a HttpServletRequest mock. Sub-classes can override this to change the settings.
     */
    protected MockHttpServletRequest makeRequest() {
        return new MockHttpServletRequest(new HashMap<String, List<String>>(), "/test", "UTF-8", Locale.US, false);
    }

    /**
     * @return  Makes a HttpServletResponse as a nice mock. Sub-classes can override this
     */
    protected MockHttpServletResponse makeResponse() {
        return new MockHttpServletResponse();
    }

    /**
     * @return  Makes a ServletContext as a nice mock. Sub-classes can override this
     */
    protected MockServletContext makeContext() {
        return MockHttpSession.context;
    }
}