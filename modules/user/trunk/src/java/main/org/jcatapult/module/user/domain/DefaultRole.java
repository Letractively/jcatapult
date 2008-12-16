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
 * This class maps all the roles of the application.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Entity
@Table(name = "roles")
public class DefaultRole extends IdentifiableImpl implements Role {
    private static final long serialVersionUID = 1;

    @Required
    @Column(unique = true, nullable = false)
    private String name;

    public DefaultRole() {
    }

    public DefaultRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultRole that = (DefaultRole) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }
}