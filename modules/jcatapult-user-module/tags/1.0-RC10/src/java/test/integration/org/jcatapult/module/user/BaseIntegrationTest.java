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
package org.jcatapult.module.user;

import java.sql.SQLException;

import org.jcatapult.config.Configuration;
import org.jcatapult.config.EnvironmentAwareConfiguration;
import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.module.user.domain.Address;
import org.jcatapult.module.user.domain.DefaultRole;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.domain.Name;
import org.jcatapult.module.user.domain.PhoneNumber;
import org.jcatapult.module.user.service.DefaultUserConfiguration;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import org.jcatapult.test.MockConfiguration;
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
public class BaseIntegrationTest extends JPABaseTest {
    protected EnvironmentAwareConfiguration environmentAwareConfiguration;
    protected PersistenceService persistenceService;

    public BaseIntegrationTest() {
        EmailTestHelper.setup(this);
    }

    @Inject
    public void setServices(EnvironmentAwareConfiguration environmentAwareConfiguration, PersistenceService persistenceService) {
        this.environmentAwareConfiguration = environmentAwareConfiguration;
        this.persistenceService = persistenceService;
    }

    protected void clear() throws SQLException {
        clearTable("users_roles");
        clearTable("users_addresses");
        clearTable("addresses");
        clearTable("users_phone_numbers");
        clearTable("phone_numbers");
        clearTable("users");
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

    /**
     * Creates an User. This assumes that all relationship objects have seed values in the database.
     * If this isn't true, they should be created here.
     *
     * @param   login The user login.
     * @return  The User.
     */
    protected DefaultUser makeUser(String login) {
        DefaultUser user = new DefaultUser();
        user.setGuid("test-guid");
        user.setLogin(login);
        user.setPassword("test-password");
        user.setCompanyName("test company name");
        user.setName(new Name());
        user.getName().setFirstName("test first name");
        user.getName().setLastName("test first name");
        user.getAddresses().put("home", new Address("test street", "test city", "test state", null, "test country", "test postal", "home"));
        user.getAddresses().put("work", new Address("test street", "test city", "test state", null, "test country", "test postal", "work"));
        user.getPhoneNumbers().put("work", new PhoneNumber("303-555-5555", "work"));
        user.getPhoneNumbers().put("home", new PhoneNumber("303-555-5555", "home"));
        user.getPhoneNumbers().put("cell", new PhoneNumber("303-555-5555", "cell"));
        user.addRole(persistenceService.findById(DefaultRole.class, 1));

        persistenceService.persist(user);
        return user;
    }
}