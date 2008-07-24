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
 * This tests the image control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ImageTest extends AbstractButtonInputTest {
    protected AbstractButtonInput getControl(ExpressionEvaluator ee) {
        return new Image();
    }

    protected String getType() {
        return "image";
    }

    @Test
    public void testIsmap() {
        Edit action = new Edit();
        ExpressionEvaluator ee = EasyMock.createStrictMock(ExpressionEvaluator.class);
        EasyMock.replay(ee);

        AbstractButtonInput input = getControl(ee);
        run(input, action, getType(), "foo.bar", "test", "Test",
            mapNV("name", "test", "value", "test-value", "class", "css-class", "bundle", "foo.bar", "ismap", true),
            "<input type=\"hidden\" name=\"test@param\" value=\"param-value\"/>\n" +
            "<input type=\"hidden\" name=\"__jc_a_test\" value=\"\"/>\n" +
            "<div class=\"input\">\n" +
            "<div class=\"control-container\"><input type=\"" + getType() + "\" class=\"css-class\" id=\"test\" ismap=\"ismap\" name=\"test\" value=\"Test\"/></div>\n" +
            "</div>\n");

        EasyMock.verify(ee);
    }
}