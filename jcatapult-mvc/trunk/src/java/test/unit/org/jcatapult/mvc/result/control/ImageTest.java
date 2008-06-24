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

import java.util.Map;
import java.util.Locale;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.MessageProvider;
import org.jcatapult.environment.EnvironmentResolver;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.freemarker.DefaultFreeMarkerService;
import org.jcatapult.freemarker.OverridingTemplateLoader;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;

import static net.java.util.CollectionTools.map;
import static net.java.util.CollectionTools.mapNV;

/**
 * <p>
 * This tests the image control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ImageTest extends AbstractButtonInputTest {
    protected AbstractButtonInput getControl(ExpressionEvaluator ee) {
        return new Image();
    }

    protected String getType() {
        return "image";
    }

    @Test
    public void testIsmap() {
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(null, "/test");
        MessageStore ms = makeMessageStore("test");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver(getType());

        Map<String, String> parameterAttributes = map("param", "param-value");
        MessageProvider mp = makeMessageProvider("foo.bar", "test", Locale.US, parameterAttributes, "Test");

        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        AbstractButtonInput input = getControl(ee);
        input.setServices(Locale.US, request, ais, ms, fms);
        input.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        input.render(writer, mapNV("name", "test", "value", "test-value", "class", "css-class", "bundle", "foo.bar", "ismap", true), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\"><input type=\"" + getType() + "\" class=\"css-class\" id=\"test\" ismap=\"ismap\" name=\"test\" value=\"Test\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }
}