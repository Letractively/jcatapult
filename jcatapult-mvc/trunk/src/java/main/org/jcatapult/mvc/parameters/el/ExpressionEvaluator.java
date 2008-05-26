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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * This
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ExpressionEvaluator {
    @SuppressWarnings("unchecked")
    public static <T> T getValue(String expression, Object bean, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes) {
        List<Atom> atoms = parse(expression);
        Context context = new Context(atoms, request, response, locale, attributes);
        context.init(bean);
        while (context.hasNext()) {
            Atom atom = context.next();
            context.initAccessor(atom.getName());
            if (context.skip()) {
                System.out.println("Skipped");
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

    @SuppressWarnings("unchecked")
    public static void setValue(String expression, Object bean, Object value, HttpServletRequest request,
            HttpServletResponse response, Locale locale, Map<String, String> attributes) {
        List<Atom> atoms = parse(expression);
        Context context = new Context(atoms, request, response, locale, attributes);
        context.init(bean);
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
     * This breaks the expression name down into manageable pieces. These are the individual instances
     * of the Atom inner class which store the name and the indices (which could be null or any object).
     * This is broken on the '.' character.
     *
     * @param   expression The expression strng to break down.
     * @return  A new ArrayList of PropertyInfo objects.
     * @throws  ExpressionException If the property string is invalid.
     */
    private static List<Atom> parse(String expression) throws ExpressionException {
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
    private static List<Atom> createAtoms(String expression) throws ExpressionException {
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