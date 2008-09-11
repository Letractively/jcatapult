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
public class YearsSelectTest extends AbstractInputTest {
    public YearsSelectTest() {
        super(true);
    }

    @Test
    public void testActionLess() {
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        YearsSelect select = new YearsSelect();
        select.setExpressionEvaluator(ee);
        run(select, null, "select", "foo.bar", "test", "Test",
            mapNV("name", "test", "class", "css-class", "bundle", "foo.bar"),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"test\" class=\"label\">Test</label></div>\n" +
            "<div class=\"control-container\">\n" +
            "  <select class=\"css-class\" id=\"test\" name=\"test\">\n" +
            "    <option value=\"2008\">2008</option>\n" +
            "    <option value=\"2009\">2009</option>\n" +
            "    <option value=\"2010\">2010</option>\n" +
            "    <option value=\"2011\">2011</option>\n" +
            "    <option value=\"2012\">2012</option>\n" +
            "    <option value=\"2013\">2013</option>\n" +
            "    <option value=\"2014\">2014</option>\n" +
            "    <option value=\"2015\">2015</option>\n" +
            "    <option value=\"2016\">2016</option>\n" +
            "    <option value=\"2017\">2017</option>\n" +
            "  </select>\n" +
            "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }

    @Test
    public void testAction() {
        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.expect(ee.getValue("year", action)).andReturn(2003);
        EasyMock.replay(ee);

        YearsSelect select = new YearsSelect();
        select.setExpressionEvaluator(ee);
        run(select, action, "select", "/test", "year", "Year",
            mapNV("name", "year", "class", "css-class", "startYear", 2001, "endYear", 2006),
            "<input type=\"hidden\" name=\"year@param\" value=\"param-value\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"label-container\"><label for=\"year\" class=\"label\">Year</label></div>\n" +
            "<div class=\"control-container\">\n" +
            "  <select class=\"css-class\" id=\"year\" name=\"year\">\n" +
            "    <option value=\"2001\">2001</option>\n" +
            "    <option value=\"2002\">2002</option>\n" +
            "    <option value=\"2003\" selected=\"selected\">2003</option>\n" +
            "    <option value=\"2004\">2004</option>\n" +
            "    <option value=\"2005\">2005</option>\n" +
            "    <option value=\"2006\">2006</option>\n" +
            "  </select>\n" +
            "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }
}