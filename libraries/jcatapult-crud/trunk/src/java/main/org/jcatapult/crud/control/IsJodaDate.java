/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jcatapult.crud.control;

import java.util.List;

import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;

/**
 * <p>
 * This is a helper method that determines if something is a date so that the
 * Joda formatting rules can be applied.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class IsJodaDate implements TemplateMethodModelEx {
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.isEmpty()) {
            throw new TemplateModelException("You must pass in a single value that is to be checked" +
                " to see if it is a Joda class. Like this: [#if isjodadate(value)]");
        }

        Object obj = arguments.get(0);
        if (obj instanceof BeanModel) {
            obj = ((BeanModel) obj).getWrappedObject();
            System.out.println("Type is "+ obj.getClass());
            if (obj instanceof ReadableInstant || obj instanceof ReadablePartial) {
                return TemplateBooleanModel.TRUE;
            }
        }

        return TemplateBooleanModel.FALSE;
    }
}