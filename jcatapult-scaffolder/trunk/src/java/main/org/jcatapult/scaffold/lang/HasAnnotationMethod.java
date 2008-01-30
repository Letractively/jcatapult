/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.scaffold.lang;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.lang.model.element.Element;

import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * <p>
 * This class is a FreeMarker method that determines if an {@link Element} contains
 * a specific annotation
 * </p>
 *
 * @author Brian Pontarelli
 */
public class HasAnnotationMethod implements TemplateMethodModelEx {
    @SuppressWarnings("unchecked")
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 2) {
            throw new TemplateModelException("Invalid arguments to has_annotation method. This method " +
                "takes two arguments. The first is the type javax.lang.model.element.Element and the " +
                "second is a String or Class object that references the annotation to check for.");
        }

        try {
            BeanModel obj = (BeanModel) arguments.get(0);
            Element element = (Element) obj.getWrappedObject();

            TemplateModel obj2 = (TemplateModel) arguments.get(1);
            Class<? extends Annotation> annotation;
            if (obj2 instanceof BeanModel) {
                annotation = (Class<? extends Annotation>) ((BeanModel) obj2).getWrappedObject();
            } else if (obj2 instanceof TemplateScalarModel) {
                annotation = (Class<? extends Annotation>) Class.forName(((TemplateScalarModel) obj2).getAsString());
            } else {
                throw new TemplateModelException("Invalid arguments to has_annotation method. This method " +
                    "takes two arguments. The first is the type javax.lang.model.element.Element and the " +
                    "second is a String or Class object that references the annotation to check for. It " +
                    "looks like the second parameters is the wrong type [" + obj2.getClass() + "]");
            }

            return element.getAnnotation(annotation) != null;
        } catch (ClassCastException cce) {
            throw new TemplateModelException("Invalid arguments to has_annotation method. This method " +
                "takes two arguments. The first is the type javax.lang.model.element.Element and the " +
                "second is a String or Class object that references the annotation to check for. It " +
                "looks like one of the parameters is the wrong type [" + cce.toString() + "]");
        } catch (ClassNotFoundException e) {
            throw new TemplateModelException("The annotation [" + arguments.get(1) + "] is not " +
                "in the classpath.");
        }
    }
}