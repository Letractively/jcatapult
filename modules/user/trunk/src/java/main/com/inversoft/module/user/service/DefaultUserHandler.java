/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.Arrays.asList;

import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.persistence.service.PersistenceService;

import com.google.inject.Inject;
import net.java.error.ErrorList;
import net.java.error.PropertyError;
import net.java.lang.StringTools;
import net.java.validate.EmailValidator;
import net.java.validate.RequiredValidator;
import net.java.validate.Validator;

import com.inversoft.module.user.domain.Address;
import com.inversoft.module.user.domain.AuditableCreditCard;
import com.inversoft.module.user.domain.DefaultRole;
import com.inversoft.module.user.domain.DefaultUser;
import com.inversoft.module.user.domain.PhoneNumber;
import com.inversoft.module.user.domain.Role;
import com.inversoft.module.user.domain.UserProperty;

/**
 * <p>
 * This implements the UserHandler for the Inversoft default domain
 * classes. These classes are {@link DefaultUser} and {@link DefaultRole}.
 * </p>
 *
 * @author Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class DefaultUserHandler<T extends DefaultUser, U extends DefaultRole> implements UserHandler<T, U> {
    private final UserConfiguration configuration;
    private final PersistenceService persistenceService;

    @Inject
    public DefaultUserHandler(UserConfiguration configuration, PersistenceService persistenceService) {
        this.configuration = configuration;
        this.persistenceService = persistenceService;
    }

    /**
     * {@inheritDoc}
     */
    public T createUser() {
        return (T) new DefaultUser();
    }

    /**
     * {@inheritDoc}
     */
    public Class<T> getUserType() {
        return (Class<T>) DefaultUser.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<U> getRoleType() {
        return (Class<U>) DefaultRole.class;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer[]> getDefaultAssociations() {
        return new HashMap<String, Integer[]>();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Role> getDefaultRoles() {
        DefaultRole role = persistenceService.queryFirst(getRoleType(),
            "select r from " + getRoleType().getSimpleName() + " r where r.name = ?1", "user");
        if (role == null) {
            return new HashSet<Role>();
        }

        return new HashSet<Role>(asList(role));
    }

    /**
     * {@inheritDoc}
     */
    public void associate(T user, Map<String, Integer[]> associations) {
        Integer[] roleIds = associations.get("roles");
        if (roleIds != null && roleIds.length > 0) {
            List<U> roles = persistenceService.queryAll(getRoleType(),
                "select r from " + getRoleType().getSimpleName() + " r where r.id in (?1)",
                Arrays.asList(roleIds));
            user.getRoles().addAll(roles);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ErrorList validate(T user, Map<String, Integer[]> associations, boolean existing, String password, String passwordConfirm) {
        boolean nameRequired = configuration.getDomainFlags().get(DefaultUserConfiguration.NAME_REQUIRED_FLAG);
        boolean businessRequired = configuration.getDomainFlags().get(DefaultUserConfiguration.BUSINESS_REQUIRED_FLAG);
        boolean homeAddressRequired = configuration.getDomainFlags().get(DefaultUserConfiguration.HOME_ADDRESS_REQUIRED_FLAG);
        boolean workAddressRequired = configuration.getDomainFlags().get(DefaultUserConfiguration.WORK_ADDRESS_REQUIRED_FLAG);
        boolean homePhoneRequired = configuration.getDomainFlags().get(DefaultUserConfiguration.HOME_PHONE_REQUIRED_FLAG);
        boolean workPhoneRequired = configuration.getDomainFlags().get(DefaultUserConfiguration.WORK_PHONE_REQUIRED_FLAG);
        boolean cellPhoneRequired = configuration.getDomainFlags().get(DefaultUserConfiguration.CELL_PHONE_REQUIRED_FLAG);

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
    public void prepare(T user) {
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
    public Map<String, Integer[]> getAssociationIds(T user) {
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