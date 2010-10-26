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
package org.jcatapult.persistence;

import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import org.junit.Ignore;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the base test for jcatapult core to setup the test authentication
 * service.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Ignore
public class PersistenceBaseTest extends JPABaseTest {
    @Inject public PersistenceService persistenceService;
}
