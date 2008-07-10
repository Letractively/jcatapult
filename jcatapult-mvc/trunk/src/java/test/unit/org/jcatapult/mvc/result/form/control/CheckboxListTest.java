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
package org.jcatapult.mvc.result.form.control;

import static java.util.Arrays.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.example.action.user.Edit;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.result.jsp.control.AbstractInputTest;
import org.junit.Test;

import static net.java.util.CollectionTools.*;
import net.java.util.Pair;

/**
 * <p>
 * This tests the checkboxlist control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CheckboxListTest extends AbstractInputTest {
    public CheckboxListTest() {
        super(true);
    }

    @Test
    public void testActionLess() {
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        CheckboxList checkboxlist = new CheckboxList();
        checkboxlist.setExpressionEvaluator(ee);
        run(checkboxlist, null, "checkbox-list", "foo.bar", "test", "Test",
            mapNV("name", "test", "class", "css-class", "bundle", "foo.bar", "items", asList("one", "two", "three")),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">\n" +
            "  <input type=\"checkbox\" value=\"one\" class=\"css-class\" name=\"test\"/><span class=\"checkbox-text\">one</span>\n" +
            "</div>\n" +
            "</div>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">\n" +
            "  <input type=\"checkbox\" value=\"two\" class=\"css-class\" name=\"test\"/><span class=\"checkbox-text\">two</span>\n" +
            "</div>\n" +
            "</div>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">\n" +
            "  <input type=\"checkbox\" value=\"three\" class=\"css-class\" name=\"test\"/><span class=\"checkbox-text\">three</span>\n" +
            "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }

    @Test
    public void testAction() {
        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.addresses['work'].country", action)).andReturn("US");
        EasyMock.replay(ee);

        CheckboxList checkboxlist = new CheckboxList();
        checkboxlist.setExpressionEvaluator(ee);
        run(checkboxlist, action, "checkbox-list", "org.example.action.user.Edit", "user.addresses['work'].country", "Country",
            mapNV("name", "user.addresses['work'].country", "class", "css-class", "items", lmap("US", "United States", "DE", "Germany")),
            "<input type=\"hidden\" name=\"user.addresses['work'].country@param\" value=\"param-value\"/>\n" +
            "<div class=\"label-container\"><label for=\"user_addresses['work']_country\" class=\"label\">Country</label></div>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">\n" +
            "  <input type=\"checkbox\" checked=\"checked\" value=\"US\" class=\"css-class\" name=\"user.addresses['work'].country\"/><span class=\"checkbox-text\">United States</span>\n" +
            "</div>\n" +
            "</div>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">\n" +
            "  <input type=\"checkbox\" value=\"DE\" class=\"css-class\" name=\"user.addresses['work'].country\"/><span class=\"checkbox-text\">Germany</span>\n" +
            "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }

    @Test
    public void testExpressions() {
        Pair<String, String> us = new Pair<String, String>("US", "United States");
        Pair<String, String> de = new Pair<String, String>("DE", "Germany");

        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.addresses['work'].country", action)).andReturn("US");
        EasyMock.expect(ee.getValue("first", us)).andReturn("US");
        EasyMock.expect(ee.getValue("second", us)).andReturn("United States");
        EasyMock.expect(ee.getValue("first", de)).andReturn("DE");
        EasyMock.expect(ee.getValue("second", de)).andReturn("Germany");
        EasyMock.replay(ee);

        CheckboxList checkboxlist = new CheckboxList();
        checkboxlist.setExpressionEvaluator(ee);
        run(checkboxlist, action, "checkbox-list", "org.example.action.user.Edit", "user.addresses['work'].country", "Country",
            mapNV("name", "user.addresses['work'].country", "class", "css-class", "valueExpr", "first", "textExpr", "second", "items", array(us, de)),
            "<input type=\"hidden\" name=\"user.addresses['work'].country@param\" value=\"param-value\"/>\n" +
            "<div class=\"label-container\"><label for=\"user_addresses['work']_country\" class=\"label\">Country</label></div>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">\n" +
            "  <input type=\"checkbox\" checked=\"checked\" value=\"US\" class=\"css-class\" name=\"user.addresses['work'].country\"/><span class=\"checkbox-text\">United States</span>\n" +
            "</div>\n" +
            "</div>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">\n" +
            "  <input type=\"checkbox\" value=\"DE\" class=\"css-class\" name=\"user.addresses['work'].country\"/><span class=\"checkbox-text\">Germany</span>\n" +
            "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }

    @Test
    public void testFieldErrors() {
        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("user.addresses['work'].country", action)).andReturn("US");
        EasyMock.replay(ee);

        CheckboxList checkboxlist = new CheckboxList();
        checkboxlist.setExpressionEvaluator(ee);
        run(checkboxlist, action, "checkbox-list", "org.example.action.user.Edit", "user.addresses['work'].country", "Country",
            mapNV("name", "user.addresses['work'].country", "class", "css-class", "items", lmap("US", "United States", "DE", "Germany")),
            "<input type=\"hidden\" name=\"user.addresses['work'].country@param\" value=\"param-value\"/>\n" +
            "<div class=\"label-container\"><label for=\"user_addresses['work']_country\" class=\"label\"><span class=\"error\">Country (Country is required, Country must be cool)</span></label></div>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">\n" +
            "  <input type=\"checkbox\" checked=\"checked\" value=\"US\" class=\"css-class\" name=\"user.addresses['work'].country\"/><span class=\"checkbox-text\">United States</span>\n" +
            "</div>\n" +
            "</div>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">\n" +
            "  <input type=\"checkbox\" value=\"DE\" class=\"css-class\" name=\"user.addresses['work'].country\"/><span class=\"checkbox-text\">Germany</span>\n" +
            "</div>\n" +
            "</div>\n", "Country is required", "Country must be cool");
        EasyMock.verify(ee);
    }

    public static <T> Map<T, T> lmap(T... values) {
        LinkedHashMap<T, T> map = new LinkedHashMap<T,T>();
        for (int i = 0; i < values.length; i = i + 2) {
            T key = values[i];
            T value = values[i + 1];
            map.put(key, value);
        }

        return map;
    }
}