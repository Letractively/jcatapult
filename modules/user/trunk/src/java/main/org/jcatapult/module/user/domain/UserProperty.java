/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.jcatapult.persistence.domain.IdentifiableImpl;

/**
 * <p>
 * This class is a entity that stores misc. user properties such
 * as flags that control emailing options.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@Table(name="user_properties", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "users_id"})})
public class UserProperty extends IdentifiableImpl {
    private static final long serialVersionUID = 1;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String name;

    @ManyToOne()
    @JoinColumn(name = "users_id")
    private DefaultUser user;

    public UserProperty() {
    }

    /**
     * Constructs a user property.
     *
     * @param   name The property name.
     * @param   value The property value.
     */
    public UserProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DefaultUser getUser() {
        return user;
    }

    public void setUser(DefaultUser user) {
        this.user = user;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProperty that = (UserProperty) o;

        if (!value.equals(that.value)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = value.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}