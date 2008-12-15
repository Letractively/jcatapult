/*
 * Copyright (c) 2007, Inversoft LLC, All Rights Reserved
 */
package com.inversoft.module.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.persistence.domain.IdentifiableImpl;

/**
 * <p>
 * This class is a entity that stores phone numbers
 * and also the type of the phone number (cell, home,
 * work, etc). The type is a String so that we don't
 * run into enum hell.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@Table(name="phone_numbers")
public class PhoneNumber extends IdentifiableImpl {
    private static final long serialVersionUID = 1;

    @Required
    @Column(nullable = false)
    private String number;

    @Required
    @Column(nullable = false)
    private String type;

    public PhoneNumber() {
    }

    /**
     * Constructs a phone number with values.
     *
     * @param   number The phone number.
     * @param   type The type.
     */
    public PhoneNumber(String number, String type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber)) return false;

        PhoneNumber that = (PhoneNumber) o;

        if (!number.equals(that.number)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = number.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}