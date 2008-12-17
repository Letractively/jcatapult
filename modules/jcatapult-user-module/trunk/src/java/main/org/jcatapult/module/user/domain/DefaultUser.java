/*
 * Copyright (c) 2007, Inversoft LLC, All Rights Reserved
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
import javax.persistence.Transient;

import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.persistence.domain.SoftDeletableImpl;

/**
 * <p>
 * This class is the main Inversoft User object. It provides both a common set
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
public class DefaultUser extends SoftDeletableImpl implements User {
    private static final long serialVersionUID = 1;

    // ------------------------------------- Security -------------------------------------

    @Required
    @Column(unique = true, nullable = false)
    private String login;

    @Required
    @Column(nullable = false)
    private String password;

    @Required
    @Transient
    private String passwordConfirm;

    @Column()
    private String guid;

    @Column(nullable = false)
    private boolean expired = false;

    @Column(nullable = false)
    private boolean locked = false;

    @Column(name = "password_expired", nullable = false)
    private boolean passwordExpired = false;

    private boolean partial;

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


    // ------------------------------------- Security -------------------------------------

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Sets in the password.
     *
     * @param   password The new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * Sets in the password confirmation.
     *
     * @param   passwordConfirm The new password confirmation.
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    /**
     * @return  True if the account only contains partial information and needs more information from
     *          the user before it can be fully used. These types of accounts are useful when someone
     *          makes a purchase as a guest on the system, but you want to store the transaction
     *          details.
     */
    public boolean isPartial() {
        return partial;
    }

    /**
     * Sets if the account is a partial account.
     *
     * @param   partial If the account is a partial account.
     */
    public void setPartial(boolean partial) {
        this.partial = partial;
    }

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

    public void setRoles(Set<? extends Role> roles) {
        this.roles = (Set<DefaultRole>) roles;
    }

    public void addRole(Role role) {
        this.roles.add((DefaultRole) role);
    }

    // ------------------ Helpers ------------------------

    /**
     * Convienence method to figure out if a user has the given role.
     *
     * @param   role The user role to check for.
     * @return  True if the user has it, false otherwise.
     */
    public boolean hasRole(String role) {
        for (Role r : roles) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultUser)) return false;

        DefaultUser that = (DefaultUser) o;

        if (!login.equals(that.login)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }
}