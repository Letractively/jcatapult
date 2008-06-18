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
package org.jcatapult.mvc;

import org.jcatapult.mvc.servlet.MVCWorkflow;
import org.jcatapult.mvc.test.junit.WebBaseTest;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the MVC from a high level perspective.
 * </p>
 *
 * This doesn't work probably because of the request, response and context being called multiple times
 * for the locale and other values. Probably need to mock those out.
 *
 * @author Brian Pontarelli
 */
@Ignore
public class GlobalTest extends WebBaseTest {
    private MVCWorkflow workflow;

    @Inject
    public void setWorkflow(MVCWorkflow workflow) {
        this.workflow = workflow;
    }

    @Test
    public void testWorkflowCall() {

    }
}