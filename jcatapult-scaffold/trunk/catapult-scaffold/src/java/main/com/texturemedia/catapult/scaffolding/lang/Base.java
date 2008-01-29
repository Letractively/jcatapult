/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package com.texturemedia.catapult.scaffolding.lang;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import net.java.lang.ObjectTools;
import net.java.lang.StringTools;
import net.java.text.Pluralizer;
import net.java.text.SimplePluralizer;

/**
 * <p>
 * This class is the base class for fields and types.
 * </p>
 *
 * @author Brian Pontarelli
 */
public abstract class Base {
    private static final Pluralizer pluralizer = new SimplePluralizer();

    public abstract String getName();

    public String getPlainEnglishName() {
        String str = getName();
        String[] parts = StringTools.deCamelCase(str, false);
        parts[0] = StringTools.capitalize(parts[0]);
        return ObjectTools.join(parts, " ");
    }

    public String getFieldName() {
        return StringTools.decapitalize(getName());
    }

    public String getMethodName() {
        return StringTools.capitalize(getName());
    }

    public String getPluralName() {
        String str = getName();
        return pluralizer.pluralize(str);
    }

    public String getPluralPlainEnglishName() {
        String str = getPlainEnglishName();
        return pluralizer.pluralize(str);
    }

    public String getPluralFieldName() {
        String str = getFieldName();
        return pluralizer.pluralize(str);
    }

    public String getPluralMethodName() {
        String str = getMethodName();
        return pluralizer.pluralize(str);
    }

    public abstract Element getElement();

    public abstract ElementType getElementType();

    public List<Annotation> getAnnotations() {
        List<? extends AnnotationMirror> annotationMirrors = getElement().getAnnotationMirrors();
        List<Annotation> annotations = new ArrayList<Annotation>();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            annotations.add(new Annotation(annotationMirror, getElementType()));
        }

        return annotations;
    }

    public Annotation getAnnotation(String className) {
        List<? extends AnnotationMirror> annotationMirrors = getElement().getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            TypeElement annotationTypeElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            if (annotationTypeElement.getQualifiedName().toString().equals(className)) {
                return new Annotation(annotationMirror, getElementType());
            }
        }

        return null;
    }

    public boolean hasAnnotation(String className) {
        return getAnnotation(className) != null;
    }
}