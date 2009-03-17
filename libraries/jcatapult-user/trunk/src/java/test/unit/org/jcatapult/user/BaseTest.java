/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.user;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;
import org.jcatapult.config.Configuration;
import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import org.jcatapult.user.service.UserHandler;
import org.jcatapult.user.service.UserService;
import static org.junit.Assert.*;
import org.junit.Ignore;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

/**
 * <p>
 * This is a base test.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class BaseTest extends JPABaseTest {
    @Inject public UserService userService;
    @Inject public PersistenceService ps;

    public BaseTest() {
        setupConfigurationAndUserHandlerAndEmail();
    }

    protected void setupConfigurationAndUserHandlerAndEmail() { 
        final Configuration config = createNiceMock(Configuration.class);
        expect(config.getString("jcatapult.user.default-role", "user")).andReturn("user");
        expect(config.getBoolean("jcatapult.user.logins-are-emails", true)).andReturn(true);
        expect(config.getBoolean("jcatapult.user.verify-emails", false)).andReturn(false);
        replay(config);

        addModules(new AbstractModule() {
            public void configure() {
                bind(UserHandler.class).to(TestUserHandler.class);
                bind(Configuration.class).toInstance(config);
            }
        });

        EmailTestHelper.setup(this);
    }

    /**
     * Creates an User with roles.
     *
     * @param   login The user login.
     * @return  The User.
     */
    protected TestUser makeUser(String login) {
        TestUser user = new TestUser();
        user.setLogin(login);

        int adminID = ps.queryFirst(TestRole.class, "select tr from TestRole tr where tr.name = 'admin'").getId();
        int userID = ps.queryFirst(TestRole.class, "select tr from TestRole tr where tr.name = 'user'").getId();

        Map<String, int[]> associations = new HashMap<String, int[]>();
        associations.put("roles", new int[]{adminID, userID});

        assertTrue(userService.persist(user, associations, "password"));
        return user;
    }

    protected void clear() throws SQLException {
        clearTable("test_user_test_role");
        clearTable("test_user");
    }
}
