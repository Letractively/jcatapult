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
import org.junit.Ignore;

/**
 * <p>
 * This is an abstract test that is used to test checked controls.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public abstract class AbstractCheckedInputTest extends AbstractInputTest {
    protected AbstractCheckedInputTest() {
        super(true);
    }

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
}