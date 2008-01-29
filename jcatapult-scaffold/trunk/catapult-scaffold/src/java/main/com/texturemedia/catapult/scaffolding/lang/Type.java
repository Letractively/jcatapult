/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package com.texturemedia.catapult.scaffolding.lang;

import javax.lang.model.type.TypeMirror;

/**
 * <p>
 * This class models a type (Class, Interface, Enum).
 * </p>
 *
 * @author Brian Pontarelli
 */
public abstract class Type extends Base {
    protected final TypeMirror type;

    public Type(TypeMirror type) {
        this.type = type;
    }

    public abstract boolean isPrimitive();

    public abstract boolean isSimpleType();
}