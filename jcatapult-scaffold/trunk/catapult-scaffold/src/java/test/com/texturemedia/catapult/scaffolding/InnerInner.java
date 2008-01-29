/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package com.texturemedia.catapult.scaffolding;

import javax.persistence.Embeddable;

/**
 * <p>
 * This is a test embeddable object.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Embeddable
public class InnerInner {
    private String first;
    private String second;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}