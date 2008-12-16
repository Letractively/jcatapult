/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.persistence.domain.IdentifiableImpl;

/**
 * <p>
 * This class is a simple address object that provides just a
 * few helper methods for picking apart address Strings.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@Table(name = "email_addresses")
public class EmailAddress extends IdentifiableImpl {
    private static final long serialVersionUID = 1;

    @Required
    @Column(nullable = false)
    private String type;

    @Column()
    private String display;

    @Required
    @Column(nullable = false)
    private String address;

    /**
     * Constructs an empty email address.
     */
    public EmailAddress() {
    }

    /**
     * Constructs a new EmailAddress with only and address and no display name.
     *
     * @param   address The address.
     * @param   type The type of the email address (work, home, etc).
     */
    public EmailAddress(String address, String type) {
        this(address, null, type);
    }

    /**
     * Constructs an email address with the address and display name.
     *
     * @param   address The address.
     * @param   display The display name. The encoding is always UTF-8.
     * @param   type The type of the email address (work, home, etc).
     */
    public EmailAddress(String address, String display, String type) {
        this.type = type;
        this.address = address;
        this.display = display;
    }

    /**
     * @return  The type of the email address (work, home, etc)
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the email address.
     *
     * @param   type The type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retrieves the address address.
     *
     * @return  The address address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address address.
     *
     * @param   address The address address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return  The display name for the address address.
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Sets the display name of the address address.
     *
     * @param   display The display name.
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * @return  Returns the host portion of the address address. This is the part after the at sign.
     *          (for example, bob@example.com would return example.com)
     */
    public String getHost() {
        return address.split("@")[1];
    }

    /**
     * @return  Returns the account portion of the address address. This is the part before the at
     *          sign. (for example, bob@example.com would return bob).
     */
    public String getAccount() {
        return address.split("@")[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailAddress)) return false;

        EmailAddress that = (EmailAddress) o;

        if (!address.equals(that.address)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + address.hashCode();
        return result;
    }
}