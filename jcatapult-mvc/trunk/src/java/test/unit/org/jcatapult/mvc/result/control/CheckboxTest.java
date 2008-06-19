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
 * This tests the checkbox control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CheckboxTest extends AbstractInputTest {
    @Test
    public void testActionLess() {
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(null, "/test");
        MessageStore ms = makeMessageStore("test");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("checkbox");

        Map<String, String> parameterAttributes = map("param", "param-value");
        MessageProvider mp = makeMessageProvider("foo.bar", "test", Locale.US, parameterAttributes, "Test");

        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Checkbox checkbox = new Checkbox(ee);
        checkbox.setServices(Locale.US, request, ais, ms, fms);
        checkbox.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        checkbox.render(writer, mapNV("name", "test", "value", "test-value", "class", "css-class", "bundle", "foo.bar"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"control-container\"><input type=\"checkbox\" class=\"css-class\" id=\"test\" name=\"test\" value=\"test-value\"/><input type=\"hidden\" name=\"__jc_cb_test\" value=\"\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }

    @Test
    public void testAction() {
        testAction(true);
        testAction(false);
    }

    protected void testAction(boolean flag) {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.male");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("checkbox");

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.male", action, parameterAttributes)).andReturn(flag ? "true" : "false");
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.male", Locale.US, parameterAttributes, "Male?");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Checkbox checkbox = new Checkbox(ee);
        checkbox.setServices(Locale.US, request, ais, ms, fms);
        checkbox.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        checkbox.render(writer, mapNV("name", "user.male", "value", "true", "class", "css-class"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.male@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_male\" class=\"label\">Male?</label></div>\n" +
            "<div class=\"control-container\"><input type=\"checkbox\" " + (flag ? "checked=\"checked\" " : "") + "class=\"css-class\" id=\"user_male\" name=\"user.male\" value=\"true\"/><input type=\"hidden\" name=\"__jc_cb_user.male\" value=\"\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }

    @Test
    public void testDefaultChecked() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.male");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("checkbox");

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.male", action, parameterAttributes)).andReturn(null);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.male", Locale.US, parameterAttributes, "Male?");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Checkbox checkbox = new Checkbox(ee);
        checkbox.setServices(Locale.US, request, ais, ms, fms);
        checkbox.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        checkbox.render(writer, mapNV("name", "user.male", "value", "true", "class", "css-class", "defaultChecked", true), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.male@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_male\" class=\"label\">Male?</label></div>\n" +
            "<div class=\"control-container\"><input type=\"checkbox\" checked=\"checked\" class=\"css-class\" id=\"user_male\" name=\"user.male\" value=\"true\"/><input type=\"hidden\" name=\"__jc_cb_user.male\" value=\"\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }

    @Test
    public void testHardCodedChecked() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.male");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("checkbox");

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.male", Locale.US, parameterAttributes, "Male?");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Checkbox checkbox = new Checkbox(ee);
        checkbox.setServices(Locale.US, request, ais, ms, fms);
        checkbox.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        checkbox.render(writer, mapNV("name", "user.male", "checked", true, "class", "css-class", "value", "true"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.male@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_male\" class=\"label\">Male?</label></div>\n" +
            "<div class=\"control-container\"><input type=\"checkbox\" checked=\"checked\" class=\"css-class\" id=\"user_male\" name=\"user.male\" value=\"true\"/><input type=\"hidden\" name=\"__jc_cb_user.male\" value=\"\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }

    @Test
    public void testFieldErrors() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.male", "Must be male", "Check this box dude!");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("checkbox");

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.male", action, parameterAttributes)).andReturn(null);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.male", Locale.US, parameterAttributes, "Male?");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Checkbox checkbox = new Checkbox(ee);
        checkbox.setServices(Locale.US, request, ais, ms, fms);
        checkbox.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        checkbox.render(writer, mapNV("name", "user.male", "class", "css-class", "value", "true"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.male@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_male\" class=\"label\"><span class=\"error\">Male? (Must be male, Check this box dude!)</span></label></div>\n" +
            "<div class=\"control-container\"><input type=\"checkbox\" class=\"css-class\" id=\"user_male\" name=\"user.male\" value=\"true\"/><input type=\"hidden\" name=\"__jc_cb_user.male\" value=\"\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }
}