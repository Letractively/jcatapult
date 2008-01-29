package com.texturemedia.catapult.scaffolding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import jline.Completor;

public class BetterSimpleCompletor implements Completor {
    /**
     *  The list of candidates that will be completed.
     */
    SortedSet candidates;

    /**
     *  A delimiter to use to qualify completions.
     */
    String delimiter;
    final SimpleCompletorFilter filter;

    /**
     *  Create a new SimpleCompletor with a single possible completion
     *  values.
     */
    public BetterSimpleCompletor(final String candidateString) {
        this(new String[] {candidateString});
    }

    /**
     *  Create a new SimpleCompletor with a list of possible completion
     *  values.
     */
    public BetterSimpleCompletor(final String[] candidateStrings) {
        this(candidateStrings, null);
    }

    public BetterSimpleCompletor(final String[] strings, final SimpleCompletorFilter filter) {
        this.filter = filter;
        setCandidateStrings(strings);
    }

    /**
     *  Complete candidates using the contents of the specified Reader.
     */
    public BetterSimpleCompletor(final Reader reader) throws IOException {
        this(getStrings(reader));
    }

    /**
     *  Complete candidates using the whitespearated values in
     *  read from the specified Reader.
     */
    public BetterSimpleCompletor(final InputStream in) throws IOException {
        this(getStrings(new InputStreamReader(in)));
    }

    private static String[] getStrings(final Reader in)
                                throws IOException {
        final Reader reader =
            (in instanceof BufferedReader) ? in : new BufferedReader(in);

        List words = new LinkedList();
        String line;

        while ((line = ((BufferedReader) reader).readLine()) != null) {
            for (StringTokenizer tok = new StringTokenizer(line);
                     tok.hasMoreTokens(); words.add(tok.nextToken())) {
                ;
            }
        }

        return (String[]) words.toArray(new String[words.size()]);
    }

    public int complete(final String buffer, final int cursor, final List clist) {
        String start = (buffer == null) ? "" : buffer;

        SortedSet matches = candidates.tailSet(start);

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

    public void setCandidates(final SortedSet candidates) {
        if (filter != null) {
            TreeSet filtered = new TreeSet();

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

    public void setCandidateStrings(final String[] strings) {
        setCandidates(new TreeSet(Arrays.asList(strings)));
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
         */
        public String filter(String element);
    }

    public static class NoOpFilter implements SimpleCompletorFilter {
        public String filter(final String element) {
            return element;
        }
    }
}