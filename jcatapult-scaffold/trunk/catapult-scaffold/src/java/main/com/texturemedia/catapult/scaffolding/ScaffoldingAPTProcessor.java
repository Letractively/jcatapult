/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package com.texturemedia.catapult.scaffolding;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.texturemedia.catapult.scaffolding.lang.ClassType;

/**
 * <p>
 * This class is an annotation processor that locates the TypeElement
 * that the user is scaffolding. Usually this is an annotated domain
 * class or something similar. From this TypeElement, anything can be
 * determined by tranversing the AST.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("*")
public class ScaffoldingAPTProcessor extends AbstractProcessor {
    private String className;
    private ClassType type;

    public ScaffoldingAPTProcessor(String className) {
        this.className = className;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> rootElements = roundEnv.getRootElements();
        for (Element rootElement : rootElements) {
            if (rootElement.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) rootElement;
                if (className.equals(typeElement.getQualifiedName().toString())) {
                    type = new ClassType(typeElement);
                    break;
                }
            }
        }

        return true;
    }

    /**
     * @return  This returns the Type for the class whose name was supplied to the constructor.
     *          If that Type could not be found, null is returned.
     */
    public ClassType getType() {
        return type;
    }
}