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
 *
 */
package org.jcatapult.mvc.test.junit;

import org.jcatapult.guice.GuiceContainer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * <p>
 * This class is a JUnit 4 base test for MVC testing.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class WebBaseTest {
    /**
     * A @BeforeClass method that sets up the GuiceContainer.
     */
    @BeforeClass
    public static void setupGuiceContainer() {
        GuiceContainer.inject();
        GuiceContainer.initialize();
    }

    /**
     * A @Before method that injects this class.
     */
    @Before
    public void inject() {
        GuiceContainer.getInjector().injectMembers(this);
    }
}