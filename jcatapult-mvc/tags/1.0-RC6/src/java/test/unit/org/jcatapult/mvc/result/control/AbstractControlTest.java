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

import org.easymock.EasyMock;
import org.jcatapult.config.Configuration;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.environment.EnvironmentResolver;
import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.freemarker.DefaultFreeMarkerService;
import org.jcatapult.freemarker.OverridingTemplateLoader;
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
    protected FreeMarkerService makeFreeMarkerService(String name) {
        return new DefaultFreeMarkerService(makeConfiguration(), makeEnvironmenResolver(),
            new OverridingTemplateLoader(makeContainerResolver(name)));
    }

    protected HttpServletRequest makeRequest(boolean checkBundle) {
        HttpServletRequest request = EasyMock.createStrictMock(HttpServletRequest.class);
        if (checkBundle) {
            EasyMock.expect(request.getAttribute("jcatapultControlBundle")).andReturn(null);
        }
        EasyMock.replay(request);
        return request;
    }

    protected EnvironmentResolver makeEnvironmenResolver() {
        EnvironmentResolver env = EasyMock.createStrictMock(EnvironmentResolver.class);
        EasyMock.expect(env.getEnvironment()).andReturn("development");
        EasyMock.replay(env);
        return env;
    }

    protected MessageStore makeFieldMessageStore(boolean isError, String field, String... messages) {
        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        if (field != null) {
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            if (messages.length > 0) {
                map.put(field, asList(messages));
            }

            EasyMock.expect(ms.getFieldMessages(MessageType.PLAIN)).andReturn(isError ? new HashMap<String, List<String>>() : map);
            EasyMock.expect(ms.getFieldMessages(MessageType.ERROR)).andReturn(isError ? map : new HashMap<String, List<String>>());
        }

        EasyMock.replay(ms);
        return ms;
    }

    protected MessageStore makeActionMessageStore(boolean errorType, String... errors) {
        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.expect(ms.getActionMessages(MessageType.PLAIN)).andReturn(errorType ? new ArrayList<String>(): asList(errors) );
        EasyMock.expect(ms.getActionMessages(MessageType.ERROR)).andReturn(errorType ? asList(errors) : new ArrayList<String>());
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
        EasyMock.expect(ais.getCurrent()).andReturn(invocation);
        EasyMock.replay(ais);
        return ais;
    }

    protected ContainerResolver makeContainerResolver(String name) {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/" + name + "_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/" + name + ".ftl");
        EasyMock.replay(containerResolver);
        return containerResolver;
    }
}