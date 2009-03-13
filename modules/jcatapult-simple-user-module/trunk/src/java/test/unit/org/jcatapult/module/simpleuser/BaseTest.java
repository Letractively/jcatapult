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
package org.jcatapult.module.simpleuser;

import java.sql.SQLException;

import org.jcatapult.config.Configuration;
import org.jcatapult.config.EnvironmentAwareConfiguration;
import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.module.simpleuser.domain.DefaultRole;
import org.jcatapult.module.simpleuser.domain.DefaultUser;
import org.jcatapult.module.simpleuser.service.DefaultUserConfiguration;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.spi.SecurityContextProvider;
import org.jcatapult.test.MockConfiguration;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the base test case for the Jcatapult User module.
 * It sets up the security context.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class BaseTest extends JPABaseTest {
    protected PersistenceService persistenceService;
    protected String username = "test";
    private EnvironmentAwareConfiguration environmentAwareConfiguration;

    public BaseTest() {
        EmailTestHelper.setup(this);
    }

    @Inject
    public void setServices(PersistenceService ps, EnvironmentAwareConfiguration environmentAwareConfiguration) {
        this.persistenceService = ps;
        this.environmentAwareConfiguration = environmentAwareConfiguration;
    }

    @Before
    public void setupSecurityContext() {
        SecurityContext.setProvider(new SecurityContextProvider() {
            public String getCurrentUsername() {
                return username;
            }

            public Object getCurrentUser() {
                throw new AssertionError();
            }
        });
    }

    protected void clear() throws SQLException {
        clearTable("users_roles");
        clearTable("users_addresses");
        clearTable("users_email_addresses");
        clearTable("addresses");
        clearTable("email_addresses");
        clearTable("users_phone_numbers");
        clearTable("phone_numbers");
        clearTable("users");
    }

    /**
     * Creates an User. This assumes that all relationship objects have seed values in the database.
     * If this isn't true, they should be created here.
     *
     * @param   login The user login.
     * @return  The User.
     */
    protected DefaultUser makeUser(String login) {
        DefaultUser user = new DefaultUser();
        user.setGuid("test guid");
        user.setLogin(login);
        user.setPassword("test password");
        user.addRole(persistenceService.findById(DefaultRole.class, 1));

        persistenceService.persist(user);
        return user;
    }

    /**
     * Makes a simple configuration where everything is enabled.
     *
     * @param   registrationDisabled Determines if registration is disabled or not.
     * @return  The configuration mock.
     */
    protected Configuration makeConfiguration(boolean registrationDisabled) {
        MockConfiguration configuration = new MockConfiguration(environmentAwareConfiguration);
        configuration.addParameter(DefaultUserConfiguration.REGISTRATION_DISABLED, registrationDisabled);
        return configuration;
    }
}