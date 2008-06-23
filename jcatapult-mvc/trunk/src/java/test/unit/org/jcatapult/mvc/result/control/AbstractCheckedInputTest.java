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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import org.junit.Ignore;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This is an abstract test that is used to test checked controls.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public abstract class AbstractCheckedInputTest extends AbstractInputTest {
    /**
     * Makes a control for testing.
     *
     * @param   ee The expression evaluator.
     * @return  The Control.
     */
    protected abstract AbstractCheckedInput getControl(ExpressionEvaluator ee);

    /**
     * @return  The input type.
     */
    protected abstract String getType();

    /**
     * @return  The prefix for the hidden tag.
     */
    protected abstract String getHiddenPrefix();

    @Test
    public void testActionLess() {
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
        AbstractCheckedInput input = getControl(ee);
        input.setServices(Locale.US, request, ais, ms, fms);
        input.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        input.render(writer, mapNV("name", "test", "value", "test-value", "class", "css-class", "bundle", "foo.bar"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"control-container\"><input type=\"" + getType() + "\" class=\"css-class\" id=\"test\" name=\"test\" value=\"test-value\"/><input type=\"hidden\" name=\"" + getHiddenPrefix() + "_test\" value=\"\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }

    @Test
    public void testAction() {
        // Test booleans
        testAction(true, true, "true");
        testAction(false, false, "true");

        // Test arrays
        testAction(true, new int[]{1, 2, 3}, "1");
        testAction(true, new int[]{1, 2, 3}, "2");
        testAction(true, new int[]{1, 2, 3}, "3");
        testAction(false, new int[]{1, 2, 3}, "4");

        // Test collection with Strings
        Set<String> set = new HashSet<String>();
        set.add("1");
        set.add("2");
        set.add("3");
        testAction(true, set, "1");
        testAction(true, set, "2");
        testAction(true, set, "3");
        testAction(false, set, "4");

        // Test collection with Integers
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        testAction(true, list, "1");
        testAction(true, list, "2");
        testAction(true, list, "3");
        testAction(false, list, "4");
    }

    protected void testAction(boolean flag, Object beanValue, String value) {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.male");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver(getType());

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.male", action)).andReturn(beanValue);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.male", Locale.US, parameterAttributes, "Male?");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        AbstractCheckedInput input = getControl(ee);
        input.setServices(Locale.US, request, ais, ms, fms);
        input.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        input.render(writer, mapNV("name", "user.male", "value", value, "class", "css-class"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.male@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_male\" class=\"label\">Male?</label></div>\n" +
            "<div class=\"control-container\"><input type=\"" + getType() + "\" " + (flag ? "checked=\"checked\" " : "") + "class=\"css-class\" id=\"user_male\" name=\"user.male\" value=\"" + value + "\"/><input type=\"hidden\" name=\"" + getHiddenPrefix() + "_user.male\" value=\"\"/></div>\n" +
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
        ContainerResolver containerResolver = makeContainerResolver(getType());

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.male", action)).andReturn(null);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.male", Locale.US, parameterAttributes, "Male?");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        AbstractCheckedInput input = getControl(ee);
        input.setServices(Locale.US, request, ais, ms, fms);
        input.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        input.render(writer, mapNV("name", "user.male", "value", "true", "class", "css-class", "defaultChecked", true), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.male@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_male\" class=\"label\">Male?</label></div>\n" +
            "<div class=\"control-container\"><input type=\"" + getType() + "\" checked=\"checked\" class=\"css-class\" id=\"user_male\" name=\"user.male\" value=\"true\"/><input type=\"hidden\" name=\"" + getHiddenPrefix() + "_user.male\" value=\"\"/></div>\n" +
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
        ContainerResolver containerResolver = makeContainerResolver(getType());

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.male", Locale.US, parameterAttributes, "Male?");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        AbstractCheckedInput input = getControl(ee);
        input.setServices(Locale.US, request, ais, ms, fms);
        input.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        input.render(writer, mapNV("name", "user.male", "checked", true, "class", "css-class", "value", "true"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.male@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_male\" class=\"label\">Male?</label></div>\n" +
            "<div class=\"control-container\"><input type=\"" + getType() + "\" checked=\"checked\" class=\"css-class\" id=\"user_male\" name=\"user.male\" value=\"true\"/><input type=\"hidden\" name=\"" + getHiddenPrefix() + "_user.male\" value=\"\"/></div>\n" +
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
        ContainerResolver containerResolver = makeContainerResolver(getType());

        Map<String, String> parameterAttributes = map("param", "param-value");
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.male", action)).andReturn(null);
        EasyMock.replay(ee);

        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.male", Locale.US, parameterAttributes, "Male?");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        AbstractCheckedInput input = getControl(ee);
        input.setServices(Locale.US, request, ais, ms, fms);
        input.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        input.render(writer, mapNV("name", "user.male", "class", "css-class", "value", "true"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.male@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_male\" class=\"label\"><span class=\"error\">Male? (Must be male, Check this box dude!)</span></label></div>\n" +
            "<div class=\"control-container\"><input type=\"" + getType() + "\" class=\"css-class\" id=\"user_male\" name=\"user.male\" value=\"true\"/><input type=\"hidden\" name=\"" + getHiddenPrefix() + "_user.male\" value=\"\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, ee, mp);
    }
}