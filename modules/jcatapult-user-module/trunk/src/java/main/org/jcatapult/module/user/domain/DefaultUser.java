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
package org.jcatapult.module.user.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.jcatapult.user.domain.AbstractUser;

/**
 * <p>
 * This class is the main Jcatapult User object. It provides both a common set
 * of attributes that are usable for most applications as well as an inheritance
 * strategy for applications that need to add information to the user.
 * </p>
 *
 * <p>
 * This domain maps to the table named <strong>users</strong>. It also provides
 * the inheritance strategy of <strong>SINGLE_TABLE</strong> since this will be
 * the most common. Most applications will not have multiple user types and if
 * they do, they probably won't be using a pre-built module for them.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@Table(name="users")
@SuppressWarnings("unchecked")
public class DefaultUser extends AbstractUser<DefaultRole> {
    private static final long serialVersionUID = 1;

    // ------------------------------------- Name -------------------------------------

    @Column(name="company_name")
    private String companyName;

    private Name name = new Name();

    private PropertySupport properties = new PropertySupport();

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "user", fetch = FetchType.EAGER)
    private List<AuditableCreditCard> creditCards = new ArrayList<AuditableCreditCard>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Set<DefaultRole> roles = new HashSet<DefaultRole>();

    @OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.EAGER)
    @MapKey(name="type")
    private Map<String, Address> addresses = new HashMap<String, Address>();

    @OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.EAGER)
    @MapKey(name="type")
    @JoinTable(name = "users_phone_numbers", inverseJoinColumns = {@JoinColumn(name = "phone_numbers_id")})
    private Map<String, PhoneNumber> phoneNumbers = new HashMap<String, PhoneNumber>();

    @OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.EAGER)
    @MapKey(name="type")
    @JoinTable(name = "users_email_addresses", inverseJoinColumns = {@JoinColumn(name = "email_addresses_id")})
    private Map<String, EmailAddress> emailAddresses = new HashMap<String, EmailAddress>();



    // ------------------------------------- Name -------------------------------------

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    // ------------------------------------- Associations -------------------------------------

    public Map<String, Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Map<String, Address> addresses) {
        this.addresses = addresses;
    }

    /**
     * Adds the address to the mapping using the {@link Address#getType()} method for the key.
     *
     * @param   address The address to add.
     */
    public void addAddress(Address address) {
        addresses.put(address.getType(), address);
    }

    public Map<String, PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Map<String, PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    /**
     * Adds the phone number to the mapping using the {@link PhoneNumber#getType()} method for the key.
     *
     * @param   phoneNumber The phone number to add.
     */
    public void addPhoneNumber(PhoneNumber phoneNumber) {
        phoneNumbers.put(phoneNumber.getType(), phoneNumber);
    }

    public Map<String, EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(Map<String, EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    /**
     * Adds the email address to the mapping using the {@link EmailAddress#getType()} method for the key.
     *
     * @param   emailAddress The email address to add.
     */
    public void addEmailAddress(EmailAddress emailAddress) {
        emailAddresses.put(emailAddress.getType(), emailAddress);
    }

    public PropertySupport getProperties() {
        return properties;
    }

    public void setProperties(PropertySupport properties) {
        this.properties = properties;
    }

    public List<AuditableCreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<AuditableCreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    public void addCreditCard(AuditableCreditCard creditCard) {
        this.creditCards.add(creditCard);
    }

    public void removeCreditCard(AuditableCreditCard cc) {
        this.creditCards.remove(cc);
    }

    public Set<DefaultRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<DefaultRole> roles) {
        this.roles = roles;
    }

    public void addRole(DefaultRole role) {
        this.roles.add(role);
    }

    // ------------------ Helpers ------------------------

    /**
     * Convienence method to figure out if a user has the given role.
     *
     * @param   role The user role to check for.
     * @return  True if the user has it, false otherwise.
     */
    public boolean hasRole(String role) {
        for (DefaultRole r : roles) {
            if (r.getName().equals(role)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return  True if the user is an admin. This checks for the role named <strong>admin</strong>.
     */
    public boolean isAdmin() {
        return hasRole("admin");
    }
}