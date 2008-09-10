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

import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;

/**
 * <p>
 * This tests the button control.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ButtonTest extends AbstractButtonInputTest {
    protected AbstractButtonInput getControl(ExpressionEvaluator ee) {
        return new Button();
    }

    protected String getType() {
        return "button";
    }
}