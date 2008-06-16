/*
 * Copyright (c) 2001-2008, JCatapult.org, All Rights Reserved
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

package org.jcatapult.deployer.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jline.Completor;

/**
 * <p>Used for tab completion</p>
 *
 * @author jhumphrey
 */
public class BetterSimpleCompletor implements Completor {

    private TreeSet<String> candidates;
    String delimiter;
    SimpleCompletorFilter filter = null;

    /**
     *  <p>Create a new SimpleCompletor with a single possible completion
     *  values.</p>
     *
     * @param candidateString the candidate string
     */
    public void setCandidateString(String candidateString) {
        setCandidateStrings(new String[]{candidateString});
    }

    /**
     *  <p>Create a new SimpleCompletor with a list of possible completion values.</p>
     *
     * @param candidateStrings array of candidate strings
     */
    public void setCandidateStrings(String[] candidateStrings) {
        candidates = new TreeSet<String>(Arrays.asList(candidateStrings));
    }

    /**
     * <p>Called to tab complete</p>
     *
     * @param buffer the buffer string
     * @param cursor the curor position
     * @param clist the command list
     * @return complete index
     */
    @SuppressWarnings(value = "unchecked")
    public int complete(final String buffer, final int cursor, final List clist) {
        String start = (buffer == null) ? "" : buffer;

        SortedSet<String> matches = candidates.tailSet(start);

        Set<String> results = new HashSet<String>();
        boolean trimmed = false;
        for (Iterator i = matches.iterator(); i.hasNext();) {
            String can = (String) i.next();

            if (!(can.startsWith(start))) {
                break;
            }

            if (delimiter != null) {
                int index = can.indexOf(delimiter, cursor);

                if (index != -1) {
                    can = can.substring(0, index + 1);
                    trimmed = true;
                }
            }

            results.add(can);
        }

        clist.addAll(results);
        if (clist.size() == 1 && !trimmed) {
            clist.set(0, (clist.get(0)) + " ");
        }

        // the index of the completion is always from the beginning of
        // the buffer.
        return (clist.size() == 0) ? (-1) : 0;
    }

    /**
     * <p>Sets the tab completion delimiter</p>
     *
     * @param delimiter the tab completion delimiter
     */
    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * <p>Gets the tab completion delimeter</p>
     *
     * @return the delimiter
     */
    public String getDelimiter() {
        return this.delimiter;
    }

    /**
     * <p>Sets the candidate to be used for tab completion</p>
     *
     * @param candidates the tab completion candidates
     */
    public void setCandidates(final TreeSet<String> candidates) {
        if (filter != null) {
            TreeSet<String> filtered = new TreeSet<String>();

            for (Iterator i = candidates.iterator(); i.hasNext();) {
                String element = (String) i.next();
                element = filter.filter(element);

                if (element != null) {
                    filtered.add(element);
                }
            }

            this.candidates = filtered;
        } else {
            this.candidates = candidates;
        }
    }

    /**
     * <p>Gets the tab completion candidates</p>
     *
     * @return sorted set of tab completed candidates
     */
    public SortedSet getCandidates() {
        return Collections.unmodifiableSortedSet(this.candidates);
    }

    /**
     * <p>Adds a tab completion candidate to the tab completion list</p>
     *
     * @param candidateString the tab completion string
     */
    public void addCandidateString(final String candidateString) {
        final String string =
            (filter == null) ? candidateString : filter.filter(candidateString);

        if (string != null) {
            candidates.add(string);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     *  <p>Filter for elements in the completor.</p>
     *
     *  @author  <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
     */
    public static interface SimpleCompletorFilter {
        /**
         *  Filter the specified String. To not filter it, return the
         *  same String as the parameter. To exclude it, return null.
         * @param element the element
         * @return returns a filtered element
         */
        public String filter(String element);
    }
}
