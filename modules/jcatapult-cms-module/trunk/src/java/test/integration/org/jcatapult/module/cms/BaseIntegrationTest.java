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

import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.module.cms.service.ContentService;
import org.jcatapult.module.user.domain.DefaultRole;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.Inject;

/**
 * <p>
 * This is a base integration test.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class BaseIntegrationTest extends JPABaseTest {
    @Inject protected ContentService contentService;
    @Inject protected PersistenceService persistenceService;
    protected DefaultUser publisher;
    protected DefaultUser editor;

    public BaseIntegrationTest() {
        EmailTestHelper.setup(this);
    }

    @Before
    public void setupDatabase() throws SQLException {
//        clearTable("cms_node_action_states");
//        clearTable("cms_node_actions");
//        clearTable("cms_node_states");
//        clearTable("cms_content_states");
//        clearTable("cms_locale_contents");
//        clearTable("cms_contents");
//        clearTable("cms_nodes");
//
//        clearTable("users_roles");
//        clearTable("users");

        publisher = persistenceService.queryFirst(DefaultUser.class, "select u from DefaultUser u where u.login = 'publisher'");
        if (publisher == null) {
            publisher = new DefaultUser();
            publisher.setLogin("publisher");
            publisher.setPassword("password");
            publisher.addRole(persistenceService.queryFirst(DefaultRole.class, "select r from DefaultRole r where r.name = 'publisher'"));
            persistenceService.persist(publisher);
        }

        editor = persistenceService.queryFirst(DefaultUser.class, "select u from DefaultUser u where u.login = 'editor'");
        if (editor == null) {
            editor = new DefaultUser();
            editor.setLogin("editor");
            editor.setPassword("password");
            editor.addRole(persistenceService.queryFirst(DefaultRole.class, "select r from DefaultRole r where r.name = 'editor'"));
            persistenceService.persist(editor);
        }
    }
}