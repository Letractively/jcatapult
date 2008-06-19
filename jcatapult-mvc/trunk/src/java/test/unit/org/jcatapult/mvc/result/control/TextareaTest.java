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
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageProvider;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the textarea control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class TextareaTest extends AbstractInputTest {
    @Test
    public void testActionLess() {
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(null, "/test");
        MessageStore ms = makeMessageStore("test");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("textarea");

        Map<String, String> parameterAttributes = map("param", "param-value");
        MessageProvider mp = makeMessageProvider("foo.bar", "test", Locale.US, parameterAttributes, "Test");

        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Textarea textarea = new Textarea(ee);
        textarea.setServices(Locale.US, request, ais, ms, fms);
        textarea.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        textarea.render(writer, mapNV("name", "test", "class", "css-class", "bundle", "foo.bar"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"control-container\"><textarea class=\"css-class\" id=\"test\" name=\"test\"></textarea></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }

    @Test
    public void testAction() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.name");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("textarea");

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.name", action, parameterAttributes)).andReturn("Brian");
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.name", Locale.US, parameterAttributes, "Your name");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Textarea textarea = new Textarea(ee);
        textarea.setServices(Locale.US, request, ais, ms, fms);
        textarea.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        textarea.render(writer, mapNV("name", "user.name", "class", "css-class"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\">Your name</label></div>\n" +
            "<div class=\"control-container\"><textarea class=\"css-class\" id=\"user_name\" name=\"user.name\">Brian</textarea></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }

    @Test
    public void testDefaultValue() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.name");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("textarea");

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.name", action, parameterAttributes)).andReturn(null);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.name", Locale.US, parameterAttributes, "Your name");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Textarea textarea = new Textarea(ee);
        textarea.setServices(Locale.US, request, ais, ms, fms);
        textarea.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        textarea.render(writer, mapNV("name", "user.name", "class", "css-class", "defaultValue", "John"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\">Your name</label></div>\n" +
            "<div class=\"control-container\"><textarea class=\"css-class\" id=\"user_name\" name=\"user.name\">John</textarea></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }

    @Test
    public void testHardCodedValue() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.name");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("textarea");

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.name", Locale.US, parameterAttributes, "Your name");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Textarea textarea = new Textarea(ee);
        textarea.setServices(Locale.US, request, ais, ms, fms);
        textarea.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        textarea.render(writer, mapNV("name", "user.name", "class", "css-class", "value", "Barry"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\">Your name</label></div>\n" +
            "<div class=\"control-container\"><textarea class=\"css-class\" id=\"user_name\" name=\"user.name\">Barry</textarea></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }

    @Test
    public void testFieldErrors() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.name", "Name is required", "Name must be cool");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("textarea");

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.name", Locale.US, parameterAttributes, "Your name");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Textarea textarea = new Textarea(ee);
        textarea.setServices(Locale.US, request, ais, ms, fms);
        textarea.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        textarea.render(writer, mapNV("name", "user.name", "class", "css-class", "value", "Barry"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\"><span class=\"error\">Your name (Name is required, Name must be cool)</span></label></div>\n" +
            "<div class=\"control-container\"><textarea class=\"css-class\" id=\"user_name\" name=\"user.name\">Barry</textarea></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }
}