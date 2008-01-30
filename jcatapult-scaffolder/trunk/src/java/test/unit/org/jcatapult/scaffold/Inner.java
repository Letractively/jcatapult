/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.scaffold;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * <p>
 * This is a test embeddable object.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Embeddable
public class Inner {
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private InnerInner inner;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public InnerInner getInner() {
        return inner;
    }

    public void setInner(InnerInner inner) {
        this.inner = inner;
    }
}