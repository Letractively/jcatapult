/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.scaffolder.lang;

import java.lang.annotation.ElementType;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * <p>
 * This class models a primitive type (boolean, char, int, etc).
 * </p>
 *
 * @author Brian Pontarelli
 */
public class PrimitiveType extends Type {
    public PrimitiveType(TypeMirror typeMirror) {
        super(typeMirror);
    }

    @Override
    public String getName() {
        return type.getKind().toString().toLowerCase();
    }

    public String getFullName() {
        return type.getKind().toString().toLowerCase();
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    /**
     * @return  Always true because primitives are all simple types.
     */
    @Override
    public boolean isSimpleType() {
        return true;
    }

    @Override
    public Element getElement() {
        return null;
    }

    @Override
    public ElementType getElementType() {
        return null;
    }
}