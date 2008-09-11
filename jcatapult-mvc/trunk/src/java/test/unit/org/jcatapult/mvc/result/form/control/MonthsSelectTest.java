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

import org.easymock.EasyMock;
import org.example.action.user.Edit;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This tests the months select control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class MonthsSelectTest extends AbstractInputTest {
    public MonthsSelectTest() {
        super(true);
    }

    @Test
    public void testActionLess() {
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        MonthsSelect select = new MonthsSelect();
        select.setExpressionEvaluator(ee);
        run(select, null, "select", "foo.bar", "test", "Test",
            mapNV("name", "test", "class", "css-class", "bundle", "foo.bar"),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"control-container\">\n" +
            "  <select class=\"css-class\" id=\"test\" name=\"test\">\n" +
            "    <option value=\"1\">January</option>\n" +
            "    <option value=\"2\">February</option>\n" +
            "    <option value=\"3\">March</option>\n" +
            "    <option value=\"4\">April</option>\n" +
            "    <option value=\"5\">May</option>\n" +
            "    <option value=\"6\">June</option>\n" +
            "    <option value=\"7\">July</option>\n" +
            "    <option value=\"8\">August</option>\n" +
            "    <option value=\"9\">September</option>\n" +
            "    <option value=\"10\">October</option>\n" +
            "    <option value=\"11\">November</option>\n" +
            "    <option value=\"12\">December</option>\n" +
            "  </select>\n" +
            "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }

    @Test
    public void testAction() {
        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("month", action)).andReturn(5);
        EasyMock.replay(ee);

        MonthsSelect select = new MonthsSelect();
        select.setExpressionEvaluator(ee);
        run(select, action, "select", "/test", "month", "Month",
            mapNV("name", "month", "class", "css-class"),
            "<input type=\"hidden\" name=\"month@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"month\" class=\"label\">Month</label></div>\n" +
            "<div class=\"control-container\">\n" +
            "  <select class=\"css-class\" id=\"month\" name=\"month\">\n" +
            "    <option value=\"1\">January</option>\n" +
            "    <option value=\"2\">February</option>\n" +
            "    <option value=\"3\">March</option>\n" +
            "    <option value=\"4\">April</option>\n" +
            "    <option value=\"5\" selected=\"selected\">May</option>\n" +
            "    <option value=\"6\">June</option>\n" +
            "    <option value=\"7\">July</option>\n" +
            "    <option value=\"8\">August</option>\n" +
            "    <option value=\"9\">September</option>\n" +
            "    <option value=\"10\">October</option>\n" +
            "    <option value=\"11\">November</option>\n" +
            "    <option value=\"12\">December</option>\n" +
            "  </select>\n" +
            "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }
}