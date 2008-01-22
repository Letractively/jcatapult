package org.jcatapult.action;

import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.opensymphony.xwork2.Action;
import net.java.lang.ObjectTools;
import net.java.lang.StringTools;

/**
 * User: jhumphrey
 * Date: Jan 22, 2008
 */
public class States implements Action {

    private static final Logger logger = Logger.getLogger(States.class.getName());

    private boolean includeBlank;
    private boolean includeTerritories;
    private String blankValue;
    private List<String> preferredStates;
    private Map<String, String> states;

    /**
     * Set to true to include territories in the list of states
     *
     * @param includeTerritories true if including territories, false otherwise
     */
    public void setIncludeTerritories(boolean includeTerritories) {
        this.includeTerritories = includeTerritories;
    }

    /**
     * This parameters determines if a blank entry is added to the front of the linked Map that is
     * built by the {@link #execute()} method. The blank key will always be empty String and the value
     * will default to empty String unless set by the {@link #setBlankValue(String)} method.
     *
     * @param   includeBlank If true a blank will be inserted, false it will not. This defaults to
     *          false.
     */
    public void setIncludeBlank(boolean includeBlank) {
        this.includeBlank = includeBlank;
    }

    /**
     * This parameter is the value that will be used for the blank spot if the {@link #setIncludeBlank(boolean)}
     * parameter is set to true. The key will always empty String.
     *
     * @param   blankValue The value for the blank spot.
     */
    public void setBlankValue(String blankValue) {
        this.blankValue = blankValue;
    }

    /**
     * This parameter can be passed in in order to set up the states that should be at the top of
     * the Map. These are the state abbreviation codes and the order they are given is the order that they are
     * put into the linked Map.
     *
     * @param   preferredStates The preferred state abbreviation codes.
     */
    @SuppressWarnings({"unchecked"})
    public void setPreferredStates(Object preferredStates) {
        this.preferredStates = (List<String>) ObjectTools.makeList(preferredStates);
    }

    /**
     * The Map built by the {@link #execute()} method.
     *
     * @return The Map or null if execute was not called.
     */
    public Map<String, String> getStates() {
        return states;
    }

    /**
     * Builds the Map of states.
     *
     * @return Always success.
     */
    public String execute() {

        // alphabetize the resource bundle
        Map<String, String> alphaStates = new TreeMap<String, String>();

        ResourceBundle stateResourceBundle = includeTerritories ?
            ResourceBundle.getBundle("org.jcatapult.action.states-territories") :
            ResourceBundle.getBundle("org.jcatapult.action.states");

        Enumeration stateEnum = stateResourceBundle.getKeys();
        while (stateEnum.hasMoreElements()) {
            String stateAbbr = (String) stateEnum.nextElement();
            String state = stateResourceBundle.getString(stateAbbr);
            alphaStates.put(state, stateAbbr);
        }

        // now populate the states map
        states = new LinkedHashMap<String, String>();

        if (includeBlank) {
            states.put("", blankValue == null ? "" : blankValue);
        }

        if (preferredStates != null && preferredStates.size() > 0) {
            for (String preferredState : preferredStates) {
                states.put(preferredState, stateResourceBundle.getString(preferredState));
            }
        }

        for (String state : alphaStates.keySet()) {
            String stateAbbr = alphaStates.get(state);
            states.put(stateAbbr, state);
        }

        return SUCCESS;
    }
}
