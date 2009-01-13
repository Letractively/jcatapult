/*
 * Copyright (c) 2007, Jcatapult LLC, All Rights Reserved
 */
package org.jcatapult.module.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.persistence.domain.Identifiable;

import static net.java.lang.StringTools.*;

/**
 * <p>
 * This class is a standard Address bean.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Entity
@Table(name="addresses")
public class Address extends org.jcatapult.domain.location.Address implements Identifiable {
    @Id
    @GeneratedValue
    private Integer id;

    @Required
    @Column(nullable = false)
    private String type;

    public Address() {
    }

    public Address(String street, String city, String state, String district, String country,
            String postalCode, String type) {
        this.setCity(city);
        this.setCountry(country);
        this.setDistrict(district);
        this.setPostalCode(postalCode);
        this.setState(state);
        this.setStreet(street);
        this.setType(type);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param   address The address to check.
     * @return  True if the address has any fields with data, false otherwise.
     */
    public static boolean isContainsData(org.jcatapult.domain.location.Address address) {
        return !isTrimmedEmpty(address.getStreet()) || !isTrimmedEmpty(address.getState()) ||
            !isTrimmedEmpty(address.getDistrict()) || !isTrimmedEmpty(address.getCity()) ||
            !isTrimmedEmpty(address.getCountry()) || !isTrimmedEmpty(address.getPostalCode());
    }

    /**
     * Extends on the address equals method by taking into account the type field.
     *
     * @param   o The object to compare.
     * @return  True if the object is an address and is equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        if (!super.equals(o)) return false;

        Address address = (Address) o;

        if (!type.equals(address.type)) return false;

        return true;
    }

    /**
     * Generates a hash code using the address hash code and the type field.
     *
     * @return  The hash code.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}