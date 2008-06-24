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

import org.easymock.EasyMock;
import org.example.action.user.Edit;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the text control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class TextTest extends AbstractInputTest {
    public TextTest() {
        super(true);
    }

    @Test
    public void testActionLess() {
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        AbstractInput input = new Text(ee);
        run(input, null, "text", "foo.bar", "test", "Test",
            mapNV("name", "test", "class", "css-class", "bundle", "foo.bar"),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"control-container\"><input type=\"text\" class=\"css-class\" id=\"test\" name=\"test\"/></div>\n" +
            "</div>");

        EasyMock.verify(ee);
    }

    @Test
    public void testAction() {
        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.name", action, map("param", "param-value"))).andReturn("Brian");
        EasyMock.replay(ee);

        AbstractInput input = new Text(ee);
        run(input, action, "text", "org.example.action.user.Edit", "user.name", "Your name",
            mapNV("name", "user.name", "class", "css-class"),
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\">Your name</label></div>\n" +
            "<div class=\"control-container\"><input type=\"text\" class=\"css-class\" id=\"user_name\" name=\"user.name\" value=\"Brian\"/></div>\n" +
            "</div>");

        EasyMock.verify(ee);
    }

    @Test
    public void testDefaultValue() {
        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.name", action, map("param", "param-value"))).andReturn(null);
        EasyMock.replay(ee);

        AbstractInput input = new Text(ee);
        run(input, action, "text", "org.example.action.user.Edit", "user.name", "Your name",
            mapNV("name", "user.name", "class", "css-class", "defaultValue", "John"),
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\">Your name</label></div>\n" +
            "<div class=\"control-container\"><input type=\"text\" class=\"css-class\" id=\"user_name\" name=\"user.name\" value=\"John\"/></div>\n" +
            "</div>");

        EasyMock.verify(ee);
    }

    @Test
    public void testHardCodedValue() {
        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        AbstractInput input = new Text(ee);
        run(input, action, "text", "org.example.action.user.Edit", "user.name", "Your name",
            mapNV("name", "user.name", "class", "css-class", "value", "Barry"),
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\">Your name</label></div>\n" +
            "<div class=\"control-container\"><input type=\"text\" class=\"css-class\" id=\"user_name\" name=\"user.name\" value=\"Barry\"/></div>\n" +
            "</div>");

        EasyMock.verify(ee);
    }

    @Test
    public void testFieldErrors() {
        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.name", action, map("param", "param-value"))).andReturn("Barry");
        EasyMock.replay(ee);

        AbstractInput input = new Text(ee);
        run(input, action, "text", "org.example.action.user.Edit", "user.name", "Your name",
            mapNV("name", "user.name", "class", "css-class"),
            "<input type=\"hidden\" name=\"user.name@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"user_name\" class=\"label\"><span class=\"error\">Your name (Name is required, Name must be cool)</span></label></div>\n" +
            "<div class=\"control-container\"><input type=\"text\" class=\"css-class\" id=\"user_name\" name=\"user.name\" value=\"Barry\"/></div>\n" +
            "</div>", "Name is required", "Name must be cool");
        EasyMock.verify(ee);
    }
}