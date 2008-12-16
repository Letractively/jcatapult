/*
 * Copyright (c) 2007, Inversoft LLC, All Rights Reserved
 */
package org.jcatapult.module.user;

import java.sql.SQLException;

import org.jcatapult.config.Configuration;
import org.jcatapult.config.EnvironmentAwareConfiguration;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.spi.SecurityContextProvider;
import org.jcatapult.test.MockConfiguration;
import org.jcatapult.email.EmailTestHelper;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.Inject;

import org.jcatapult.module.user.domain.Address;
import org.jcatapult.module.user.domain.DefaultRole;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.domain.Name;
import org.jcatapult.module.user.domain.PhoneNumber;
import org.jcatapult.module.user.service.DefaultUserConfiguration;

/**
 * <p>
 * This class is the base test case for the Inversoft User module.
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
        user.setCompanyName("test company name");
        user.setName(new Name());
        user.getName().setFirstName("test first name");
        user.getName().setLastName("test first name");
        user.getAddresses().put("work", new Address("test street", "test city", "test state", null, "test country", "test postal", "work"));
        user.getPhoneNumbers().put("work", new PhoneNumber("303-555-5555", "work"));
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