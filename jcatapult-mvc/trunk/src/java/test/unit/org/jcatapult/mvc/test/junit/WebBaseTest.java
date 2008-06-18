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

import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.jcatapult.guice.GuiceContainer;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.mvc.locale.DefaultLocaleStore;
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
    /**
     * A @Before method that injects this class.
     */
    @Before
    public void setup() {
        HttpServletRequest request = makeRequest();
        ServletObjectsHolder.setServletRequest(request);

        HttpServletResponse response = makeResponse();
        ServletObjectsHolder.setServletResponse(response);

        ServletContext context = makeContext();
        ServletObjectsHolder.setServletContext(context);

        GuiceContainer.inject();
        GuiceContainer.initialize();
        GuiceContainer.getInjector().injectMembers(this);
    }

    /**
     * @return  Makes a HttpServletRequest as a nice mock. Sub-classes can override this
     */
    protected HttpServletRequest makeRequest() {
        HttpServletRequest request = EasyMock.createNiceMock(HttpServletRequest.class);
        EasyMock.expect(request.getSession(false)).andReturn(null);
        EasyMock.expect(request.getAttribute(DefaultLocaleStore.LOCALE_KEY)).andReturn(Locale.US);
        EasyMock.replay(request);
        return request;
    }

    /**
     * @return  Makes a HttpServletResponse as a nice mock. Sub-classes can override this
     */
    protected HttpServletResponse makeResponse() {
        return EasyMock.createNiceMock(HttpServletResponse.class);
    }

    /**
     * @return  Makes a ServletContext as a nice mock. Sub-classes can override this
     */
    protected ServletContext makeContext() {
        return EasyMock.createNiceMock(ServletContext.class);
    }
}