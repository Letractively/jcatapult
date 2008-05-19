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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * <p>
 * This class models a class variable (field).
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Field extends Base {
    private final VariableElement variableElement;

    public Field(VariableElement variableElement) {
        this.variableElement = variableElement;
    }

    public String getName() {
        return variableElement.getSimpleName().toString();
    }

    public Element getElement() {
        return variableElement;
    }

    public ElementType getElementType() {
        return ElementType.FIELD;
    }

    public boolean isStatic() {
        Set<Modifier> modifiers = variableElement.getModifiers();
        for (Modifier modifier : modifiers) {
            if (modifier == Modifier.STATIC) {
                return true;
            }
        }

        return false;
    }

    public boolean isFinal() {
        Set<Modifier> modifiers = variableElement.getModifiers();
        for (Modifier modifier : modifiers) {
            if (modifier == Modifier.FINAL) {
                return true;
            }
        }

        return false;
    }

    public boolean isGeneric() {
        DeclaredType typeMirror = (DeclaredType) variableElement.asType();
        return typeMirror.getTypeArguments().size() > 0;
    }

    public Type getMainType() {
        TypeMirror typeMirror = variableElement.asType();
        if (typeMirror.getKind().isPrimitive()) {
            return new PrimitiveType(typeMirror);
        }

        return new ClassType((TypeElement) ((DeclaredType) typeMirror).asElement());
    }

    public List<Type> getGenericTypes() {
        List<Type> types = new ArrayList<Type>();
        TypeMirror typeMirror = variableElement.asType();
        if (typeMirror.getKind().isPrimitive()) {
            return types;
        }

        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> args = declaredType.getTypeArguments();
        for (TypeMirror arg : args) {
            DeclaredType dt = (DeclaredType) arg;
            types.add(new ClassType((TypeElement) dt.asElement()));
        }

        return types;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (!variableElement.equals(field.variableElement)) return false;

        return true;
    }

    public int hashCode() {
        return variableElement.hashCode();
    }
}