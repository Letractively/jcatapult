/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.service;

import org.easymock.EasyMock;
import org.jcatapult.config.Configuration;
import static org.junit.Assert.*;
import org.junit.Test;

import net.java.error.ErrorList;

import org.jcatapult.module.user.domain.Address;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.domain.Name;
import org.jcatapult.module.user.domain.PhoneNumber;

/**
 * <p>
 * This class tests the DefaultUserDAO.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultUserHandlerTest {
    @Test
    public void testValidate() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.NAME, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.NAME_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.BUSINESS, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.BUSINESS_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.HOME_ADDRESS, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.HOME_ADDRESS_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.WORK_ADDRESS, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.WORK_ADDRESS_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.HOME_PHONE, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.HOME_PHONE_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.WORK_PHONE, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.WORK_PHONE_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.CELL_PHONE, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.CELL_PHONE_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.EMAIL_OPTIONS, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.REGISTRATION_DISABLED, false)).andReturn(false);
        EasyMock.expect(configuration.getString(DefaultUserConfiguration.REGISTRATION_SUCCESS_URI, "/")).andReturn("/");
        EasyMock.expect(configuration.getString(DefaultUserConfiguration.LOGOUT_SUCCESS_URI, "/")).andReturn("/");
        EasyMock.expect(configuration.getString(DefaultUserConfiguration.LOGIN_URI, "/login")).andReturn("/login");
        EasyMock.expect(configuration.getString(DefaultUserConfiguration.LOGIN_SUCCESS_URI, "/")).andReturn("/");
        EasyMock.replay(configuration);

        DefaultUser newUserData = new DefaultUser();
        newUserData.setLogin("test@example.com");
        newUserData.setPassword("aaaaa");
        newUserData.setPasswordConfirm("aaaaa");
        newUserData.setCompanyName("New");
        newUserData.setName(new Name());
        newUserData.getName().setFirstName("New");
        newUserData.getName().setLastName("New");
        newUserData.getAddresses().put("work", new Address("new street", "new city", "new state", null, "new country", "new postal", "work"));
        newUserData.getPhoneNumbers().put("work", new PhoneNumber("303-555-1111", "work"));

        DefaultUserHandler handler = new DefaultUserHandler(new DefaultUserConfiguration(configuration), null);
        ErrorList errors = handler.validate(newUserData, null, false, "password", "password");
        System.out.println("Errors are " + errors);
        assertTrue(errors.isEmpty());
        EasyMock.verify(configuration);
    }

    @Test
    public void testValidateFailure() {
        Configuration configuration = EasyMock.createStrictMock(Configuration.class);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.NAME, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.NAME_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.BUSINESS, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.BUSINESS_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.HOME_ADDRESS, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.HOME_ADDRESS_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.WORK_ADDRESS, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.WORK_ADDRESS_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.HOME_PHONE, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.HOME_PHONE_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.WORK_PHONE, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.WORK_PHONE_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.CELL_PHONE, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.CELL_PHONE_REQUIRED, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.EMAIL_OPTIONS, false)).andReturn(false);
        EasyMock.expect(configuration.getBoolean(DefaultUserConfiguration.REGISTRATION_DISABLED, false)).andReturn(false);
        EasyMock.expect(configuration.getString(DefaultUserConfiguration.REGISTRATION_SUCCESS_URI, "/")).andReturn("/");
        EasyMock.expect(configuration.getString(DefaultUserConfiguration.LOGOUT_SUCCESS_URI, "/")).andReturn("/");
        EasyMock.expect(configuration.getString(DefaultUserConfiguration.LOGIN_URI, "/login")).andReturn("/login");
        EasyMock.expect(configuration.getString(DefaultUserConfiguration.LOGIN_SUCCESS_URI, "/")).andReturn("/");
        EasyMock.replay(configuration);

        DefaultUser newUserData = new DefaultUser();
        newUserData.setLogin(null);
        newUserData.setPassword("bar");
        newUserData.setPasswordConfirm("foo");
        newUserData.setCompanyName("New");
        newUserData.setName(new Name());
        newUserData.getName().setFirstName("New");
        newUserData.getName().setLastName("New");
        newUserData.getAddresses().put("work", new Address("new street", null, "new state", null, null, "new postal", "work"));
        newUserData.getPhoneNumbers().put("work", new PhoneNumber("303-555-1111", "work"));

        DefaultUserHandler handler = new DefaultUserHandler(new DefaultUserConfiguration(configuration), null);
        ErrorList errors = handler.validate(newUserData, null, false, "p", "pc");
        System.out.println("Errors are " + errors);
        assertFalse(errors.isEmpty());
        assertEquals(5, errors.size());
        EasyMock.verify(configuration);
    }
}