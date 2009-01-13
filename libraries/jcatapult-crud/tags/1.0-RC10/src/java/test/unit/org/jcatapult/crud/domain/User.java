/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.crud.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.jcatapult.persistence.domain.IdentifiableImpl;

/**
 * <p>
 * This is a test domain class.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
public class User extends IdentifiableImpl {
    @Column(nullable = false)
    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}