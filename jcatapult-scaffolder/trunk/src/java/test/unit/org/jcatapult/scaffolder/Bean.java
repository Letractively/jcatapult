/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.scaffolder;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * <p>
 * This is a test bean.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class Bean {
    private Inner inner;
    private boolean flag;

    @Column(name = "age")
    private int age;

    @Column(nullable = false)
    private String name;

    @Column()
    private String optional;

    @Transient
    private String notSaved;

    @ManyToMany()
    private List<Category> categories = new ArrayList<Category>();

    @ManyToOne
    private Email email;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Inner getInner() {
        return inner;
    }

    public void setInner(Inner inner) {
        this.inner = inner;
    }

    public String getNotSaved() {
        return notSaved;
    }

    public void setNotSaved(String notSaved) {
        this.notSaved = notSaved;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }
}