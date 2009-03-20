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
package org.jcatapult.module.cms;

import java.sql.SQLException;

import org.jcatapult.module.cms.service.DefaultContentService;
import org.jcatapult.module.simpleuser.domain.DefaultRole;
import org.jcatapult.module.simpleuser.domain.DefaultUser;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.Inject;

/**
 * <p>
 * This is a base test that provides database setup for the tests.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Ignore
public class BaseTest extends JPABaseTest {
    @Inject public DefaultContentService service;
    @Inject public PersistenceService persistenceService;
    protected DefaultUser publisher;
    protected DefaultUser editor;

    @Before
    public void setupDatabase() throws SQLException {
        publisher = new DefaultUser();
        publisher.setId(1);
        publisher.addRole(new DefaultRole("publisher"));

        editor = new DefaultUser();
        editor.setId(1);
        editor.addRole(new DefaultRole("editor"));
    }
}