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
package org.jcatapult.mvc.result.control;

import java.util.ArrayList;
import static java.util.Arrays.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.environment.EnvironmentResolver;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageType;
import org.junit.Ignore;

/**
 * <p>
 * This is a base test for helping test controls.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class AbstractControlTest {
    protected HttpServletRequest makeRequest() {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.replay(request);
        return request;
    }

    protected EnvironmentResolver makeEnvironmenResolver() {
        EnvironmentResolver env = EasyMock.createStrictMock(EnvironmentResolver.class);
        EasyMock.expect(env.getEnvironment()).andReturn("development");
        EasyMock.replay(env);
        return env;
    }

    protected MessageStore makeMessageStore(String field, String... errors) {
        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.expect(ms.getActionMessages(MessageType.PLAIN)).andReturn(new ArrayList<String>());
        EasyMock.expect(ms.getActionMessages(MessageType.ERROR)).andReturn(new ArrayList<String>());
        if (field != null) {
            EasyMock.expect(ms.getFieldMessages(MessageType.PLAIN)).andReturn(new HashMap<String, List<String>>());

            Map<String, List<String>> fieldErrors = new HashMap<String, List<String>>();
            if (errors.length > 0) {
                fieldErrors.put(field, asList(errors));
            }

            EasyMock.expect(ms.getFieldMessages(MessageType.ERROR)).andReturn(fieldErrors);
        }

        EasyMock.replay(ms);
        return ms;
    }

    protected Configuration makeConfiguration() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getInt("jcatapult.freemarker-service.check-seconds", 2)).andReturn(2);
        EasyMock.replay(configuration);
        return configuration;
    }

    protected ActionInvocationStore makeActionInvocationStore(Object action, String uri) {
        ActionInvocation invocation = new DefaultActionInvocation(action, uri, null, null);
        ActionInvocationStore ais = EasyMock.createStrictMock(ActionInvocationStore.class);
        EasyMock.expect(ais.get()).andReturn(invocation);
        EasyMock.replay(ais);
        return ais;
    }
}