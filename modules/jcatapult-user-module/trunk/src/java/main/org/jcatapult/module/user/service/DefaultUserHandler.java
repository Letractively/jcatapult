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

import java.lang.annotation.Annotation;
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
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.validation.Validator;
import org.jcatapult.mvc.validation.ValidatorProvider;
import org.jcatapult.mvc.validation.annotation.Email;
import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.user.service.AbstractUserHandler;

import com.google.inject.Inject;
import net.java.error.ErrorList;
import net.java.lang.StringTools;

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
    private final ExpressionEvaluator expressionEvaluator;
    private final ValidatorProvider validatorProvider;

    @Inject
    public DefaultUserHandler(PersistenceService persistenceService, Configuration configuration,
            UserConfiguration userConfiguration, ExpressionEvaluator expressionEvaluator,
            ValidatorProvider validatorProvider) {
        super(persistenceService, configuration);
        this.userConfiguration = userConfiguration;
        this.expressionEvaluator = expressionEvaluator;
        this.validatorProvider = validatorProvider; 
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

        // User properties. If the username and email are the same, just validate the username as an
        // email. Otherwise, validate both and the email as an email.
        if (userConfiguration.isUsernameSameAsEmail()) {
            if (!validate(Required.class, user, "username", errors)) {
                validate(Email.class, user, "username", errors);
            }
        } else {
            validate(Required.class, user, "username", errors);
            if (!validate(Required.class, user, "email", errors)) {
                validate(Email.class, user, "username", errors);
            }
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
            validate(Required.class, user, "name.firstName", errors);
            validate(Required.class, user, "name.lastName", errors);
        }

        if (businessRequired) {
            validate(Required.class, user, "companyName", errors);
        }

        // Contact info, check the addresses
        if (user.getAddresses() != null) {
            Map<String, Address> addresses = user.getAddresses();
            for (String type : addresses.keySet()) {
                Address address = addresses.get(type);
                if (Address.isContainsData(address) || (homeAddressRequired && type.equals("home"))  ||
                        (workAddressRequired && type.equals("work"))) {
                    validate(Required.class, user, "addresses['" + type + "'].street", errors);
                    validate(Required.class, user, "addresses['" + type + "'].city", errors);
                    validate(Required.class, user, "addresses['" + type + "'].country", errors);

                    if (address.getCountry() != null && address.getCountry().equals("US")) {
                        validate(Required.class, user, "addresses['" + type + "'].state", errors);
                        validate(Required.class, user, "addresses['" + type + "'].postalCode", errors);
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
                validate(Required.class, user, "creditCard[" + i + "].firstName", errors);
                validate(Required.class, user, "creditCard[" + i + "].lastName", errors);
                validate(Required.class, user, "creditCard[" + i + "].expirationMonth", errors);
                validate(Required.class, user, "creditCard[" + i + "].expirationYear", errors);
                validate(Required.class, user, "creditCard[" + i + "].number", errors);
                validate(Required.class, user, "creditCard[" + i + "].address.street", errors);
                validate(Required.class, user, "creditCard[" + i + "].address.city", errors);
                validate(Required.class, user, "creditCard[" + i + "].address.country", errors);

                if (creditCard.getAddress().getCountry() != null && creditCard.getAddress().getCountry().equals("US")) {
                    validate(Required.class, user, "creditCard[" + i + "].address.state", errors);
                    validate(Required.class, user, "creditCard[" + i + "].address.postalCode", errors);
                }
            }
        }

        // Check required phone numbers
        if (homePhoneRequired) {
            validate(Required.class, user, "phoneNumbers['home'].number", errors);
        }
        if (workPhoneRequired) {
            validate(Required.class, user, "phoneNumbers['work'].number", errors);
        }
        if (cellPhoneRequired) {
            validate(Required.class, user, "phoneNumbers['cell'].number", errors);
        }

        return errors;
    }

    private boolean validate(Class<? extends Annotation> type, DefaultUser user, String property, ErrorList errors) {
        Object value = expressionEvaluator.getValue(property, user);
        Validator validator = validatorProvider.lookup(type);
        if (!validator.validate(null, null, value)) {
            errors.addError("user." + property, "user." + property + "." + type.getSimpleName().toLowerCase());
            return false;
        }

        return true;
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
}