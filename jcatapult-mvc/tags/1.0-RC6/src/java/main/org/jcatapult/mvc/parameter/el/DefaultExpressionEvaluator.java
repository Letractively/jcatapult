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
 */
package org.jcatapult.mvc.parameter.el;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.locale.annotation.CurrentLocale;
import org.jcatapult.mvc.parameter.convert.ConversionException;
import org.jcatapult.mvc.parameter.convert.GlobalConverter;
import org.jcatapult.mvc.parameter.convert.ConverterProvider;
import org.jcatapult.mvc.parameter.convert.ConverterStateException;

import com.google.inject.Inject;
import net.java.variable.ExpanderException;
import net.java.variable.ExpanderStrategy;
import net.java.variable.VariableExpander;

/**
 * <p>
 * This class is the default implementation of the ExpressionEvaluator
 * service. This provides a robust expression processing facility that
 * leverages JavaBean properties, fields and generics to get and set
 * values into Objects.
 * </p>
 *
 * <p>
 * TODO fully document here
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class DefaultExpressionEvaluator implements ExpressionEvaluator {
    private final Locale locale;
    private final HttpServletRequest request;
    private final ConverterProvider converterProvider;

    @Inject
    public DefaultExpressionEvaluator(@CurrentLocale Locale locale, HttpServletRequest request,
            ConverterProvider converterProvider) {
        this.locale = locale;
        this.request = request;
        this.converterProvider = converterProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getValue(String expression, Object object) throws ExpressionException {
        List<Atom> atoms = parse(expression);
        Context context = new Context(converterProvider, atoms);
        context.init(object);
        while (context.hasNext()) {
            Atom atom = context.next();
            context.initAccessor(atom.getName());
            if (context.skip()) {
                continue;
            }

            Object value = context.getCurrentValue();
            if (value == null) {
                return null;
            }

            context.init(value);
        }

        return (T) context.getObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue(String expression, Object object, Map<String, String> attributes)
    throws ExpressionException {
        Object value = getValue(expression, object);
        if (value == null) {
            return "";
        }

        Class<?> type = value.getClass();
        GlobalConverter converter = converterProvider.lookup(type);
        if (converter == null) {
            throw new ConverterStateException("No type converter found for the type [" + type + "]");
        }

        return converter.convertToString(value, (Class<Object>) type, attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override

    public void setValue(String expression, Object object, Object value) throws ExpressionException {
        List<Atom> atoms = parse(expression);
        Context context = new Context(converterProvider, atoms);
        context.init(object);
        while (context.hasNext()) {
            Atom atom = context.next();
            context.initAccessor(atom.getName());
            if (context.skip()) {
                if (!context.hasNext()) {
                    throw new ExpressionException("Encountered an indexed property without an index in the " +
                        "expression [" + expression + "]");
                }

                continue;
            }

            if (!context.hasNext()) {
                context.setCurrentValue(value);
            } else {
                Object nextValue = context.getCurrentValue();
                if (nextValue == null) {
                    nextValue = context.createValue();
                }

                context.init(nextValue);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(String expression, Object object, String[] values, Map<String, String> attributes)
    throws ConversionException, ConverterStateException, ExpressionException {
        List<Atom> atoms = parse(expression);
        Context context = new Context(converterProvider, atoms, request, locale, attributes);
        context.init(object);
        while (context.hasNext()) {
            Atom atom = context.next();
            context.initAccessor(atom.getName());
            if (context.skip()) {
                if (!context.hasNext()) {
                    throw new ExpressionException("Encountered an indexed property without an index in the " +
                        "expression [" + expression + "]");
                }

                continue;
            }

            if (!context.hasNext()) {
                context.setCurrentValue(values);
            } else {
                Object nextValue = context.getCurrentValue();
                if (nextValue == null) {
                    nextValue = context.createValue();
                }

                context.init(nextValue);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String expand(String str, final Object object)
    throws ExpressionException {
        return VariableExpander.expand(str, new ExpanderStrategy() {
            public String expand(String variableName) throws ExpanderException {
                return getValue(variableName, object, new HashMap<String, String>());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllMembers(Class<?> type) {
        return MemberAccessor.getAllMembers(type);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Object> getAllMemberValues(Object obj) {
        Set<String> names = getAllMembers(obj.getClass());
        Collection<Object> values = new ArrayList<Object>();
        for (String name : names) {
            values.add(getValue(name, obj));
        }

        return values;
    }

    /**
     * This breaks the expression name down into manageable pieces. These are the individual instances
     * of the Atom inner class which store the name and the indices (which could be null or any object).
     * This is broken on the '.' character.
     *
     * @param   expression The expression strng to break down.
     * @return  A new ArrayList of PropertyInfo objects.
     * @throws  ExpressionException If the property string is invalid.
     */
    private List<Atom> parse(String expression) throws ExpressionException {
        List<Atom> info = new ArrayList<Atom>();
        StringTokenizer ts = new StringTokenizer(expression, ".");

        while (ts.hasMoreTokens()) {
            // Grab the indices from the property
            info.addAll(createAtoms(ts.nextToken()));
        }

        return info;
    }

    /**
     * Given the expression string, the indices and name are extracted and returned in one or more new
     * Atoms. If the given expression string does not contain any indices, this returns a single atom.
     * Otherwise, it returns multiple atoms.
     *
     * @param   expression The expression to parse into one or more atoms.
     * @return  The atoms.
     * @throws  ExpressionException If the property name contains an invalid indices or an unclosed indices
     *          notation (i.e. '[1').
     */
    private List<Atom> createAtoms(String expression) throws ExpressionException {
        // Determine whether or not the property is indexed or not
        List<Atom> atoms = new ArrayList<Atom>();
        int bracet = expression.indexOf('[');
        int firstBracet = bracet;
        if (bracet == -1) {
            atoms.add(new Atom(expression));
            return atoms;
        }

        atoms.add(new Atom(expression.substring(0, firstBracet)));
        while (bracet != -1) {
            int bracetTwo = expression.indexOf(']', bracet);
            if (bracetTwo == -1) {
                throw new ExpressionException("The bean property name string: " + expression
                    + " contains an invalid indices");
            }

            String indexStr = expression.substring(bracet + 1, bracetTwo);
            int length = indexStr.length();
            char ch = indexStr.charAt(0);
            if (ch == '"' || ch == '\'') {
                char lastCh = indexStr.charAt(length - 1);
                if (lastCh != '"' && lastCh != '\'') {
                    throw new ExpressionException("Invalid indices value: " + indexStr);
                }

                atoms.add(new Atom(indexStr.substring(1, length - 1)));
            } else {
                atoms.add(new Atom(indexStr));
            }

            bracet = expression.indexOf('[', bracetTwo);
        }

        return atoms;
    }
}