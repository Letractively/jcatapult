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
package org.jcatapult.action;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.jcatapult.action.jcatapult.States;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * This class tests the states class.
 * </p>
 *
 * @author  Brian Pontarelli and James Humphrey
 */
public class StatesTest {

    @Test
    public void testNonTerritoryStates() {
        States stateAction = new States();
        stateAction.execute();

        ResourceBundle rb = ResourceBundle.getBundle("org.jcatapult.action.jcatapult.states");
        Assert.assertEquals(rb.keySet().size(), stateAction.getStates().size());
    }

    @Test
    public void testTerritoryStates() {
        States stateAction = new States();
        stateAction.setIncludeTerritories(true);
        stateAction.execute();

        ResourceBundle rb = ResourceBundle.getBundle("org.jcatapult.action.jcatapult.states-territories");
        Assert.assertEquals(rb.keySet().size(), stateAction.getStates().size());
    }

    @Test
    public void testBlankValue() {
        States stateAction = new States();
        stateAction.setIncludeBlank(true);
        String blankValue = "-- SELECT A STATE --";
        stateAction.setBlankValue(blankValue);
        stateAction.execute();

        ResourceBundle rb = ResourceBundle.getBundle("org.jcatapult.action.jcatapult.states");
        Assert.assertEquals(rb.keySet().size() + 1, stateAction.getStates().size());

        List<String> states = new ArrayList<String>(stateAction.getStates().keySet());

        Assert.assertEquals(blankValue, stateAction.getStates().get(states.get(0)));
    }

    @Test
    public void testBlankValueWithPreferred() {
        States stateAction = new States();
        stateAction.setIncludeBlank(true);
        String blankValue = "-- SELECT A STATE --";
        stateAction.setBlankValue(blankValue);
        stateAction.setPreferredStates("CA");
        stateAction.execute();

        ResourceBundle rb = ResourceBundle.getBundle("org.jcatapult.action.jcatapult.states");
        Assert.assertEquals(rb.keySet().size() + 1, stateAction.getStates().size());

        List<String> states = new ArrayList<String>(stateAction.getStates().keySet());

        Assert.assertEquals(blankValue, stateAction.getStates().get(states.get(0)));
        Assert.assertEquals("California", stateAction.getStates().get(states.get(1)));
    }
}
