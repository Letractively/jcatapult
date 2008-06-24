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
import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the password control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class PasswordTest extends AbstractInputTest {
    public PasswordTest() {
        super(true);
    }

    @Test
    public void testActionLess() {
        AbstractInput input = new Password();
        run(input, null, "text", "foo.bar", "test", "Test",
            mapNV("name", "test", "class", "css-class", "value", "password", "bundle", "foo.bar"),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"control-container\"><input type=\"password\" class=\"css-class\" id=\"test\" name=\"test\"/></div>\n" +
            "</div>");
    }

    @Test
    public void testAction() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.password");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("password");

        Map<String, String> parameterAttributes = map("param", "param-value");
        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.password", Locale.US, parameterAttributes, "Your name");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Password password = new Password();
        password.setServices(Locale.US, request, ais, ms, fms);
        password.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        password.render(writer, mapNV("name", "user.password", "class", "css-class"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.password@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_password\" class=\"label\">Your name</label></div>\n" +
            "<div class=\"control-container\"><input type=\"password\" class=\"css-class\" id=\"user_password\" name=\"user.password\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, mp);
    }

    @Test
    public void testHardCodedValue() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.password");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("password");

        Map<String, String> parameterAttributes = map("param", "param-value");
        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.password", Locale.US, parameterAttributes, "Your password");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Password password = new Password();
        password.setServices(Locale.US, request, ais, ms, fms);
        password.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        password.render(writer, mapNV("name", "user.password", "class", "css-class", "value", "Barry"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.password@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_password\" class=\"label\">Your password</label></div>\n" +
            "<div class=\"control-container\"><input type=\"password\" class=\"css-class\" id=\"user_password\" name=\"user.password\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, mp);
    }

    @Test
    public void testFieldErrors() {
        Edit action = new Edit();
        HttpServletRequest request = makeRequest();
        ActionInvocationStore ais = makeActionInvocationStore(action, "/test");
        MessageStore ms = makeMessageStore("user.password", "Password is required", "Password must be cool");
        Configuration configuration = makeConfiguration();
        EnvironmentResolver env = makeEnvironmenResolver();
        ContainerResolver containerResolver = makeContainerResolver("password");

        Map<String, String> parameterAttributes = map("param", "param-value");
        MessageProvider mp = makeMessageProvider(action.getClass().getName(), "user.password", Locale.US, parameterAttributes, "Your password");

        FreeMarkerService fms = new DefaultFreeMarkerService(configuration, env, new OverridingTemplateLoader(containerResolver));
        Password password = new Password();
        password.setServices(Locale.US, request, ais, ms, fms);
        password.setMessageProvider(mp);
        StringWriter writer = new StringWriter();
        password.render(writer, mapNV("name", "user.password", "class", "css-class", "value", "Barry"), parameterAttributes);
        assertEquals(
            "<input type=\"hidden\" name=\"user.password@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_password\" class=\"label\"><span class=\"error\">Your password (Password is required, Password must be cool)</span></label></div>\n" +
            "<div class=\"control-container\"><input type=\"password\" class=\"css-class\" id=\"user_password\" name=\"user.password\"/></div>\n" +
            "</div>", writer.toString());

        EasyMock.verify(request, ais, ms, configuration, env, containerResolver, mp);
    }
}