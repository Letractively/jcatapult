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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcatapult.config.Configuration;
import org.jcatapult.module.user.domain.Address;
import org.jcatapult.module.user.domain.AuditableCreditCard;
import org.jcatapult.module.user.domain.DefaultRole;
import org.jcatapult.module.user.domain.DefaultUser;
import org.jcatapult.module.user.domain.PhoneNumber;
import org.jcatapult.module.user.domain.UserProperty;
import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.user.service.AbstractUserHandler;

import com.google.inject.Inject;
import net.java.error.ErrorList;
import net.java.error.PropertyError;
import net.java.lang.StringTools;
import net.java.validate.EmailValidator;
import net.java.validate.RequiredValidator;
import net.java.validate.Validator;

/**
 * <p>
 * This implements the UserHandler for the Jcatapult default domain
 * classes. These classes are {@link DefaultUser} and {@link DefaultRole}.
 * </p>
 *
 * @author Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class DefaultUserHandler extends AbstractUserHandler<DefaultUser, DefaultRole> {
    private final UserConfiguration userConfiguration;

    @Inject
    public DefaultUserHandler(PersistenceService persistenceService, Configuration configuration,
            UserConfiguration userConfiguration) {
        super(persistenceService, configuration);
        this.userConfiguration = userConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultUser createUser() {
        return new DefaultUser();
    }

    /**
     * {@inheritDoc}
     */
    public Class<DefaultUser> getUserType() {
        return DefaultUser.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<DefaultRole> getRoleType() {
        return DefaultRole.class;
    }

    /**
     * {@inheritDoc}
     */
    public ErrorList validate(DefaultUser user, Map<String, int[]> associations, boolean existing, String password, String passwordConfirm) {
        boolean nameRequired = userConfiguration.getDomainFlags().get(DefaultUserConfiguration.NAME_REQUIRED_FLAG);
        boolean businessRequired = userConfiguration.getDomainFlags().get(DefaultUserConfiguration.BUSINESS_REQUIRED_FLAG);
        boolean homeAddressRequired = userConfiguration.getDomainFlags().get(DefaultUserConfiguration.HOME_ADDRESS_REQUIRED_FLAG);
        boolean workAddressRequired = userConfiguration.getDomainFlags().get(DefaultUserConfiguration.WORK_ADDRESS_REQUIRED_FLAG);
        boolean homePhoneRequired = userConfiguration.getDomainFlags().get(DefaultUserConfiguration.HOME_PHONE_REQUIRED_FLAG);
        boolean workPhoneRequired = userConfiguration.getDomainFlags().get(DefaultUserConfiguration.WORK_PHONE_REQUIRED_FLAG);
        boolean cellPhoneRequired = userConfiguration.getDomainFlags().get(DefaultUserConfiguration.CELL_PHONE_REQUIRED_FLAG);

        ErrorList errors = new ErrorList();

        // User properties
        if (!validate(new RequiredValidator(), user, "login", "required", errors)) {
            validate(new EmailValidator(), user, "login", "email", errors);
        }

        if (!existing || !StringTools.isTrimmedEmpty(password)) {
            if (StringTools.isTrimmedEmpty(password)) {
                errors.addError("password", "password.required");
            } else if (password.length() < 5) {
                errors.addError("password", "password.length");
            }
        }

        if (!StringTools.isTrimmedEmpty(password) && !password.equals(passwordConfirm)) {
            errors.addError("passwordConfirm", "passwordConfirm.match");
        }

        if (nameRequired) {
            validate(new RequiredValidator(), user, "name.firstName", "required", errors);
            validate(new RequiredValidator(), user, "name.lastName", "required", errors);
        }

        if (businessRequired) {
            validate(new RequiredValidator(), user, "companyName", "required", errors);
        }

        // Contact info, check the addresses
        if (user.getAddresses() != null) {
            Map<String, Address> addresses = user.getAddresses();
            for (String type : addresses.keySet()) {
                Address address = addresses.get(type);
                if (Address.isContainsData(address) || (homeAddressRequired && type.equals("home"))  ||
                        (workAddressRequired && type.equals("work"))) {
                    validate(new RequiredValidator(), user, "addresses['" + type + "'].street", "required", errors);
                    validate(new RequiredValidator(), user, "addresses['" + type + "'].city", "required", errors);
                    validate(new RequiredValidator(), user, "addresses['" + type + "'].country", "required", errors);

                    if (address.getCountry() != null && address.getCountry().equals("US")) {
                        validate(new RequiredValidator(), user, "addresses['" + type + "'].state", "required", errors);
                        validate(new RequiredValidator(), user, "addresses['" + type + "'].postalCode", "required", errors);
                    }
                }
            }
        }

        // Credit cards
        List<AuditableCreditCard> creditCards = user.getCreditCards();
        for (int i = 0; i < creditCards.size(); i++) {
            AuditableCreditCard creditCard = creditCards.get(i);
            if (creditCard.getExpirationDate() != null || creditCard.getExpirationMonth() != null ||
                    !StringTools.isEmpty(creditCard.getFirstName()) || !StringTools.isEmpty(creditCard.getLastName()) ||
                    !StringTools.isEmpty(creditCard.getNumber()) || !StringTools.isEmpty(creditCard.getSvn()) ||
                    Address.isContainsData(creditCard.getAddress())) {
                validate(new RequiredValidator(), user, "creditCard[" + i + "].firstName", "required", errors);
                validate(new RequiredValidator(), user, "creditCard[" + i + "].lastName", "required", errors);
                validate(new RequiredValidator(), user, "creditCard[" + i + "].expirationMonth", "required", errors);
                validate(new RequiredValidator(), user, "creditCard[" + i + "].expirationYear", "required", errors);
                validate(new RequiredValidator(), user, "creditCard[" + i + "].number", "required", errors);
                validate(new RequiredValidator(), user, "creditCard[" + i + "].address.street", "required", errors);
                validate(new RequiredValidator(), user, "creditCard[" + i + "].address.city", "required", errors);
                validate(new RequiredValidator(), user, "creditCard[" + i + "].address.country", "required", errors);

                if (creditCard.getAddress().getCountry() != null && creditCard.getAddress().getCountry().equals("US")) {
                    validate(new RequiredValidator(), user, "creditCard[" + i + "].address.state", "required", errors);
                    validate(new RequiredValidator(), user, "creditCard[" + i + "].address.postalCode", "required", errors);
                }
            }
        }

        // Check required phone numbers
        if (homePhoneRequired) {
            validate(new RequiredValidator(), user, "phoneNumbers['home'].number", "required", errors);
        }
        if (workPhoneRequired) {
            validate(new RequiredValidator(), user, "phoneNumbers['work'].number", "required", errors);
        }
        if (cellPhoneRequired) {
            validate(new RequiredValidator(), user, "phoneNumbers['cell'].number", "required", errors);
        }

        return errors;
    }

    private boolean validate(Validator validator, DefaultUser user, String property, String key,
            ErrorList errors, String... params) {
        Map<String, String> parameters = new HashMap<String, String>();
        for (int i = 0; i < params.length; i = i + 2) {
            String name = params[i];
            String value = params[i + 1];
            parameters.put(name, value);
        }

        Wrapper wrapper = new Wrapper(user);
        PropertyError error = validator.fetchAndValidate("user." + property, wrapper,
            "user." + property + "." + key,
            null, null, parameters);
        if (error != null) {
            errors.addError(error);
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void prepare(DefaultUser user) {
        // Prepare UserProperty's
        if (user.getProperties() != null) {
            Map<String, UserProperty> props = user.getProperties().getMap();
            for (String name : props.keySet()) {
                props.get(name).setUser(user);
            }
        }

        // Setup any types for addresses and phone numbers
        if (user.getAddresses() != null) {
            Map<String, Address> addresses = user.getAddresses();
            for (String type : addresses.keySet()) {
                addresses.get(type).setType(type);
            }

        }

        if (user.getPhoneNumbers() != null) {
            Map<String, PhoneNumber> phoneNumbers = user.getPhoneNumbers();
            for (String type : phoneNumbers.keySet()) {
                phoneNumbers.get(type).setType(type);
            }
        }
    }

    /**
     * Gets the roles only.
     *
     * @param   user The user to get the roles from.
     * @return  The Map that contains the role association IDs.
     */
    public Map<String, Integer[]> getAssociationIds(DefaultUser user) {
        Map<String, Integer[]> values = new HashMap<String, Integer[]>();
        List<Integer> rolesIds = new ArrayList<Integer>();
        for (Identifiable ident : user.getRoles()) {
            rolesIds.add(ident.getId());
        }
        values.put("roles", rolesIds.toArray(new Integer[rolesIds.size()]));
        return values;
    }

    public class Wrapper {
        private final DefaultUser user;

        private Wrapper(DefaultUser user) {
            this.user = user;
        }

        public DefaultUser getUser() {
            return user;
        }
    }
}