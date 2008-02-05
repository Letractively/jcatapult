/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.scaffold.lang;

import java.lang.annotation.ElementType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * <p>
 * This class models a type (Class, Interface, Enum).
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ClassType extends Type {
    private final TypeElement typeElement;

    public ClassType(TypeElement typeElement) {
        super(typeElement.asType());
        this.typeElement = typeElement;
    }

    /**
     * @return  Always false.
     */
    @Override
    public boolean isPrimitive() {
        return false;
    }

    /**
     * @return  True if this is a wrapper class (Boolean, Integer, Number, etc) or a String.
     */
    @Override
    public boolean isSimpleType() {
        String fqn = typeElement.getQualifiedName().toString();
        return fqn.equals("java.lang.Boolean") || fqn.equals("java.lang.Byte") || fqn.equals("java.lang.Character") ||
            fqn.equals("java.lang.Double") || fqn.equals("java.lang.Float") || fqn.equals("java.lang.Integer") ||
            fqn.equals("java.lang.Long") || fqn.equals("java.lang.Short") || fqn.equals("java.lang.String");
    }

    public ClassType getParent() {
        TypeMirror typeMirror = typeElement.getSuperclass();
        if (typeMirror.getKind() != TypeKind.NONE) {
            return new ClassType((TypeElement) ((DeclaredType) typeMirror).asElement());
        }

        return null;
    }

    public List<ClassType> getParents() {
        if (getParent() == null) {
            return new ArrayList<ClassType>();
        }

        List<ClassType> parents = new ArrayList<ClassType>();
        parents.add(getParent());
        parents.addAll(getParent().getParents());
        return parents;
    }

    public boolean implement(String className) {
        List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
        for (TypeMirror anInterface : interfaces) {
            if (anInterface.getKind() != TypeKind.NONE) {
                TypeElement ite = (TypeElement) ((DeclaredType) anInterface).asElement();
                if (ite.getQualifiedName().toString().equals(className)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isA(String className) {
        if (implement(className)) {
            return true;
        }

        List<ClassType> parents = getParents();
        for (ClassType parent : parents) {
            if (parent.getFullName().equals(className) || parent.implement(className)) {
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return typeElement.getSimpleName().toString();
    }

    public String getFullName() {
        return typeElement.getQualifiedName().toString();
    }

    public String getPackageName() {
        String fqn = typeElement.getQualifiedName().toString();
        int index = fqn.lastIndexOf('.');
        if (index > 0) {
            return fqn.substring(0, index);
        }

        return null;
    }

    public Element getElement() {
        return typeElement;
    }

    public ElementType getElementType() {
        return ElementType.TYPE;
    }

    public Set<Field> getLocalFields() {
        // Add in the local fields and not anything from the parent class
        Set<Field> fields = new HashSet<Field>();
        List<? extends Element> elements = typeElement.getEnclosedElements();
        for (Element element : elements) {
            if (element.getKind() == ElementKind.FIELD) {
                VariableElement variable = (VariableElement) element;
                fields.add(new Field(variable));
            }
        }

        return fields;
    }

    public Set<Field> getAllFields() {
        Set<Field> fields = new HashSet<Field>();
        fields.addAll(getLocalFields());

        ClassType parent = getParent();
        if (parent != null) {
            fields.addAll(parent.getAllFields());
        }

        return fields;
    }
}