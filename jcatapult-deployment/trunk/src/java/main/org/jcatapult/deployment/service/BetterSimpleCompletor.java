package org.jcatapult.deployment.service;

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
 * Used for tab completion
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class BetterSimpleCompletor implements Completor {
    /**
     *  The list of candidates that will be completed.
     */
    TreeSet<String> candidates;

    /**
     *  A delimiter to use to qualify completions.
     */
    String delimiter;
    SimpleCompletorFilter filter = null;

    /**
     *  Create a new SimpleCompletor with a single possible completion
     *  values.
     * @param candidateString the candidate string
     */
    public void setCandidateString(String candidateString) {
        setCandidateStrings(new String[] {candidateString});
    }

    /**
     *  Create a new SimpleCompletor with a list of possible completion
     *  values.
     * @param candidateStrings array of candidate strings
     */
    public void setCandidateStrings(String[] candidateStrings) {
        candidates = new TreeSet<String>(Arrays.asList(candidateStrings));
    }

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

    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

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

    public SortedSet getCandidates() {
        return Collections.unmodifiableSortedSet(this.candidates);
    }

    public void addCandidateString(final String candidateString) {
        final String string =
            (filter == null) ? candidateString : filter.filter(candidateString);

        if (string != null) {
            candidates.add(string);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     *  Filter for elements in the completor.
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

    public static class NoOpFilter implements SimpleCompletorFilter {
        public String filter(final String element) {
            return element;
        }
    }
}
