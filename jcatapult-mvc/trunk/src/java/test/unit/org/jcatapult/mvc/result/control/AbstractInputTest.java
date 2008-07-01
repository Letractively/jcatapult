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

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.environment.EnvironmentResolver;
import org.jcatapult.freemarker.DefaultFreeMarkerService;
import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.freemarker.OverridingTemplateLoader;
import org.jcatapult.l10n.MessageProvider;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageStore;
import static org.junit.Assert.*;
import org.junit.Ignore;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This is an abstract test for helping test input controls.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class AbstractInputTest extends AbstractControlTest {
    private boolean label;

    public AbstractInputTest(boolean label) {
        this.label = label;
    }

    protected MessageProvider makeMessageProvider(String bundleName, String fieldName, Locale locale,
            Map<String, String> attributes, String message) {
        MessageProvider mp = EasyMock.createStrictMock(MessageProvider.class);
        EasyMock.expect(mp.getMessage(bundleName, fieldName, locale, attributes)).andReturn(message);
        EasyMock.replay(mp);
        return mp;
    }

    protected ContainerResolver makeContainerResolver(String name) {
        ContainerResolver containerResolver = EasyMock.createStrictMock(ContainerResolver.class);
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/" + name + "_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/" + name + ".ftl");
        EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/parameter-attributes_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/parameter-attributes.ftl");
        if (label) {
            EasyMock.expect(containerResolver.getRealPath("WEB-INF/control-templates/label_en_US.ftl")).andReturn("src/ftl/main/WEB-INF/control-templates/label.ftl");
        }
        EasyMock.replay(containerResolver);
        return containerResolver;
    }

    /**
     * Runs the test.
     *
     * @param   input The input control.
     * @param   action The action or null.
     * @param   template The name of the template.
     * @param   bundle The bundle for the message provider.
     * @param   key The key passed to the message provider.
     * @param   message The message returned from the message provider.
     * @param   attributes The attributes passed to the control.
     * @param   result The expected result.
     * @param   errors The field errors.
     */
    protected void run(AbstractInput input, Object action, String template, String bundle, String key,
            String message, Map<String, Object> attributes, String result, String... errors) {
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore(key, errors);
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver(template);

        Map<String, String> parameterAttributes = map("param", "param-value");
        MessageProvider mp = makeMessageProvider(bundle, key, Locale.US, parameterAttributes, message);
        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        input.setServices(Locale.US, request, ais, ms, fms);
        input.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        input.renderStart(writer, attributes, parameterAttributes);
        input.renderEnd(writer);
        assertEquals(result, writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, mp);
    }
}