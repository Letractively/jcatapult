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
 */
package org.jcatapult.domain.location;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

/**
 * <p>
 * This class is a simple address object.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Embeddable
@MappedSuperclass
public class Address implements Serializable {
    private final static int serialVersionUID = 1;

    @Column(nullable = false, length = 512)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = true)
    private String state;

    @Column(nullable = true)
    private String district;

    @Column(nullable = false)
    private String country;

    @Column(nullable = true, name = "postal_code")
    private String postalCode;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Compares the address with the given object for equality. This comparison uses all of the fields
     * in the comparison. The nullable fields are only district, postal code, and state.
     *
     * @param   o The object to compare.
     * @return  True if the object is an address and is equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;

        Address address = (Address) o;

        if (!city.equals(address.city)) return false;
        if (!country.equals(address.country)) return false;
        if (district != null ? !district.equals(address.district) : address.district != null)
            return false;
        if (postalCode != null ? !postalCode.equals(address.postalCode) : address.postalCode != null)
            return false;
        if (state != null ? !state.equals(address.state) : address.state != null) return false;
        if (!street.equals(address.street)) return false;

        return true;
    }

    /**
     * Generates a hash code using all the fields.
     *
     * @return  The hash code.
     */
    @Override
    public int hashCode() {
        int result = street.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + country.hashCode();
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        return result;
    }
}