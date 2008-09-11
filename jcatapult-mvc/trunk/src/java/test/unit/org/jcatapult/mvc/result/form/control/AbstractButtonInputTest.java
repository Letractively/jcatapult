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

import java.util.Map;

import org.easymock.EasyMock;
import org.example.action.user.Edit;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.junit.Ignore;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This is an abstract test that is used to test button controls.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public abstract class AbstractButtonInputTest extends AbstractInputTest {
    public AbstractButtonInputTest() {
        super(false);
    }

    /**
     * Makes a control for testing.
     *
     * @param   ee The expression evaluator.
     * @return  The Control.
     */
    protected abstract AbstractButtonInput getControl(ExpressionEvaluator ee);

    /**
     * @return  The input type.
     */
    protected abstract String getType();

    @Test
    public void testActionLess() {
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        AbstractButtonInput input = getControl(ee);
        run(input, null, getType(), "foo.bar", "test", "Test",
            params("name", "test", "value", "test-value", "class", "css-class", "bundle", "foo.bar"),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<input type=\"hidden\" name=\"__jc_a_test\" value=\"\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">" + tag() + "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }

    @Test
    public void testAction() {
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        AbstractButtonInput input = getControl(ee);
        run(input, new Edit(), getType(), "/test", "test", "Test",
            params("name", "test", "value", "test-value", "class", "css-class"),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<input type=\"hidden\" name=\"__jc_a_test\" value=\"\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">" + tag() + "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }

    @Test
    public void testActionAttribute() {
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        AbstractButtonInput input = getControl(ee);
        run(input, new Edit(), getType(), "/test", "test", "Test",
            params("name", "test", "action", "/foo", "value", "test-value", "class", "css-class"),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<input type=\"hidden\" name=\"__jc_a_test\" value=\"/foo\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\">" + tag() + "</div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }

    private Map<String, Object> params(String... args) {
        Map<String, Object> map = mapNV(args);
        if (getType().equals("image")) {
            map.put("src", "foo.jpg");
        }

        return map;
    }

    private String tag() {
        return "<input type=\"" + getType() + "\" class=\"css-class\" id=\"test\" name=\"test\" " +
            (getType().equals("image") ? "src=\"foo.jpg\" " : "")  + "value=\"Test\"/>";
    }
}