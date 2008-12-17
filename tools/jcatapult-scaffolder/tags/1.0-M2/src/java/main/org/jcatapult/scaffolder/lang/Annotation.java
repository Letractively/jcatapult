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
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * <p>
 * This class models an annotation on a specific location.
 * For example, an annotation on a member variable or a class.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Annotation {
    private final AnnotationMirror annotationMirror;
    private final ElementType location;

    public Annotation(AnnotationMirror annotationMirror, ElementType location) {
        this.annotationMirror = annotationMirror;
        this.location = location;
    }

    public ClassType getType() {
        return new ClassType((TypeElement) annotationMirror.getAnnotationType().asElement());
    }

    public ElementType getLocation() {
        return location;
    }

    public Map<String, Object> getParameters() {
        Map<? extends ExecutableElement, ? extends AnnotationValue> params = annotationMirror.getElementValues();
        Map<String, Object> result = new HashMap<String, Object>();
        for (ExecutableElement executableElement : params.keySet()) {
            AnnotationValue value = params.get(executableElement);
            result.put(executableElement.getSimpleName().toString(), value.getValue());
        }
        return result;
    }

    public Object getParameter(String name) {
        return getParameters().get(name);
    }
}