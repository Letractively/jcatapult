/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
 *
 */
package org.jcatapult.mvc.parameters.el;

import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.parameters.convert.ConversionException;
import org.jcatapult.mvc.parameters.convert.ConverterStateException;

/**
 * <p>
 * This interface defines the mechanism by which expressions are evaluated
 * in order to get values from Objects and set values from the HTTP request
 * parameters into Objects.
 * </p>
 *
 * @author Brian Pontarelli
 */
public interface ExpressionEvaluator {
    <T> T getValue(String expression, Object bean, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ExpressionException;

    void setValue(String expression, Object bean, String[] values, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes)
    throws ConversionException, ConverterStateException, ExpressionException;

}