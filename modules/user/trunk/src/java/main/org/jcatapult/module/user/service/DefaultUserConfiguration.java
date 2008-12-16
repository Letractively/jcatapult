/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.service;

import java.util.HashMap;
import java.util.Map;

import org.jcatapult.config.Configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * This class implements the configuration service and loads up the default
 * configuration flags that control the forms and validation logic for the
 * default user and role Classes.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultUserConfiguration implements UserConfiguration {
    public static final String NAME_FLAG = "name";
    public static final String NAME_REQUIRED_FLAG = "nameRequired";
    public static final String BUSINESS_FLAG = "business";
    public static final String BUSINESS_REQUIRED_FLAG = "businessRequired";
    public static final String HOME_ADDRESS_FLAG = "homeAddress";
    public static final String HOME_ADDRESS_REQUIRED_FLAG = "homeAddressRequired";
    public static final String WORK_ADDRESS_FLAG = "workAddress";
    public static final String WORK_ADDRESS_REQUIRED_FLAG = "workAddressRequired";
    public static final String HOME_PHONE_FLAG = "homePhone";
    public static final String HOME_PHONE_REQUIRED_FLAG = "homePhoneRequired";
    public static final String WORK_PHONE_FLAG = "workPhone";
    public static final String WORK_PHONE_REQUIRED_FLAG = "workPhoneRequired";
    public static final String CELL_PHONE_FLAG = "cellPhone";
    public static final String CELL_PHONE_REQUIRED_FLAG = "cellPhoneRequired";
    public static final String EMAIL_OPTIONS_FLAG = "emailOptions";

    /* Form constants */
    public static final String NAME = "inversoft.modules.user.fields.name";
    public static final String NAME_REQUIRED = "inversoft.modules.user.fields.name-required";
    public static final String BUSINESS = "inversoft.modules.user.fields.business";
    public static final String BUSINESS_REQUIRED = "inversoft.modules.user.fields.business-required";
    public static final String HOME_ADDRESS = "inversoft.modules.user.fields.home-address";
    public static final String HOME_ADDRESS_REQUIRED = "inversoft.modules.user.fields.home-address-required";
    public static final String WORK_ADDRESS = "inversoft.modules.user.fields.work-address";
    public static final String WORK_ADDRESS_REQUIRED = "inversoft.modules.user.fields.work-address-required";
    public static final String HOME_PHONE = "inversoft.modules.user.fields.home-phone";
    public static final String HOME_PHONE_REQUIRED = "inversoft.modules.user.fields.home-phone-required";
    public static final String WORK_PHONE = "inversoft.modules.user.fields.work-phone";
    public static final String WORK_PHONE_REQUIRED = "inversoft.modules.user.fields.work-phone-required";
    public static final String CELL_PHONE = "inversoft.modules.user.fields.cell-phone";
    public static final String CELL_PHONE_REQUIRED= "inversoft.modules.user.fields.cell-phone-required";
    public static final String EMAIL_OPTIONS = "inversoft.modules.user.fields.email-options";

    /* Registration configureation */
    public static final String REGISTRATION_DISABLED = "inversoft.modules.user.register.disabled";
    public static final String REGISTRATION_SUCCESS_URI = "inversoft.modules.user.register.success-uri";
    public static final String LOGOUT_SUCCESS_URI = "inversoft.modules.user.logout.success-uri";
    public static final String LOGIN_SUCCESS_URI = "inversoft.modules.user.login.success-uri";
    public static final String LOGIN_URI = "inversoft.modules.user.login.uri";

    private final Map<String, Boolean> flags = new HashMap<String, Boolean>();
    private final String registrationSuccessURI;
    private final String logoutURI;
    private final String loginURI;
    private final String loginSuccessURI;

    @Inject
    public DefaultUserConfiguration(Configuration configuration) {
        flags.put(NAME_FLAG, configuration.getBoolean(NAME, false));
        flags.put(NAME_REQUIRED_FLAG, configuration.getBoolean(NAME_REQUIRED, false));
        flags.put(BUSINESS_FLAG, configuration.getBoolean(BUSINESS, false));
        flags.put(BUSINESS_REQUIRED_FLAG, configuration.getBoolean(BUSINESS_REQUIRED, false));
        flags.put(HOME_ADDRESS_FLAG, configuration.getBoolean(HOME_ADDRESS, false));
        flags.put(HOME_ADDRESS_REQUIRED_FLAG, configuration.getBoolean(HOME_ADDRESS_REQUIRED, false));
        flags.put(WORK_ADDRESS_FLAG, configuration.getBoolean(WORK_ADDRESS, false));
        flags.put(WORK_ADDRESS_REQUIRED_FLAG, configuration.getBoolean(WORK_ADDRESS_REQUIRED, false));
        flags.put(HOME_PHONE_FLAG, configuration.getBoolean(HOME_PHONE, false));
        flags.put(HOME_PHONE_REQUIRED_FLAG, configuration.getBoolean(HOME_PHONE_REQUIRED, false));
        flags.put(WORK_PHONE_FLAG, configuration.getBoolean(WORK_PHONE, false));
        flags.put(WORK_PHONE_REQUIRED_FLAG, configuration.getBoolean(WORK_PHONE_REQUIRED, false));
        flags.put(CELL_PHONE_FLAG, configuration.getBoolean(CELL_PHONE, false));
        flags.put(CELL_PHONE_REQUIRED_FLAG, configuration.getBoolean(CELL_PHONE_REQUIRED, false));
        flags.put(EMAIL_OPTIONS_FLAG, configuration.getBoolean(EMAIL_OPTIONS, false));

        // Registration
        flags.put(REGISTRATION_DISABLED, configuration.getBoolean(REGISTRATION_DISABLED, false));
        this.registrationSuccessURI = configuration.getString(REGISTRATION_SUCCESS_URI, "/");

        // Logout
        this.logoutURI = configuration.getString(LOGOUT_SUCCESS_URI, "/");

        // Login
        this.loginURI = configuration.getString(LOGIN_URI, "/login");
        this.loginSuccessURI = configuration.getString(LOGIN_SUCCESS_URI, "/");
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Boolean> getDomainFlags() {
        return flags;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRegistrationDisabled() {
        return flags.get(REGISTRATION_DISABLED);
    }

    /**
     * {@inheritDoc}
     */
    public String getRegistrationSuccessURI() {
        return registrationSuccessURI;
    }

    /**
     * {@inheritDoc}
     */
    public String getLogoutURI() {
        return logoutURI;
    }

    /**
     * {@inheritDoc}
     */
    public String getLogingURI() {
        return loginURI;
    }

    /**
     * {@inheritDoc}
     */
    public String getLogingSuccessURI() {
        return loginSuccessURI;
    }
}