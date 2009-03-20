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