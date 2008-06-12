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
import org.example.action.user.Edit;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.environment.EnvironmentResolver;
import org.jcatapult.freemarker.DefaultFreeMarkerService;
import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.freemarker.OverridingTemplateLoader;
import org.jcatapult.mvc.action.ActionMappingWorkflow;
import org.jcatapult.mvc.locale.LocaleWorkflow;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the text control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class TextTest extends AbstractControlTest {
    @Test
    public void testActionLess() {
        HttpServletRequest request = makeRequest();
        ActionMappingWorkflow amw = makeActionMappingWorkflow(request, null, "/test");
        LocaleWorkflow lw = makeLocaleWorkflow(request);
        MessageStore ms = makeMessageStore(request, null);
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver();

        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Text text = new Text(ee);
        text.setServices(lw, amw, ms, fms);
        StringWriter writer = new StringWriter();
        text.render(request, writer, mapNV("name", "test", "class", "css-class"), map("param", "param-value"));
        assertEquals(
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<input type=\"text\" class=\"css-class\" name=\"test\"/>", writer.toString());

        EasyMock.verify(request, amw, lw, ms, configuration, env, containerResolver, ee);
    }

    @Test
    public void testAction() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionMappingWorkflow amw = makeActionMappingWorkflow(request, action, "/test");
        LocaleWorkflow lw = makeLocaleWorkflow(request);
        MessageStore ms = makeMessageStore(request, action);
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver();

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.name", action, request, Locale.US, parameterAttributes)).andReturn("Brian");
        EasyMock.replay(ee);

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Text text = new Text(ee);
        text.setServices(lw, amw, ms, fms);
        StringWriter writer = new StringWriter();
        text.render(request, writer, mapNV("name", "user.name", "class", "css-class"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<input type=\"text\" class=\"css-class\" name=\"user.name\" value=\"Brian\"/>", writer.toString());

        EasyMock.verify(request, amw, lw, ms, configuration, env, containerResolver, ee);
    }

    @Test
    public void testDefaultValue() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionMappingWorkflow amw = makeActionMappingWorkflow(request, action, "/test");
        LocaleWorkflow lw = makeLocaleWorkflow(request);
        MessageStore ms = makeMessageStore(request, action);
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.name", action, request, Locale.US, parameterAttributes)).andReturn(null);
        EasyMock.replay(ee);

        ContainerResolver containerResolver = makeContainerResolver();

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Text text = new Text(ee);
        text.setServices(lw, amw, ms, fms);
        StringWriter writer = new StringWriter();
        text.render(request, writer, mapNV("name", "user.name", "class", "css-class", "defaultValue", "John"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<input type=\"text\" class=\"css-class\" name=\"user.name\" value=\"John\"/>", writer.toString());

        EasyMock.verify(request, amw, lw, ms, configuration, env, containerResolver, ee);
    }

    @Test
    public void testHardCodedValue() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionMappingWorkflow amw = makeActionMappingWorkflow(request, action, "/test");
        LocaleWorkflow lw = makeLocaleWorkflow(request);
        MessageStore ms = makeMessageStore(request, action);
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        ContainerResolver containerResolver = makeContainerResolver();

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Text text = new Text(ee);
        text.setServices(lw, amw, ms, fms);
        StringWriter writer = new StringWriter();
        text.render(request, writer, mapNV("name", "user.name", "class", "css-class", "value", "Barry"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<input type=\"text\" class=\"css-class\" name=\"user.name\" value=\"Barry\"/>", writer.toString());

        EasyMock.verify(request, amw, lw, ms, configuration, env, containerResolver, ee);
    }
}