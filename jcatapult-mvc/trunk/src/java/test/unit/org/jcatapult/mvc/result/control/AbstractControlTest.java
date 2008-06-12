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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.environment.EnvironmentResolver;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionMappingWorkflow;
import org.jcatapult.mvc.action.DefaultActionInvocation;
import org.jcatapult.mvc.locale.LocaleWorkflow;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageType;
import org.junit.Ignore;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
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

    protected MessageStore makeMessageStore(HttpServletRequest request, Object action) {
        MessageStore ms = EasyMock.createStrictMock(MessageStore.class);
        EasyMock.expect(ms.getFieldMessages(request, MessageType.PLAIN, action)).andReturn(new HashMap<String, List<String>>());
        EasyMock.expect(ms.getFieldMessages(request, MessageType.ERROR, action)).andReturn(new HashMap<String, List<String>>());
        EasyMock.expect(ms.getActionMessages(request, MessageType.PLAIN, action)).andReturn(new ArrayList<String>());
        EasyMock.expect(ms.getActionMessages(request, MessageType.ERROR, action)).andReturn(new ArrayList<String>());
        EasyMock.replay(ms);
        return ms;
    }

    protected Configuration makeConfiguration() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getInt("jcatapult.freemarker-service.check-seconds", 2)).andReturn(2);
        EasyMock.replay(configuration);
        return configuration;
    }

    protected ActionMappingWorkflow makeActionMappingWorkflow(HttpServletRequest request, Object action, String uri) {
        ActionInvocation invocation = new DefaultActionInvocation(action, uri, null);
        ActionMappingWorkflow amw = EasyMock.createStrictMock(ActionMappingWorkflow.class);
        EasyMock.expect(amw.fetch(request)).andReturn(invocation);
        EasyMock.replay(amw);
        return amw;
    }

    protected LocaleWorkflow makeLocaleWorkflow(HttpServletRequest request) {
        LocaleWorkflow lw = EasyMock.createStrictMock(LocaleWorkflow.class);
        EasyMock.expect(lw.getLocale(request)).andReturn(Locale.US);
        EasyMock.replay(lw);
        return lw;
    }

    protected ContainerResolver makeContainerResolver() {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/text_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/text.ftl");
        EasyMock.replay(containerResolver);
        return containerResolver;
    }
}