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
package org.jcatapult.module.user.service;

import static org.easymock.EasyMock.*;
import org.jcatapult.config.Configuration;
import org.jcatapult.module.user.domain.Address;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.domain.Name;
import org.jcatapult.module.user.domain.PhoneNumber;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.validation.ValidatorProvider;
import org.jcatapult.test.JCatapultBaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;
import net.java.error.ErrorList;

/**
 * <p>
 * This class tests the DefaultUserDAO.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultUserHandlerTest extends JCatapultBaseTest {
    @Inject public ExpressionEvaluator expressionEvaluator;
    @Inject public ValidatorProvider validatorProvider;

    @Test
    public void testValidate() {
        Configuration configuration = createStrictMock(Configuration.class);
        expect(configuration.getBoolean(DefaultUserConfiguration.NAME, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.NAME_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.BUSINESS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.BUSINESS_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.HOME_ADDRESS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.HOME_ADDRESS_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.WORK_ADDRESS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.WORK_ADDRESS_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.HOME_PHONE, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.HOME_PHONE_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.WORK_PHONE, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.WORK_PHONE_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.CELL_PHONE, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.CELL_PHONE_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.EMAIL_OPTIONS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.CAPTCHA, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.REGISTRATION_DISABLED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.VERIFY_EMAILS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.USERNAME_IS_EMAIL, true)).andReturn(true);
        expect(configuration.getString(DefaultUserConfiguration.REGISTRATION_SUCCESS_URI, "/")).andReturn("/");
        expect(configuration.getString(DefaultUserConfiguration.LOGOUT_SUCCESS_URI, "/")).andReturn("/");
        expect(configuration.getString(DefaultUserConfiguration.LOGIN_URI, "/login")).andReturn("/login");
        expect(configuration.getString(DefaultUserConfiguration.LOGIN_SUCCESS_URI, "/")).andReturn("/");
        expect(configuration.getString("jcatapult.user.default-role", "user")).andReturn("user");
        replay(configuration);

        DefaultUser newUserData = new DefaultUser();
        newUserData.setEmail("test@example.com");
        newUserData.setPassword("aaaaa");
        newUserData.setCompanyName("New");
        newUserData.setName(new Name());
        newUserData.getName().setFirstName("New");
        newUserData.getName().setLastName("New");
        newUserData.getAddresses().put("work", new Address("new street", "new city", "new state", null, "new country", "new postal", "work"));
        newUserData.getPhoneNumbers().put("work", new PhoneNumber("303-555-1111", "work"));

        DefaultUserHandler handler = new DefaultUserHandler(null, configuration, new DefaultUserConfiguration(configuration), expressionEvaluator, validatorProvider);
        ErrorList errors = handler.validate(newUserData, null, false, "password", "password");
        System.out.println("Errors are " + errors);
        assertTrue(errors.isEmpty());
        verify(configuration);
    }

    @Test
    public void testValidateFailure() {
        Configuration configuration = createStrictMock(Configuration.class);
        expect(configuration.getBoolean(DefaultUserConfiguration.NAME, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.NAME_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.BUSINESS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.BUSINESS_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.HOME_ADDRESS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.HOME_ADDRESS_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.WORK_ADDRESS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.WORK_ADDRESS_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.HOME_PHONE, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.HOME_PHONE_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.WORK_PHONE, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.WORK_PHONE_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.CELL_PHONE, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.CELL_PHONE_REQUIRED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.EMAIL_OPTIONS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.CAPTCHA, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.REGISTRATION_DISABLED, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.VERIFY_EMAILS, false)).andReturn(false);
        expect(configuration.getBoolean(DefaultUserConfiguration.USERNAME_IS_EMAIL, true)).andReturn(true);
        expect(configuration.getString(DefaultUserConfiguration.REGISTRATION_SUCCESS_URI, "/")).andReturn("/");
        expect(configuration.getString(DefaultUserConfiguration.LOGOUT_SUCCESS_URI, "/")).andReturn("/");
        expect(configuration.getString(DefaultUserConfiguration.LOGIN_URI, "/login")).andReturn("/login");
        expect(configuration.getString(DefaultUserConfiguration.LOGIN_SUCCESS_URI, "/")).andReturn("/");
        expect(configuration.getString("jcatapult.user.default-role", "user")).andReturn("user");
        replay(configuration);

        DefaultUser newUserData = new DefaultUser();
        newUserData.setPassword("bar");
        newUserData.setCompanyName("New");
        newUserData.setName(new Name());
        newUserData.getName().setFirstName("New");
        newUserData.getName().setLastName("New");
        newUserData.getAddresses().put("work", new Address("new street", null, "new state", null, null, "new postal", "work"));
        newUserData.getPhoneNumbers().put("work", new PhoneNumber("303-555-1111", "work"));

        DefaultUserHandler handler = new DefaultUserHandler(null, configuration, new DefaultUserConfiguration(configuration), expressionEvaluator, validatorProvider);
        ErrorList errors = handler.validate(newUserData, null, false, "p", "pc");
        System.out.println("Errors are " + errors);
        assertFalse(errors.isEmpty());
        assertEquals(5, errors.size());
        verify(configuration);
    }
}