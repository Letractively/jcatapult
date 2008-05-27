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
package org.jcatapult.mvc.parameters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcatapult.mvc.locale.LocaleService;
import org.jcatapult.mvc.parameters.el.ExpressionEvaluator;
import static org.jcatapult.mvc.parameters.el.ExpressionEvaluator.*;

import com.google.inject.Inject;

/**
 * <p>
 * This class uses the {@link ExpressionEvaluator} to process the incoming
 * request parameters. It also handles check boxes, submit buttons and radio
 * buttons.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultParameterService implements ParameterService {
    private static final String CHECKBOX_PREFIX = "__jc_cb";
    private static final String RADIOBUTTON_PREFIX = "__jc_rb";
    private static final String ACTION_PREFIX = "__jc_a";
    private final LocaleService localeService;

    @Inject
    public DefaultParameterService(LocaleService localeService) {
        this.localeService = localeService;
    }

    /**
     * Handles the incoming HTTP request parameters.
     *
     * @param   request The request.
     * @param   response The response.
     * @param   action The action to set the values onto.
     */
    public void process(HttpServletRequest request, HttpServletResponse response, Object action) {
        Locale locale = localeService.getLocale(request, response);

        Map<String, Struct> structs = getValuesToSet(request);
        for (String key : structs.keySet()) {
            Struct struct = structs.get(key);
            setValue(key, action, struct.values, request, response, locale, struct.attributes);
        }
    }

    /**
     * Cleanses the HTTP request parameters by removing all the special JCatapult MVC marker parameters
     * for checkboxes, radio buttons and actions. It also adds into the parameters null values for
     * any un-checked checkboxes and un-selected radio buttons. It also collects all of the attributes
     * for each parameter using the {@code @} delimiter character.
     *
     * @param   request The request.
     * @return  The parameters to set into the aciton.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Struct> getValuesToSet(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();

        // Pull out the check box, radio button and action parameters
        Set<String> checkBoxes = new HashSet<String>();
        Set<String> radioButtons = new HashSet<String>();
        Set<String> actions = new HashSet<String>();
        Map<String, Struct> structs = new HashMap<String, Struct>();
        for (String key : parameters.keySet()) {
            if (key.startsWith(CHECKBOX_PREFIX)) {
                checkBoxes.add(key.substring(CHECKBOX_PREFIX.length()));
            } else if (key.startsWith(RADIOBUTTON_PREFIX)) {
                radioButtons.add(key.substring(RADIOBUTTON_PREFIX.length()));
            } else if (key.startsWith(ACTION_PREFIX)) {
                actions.add(key.substring(ACTION_PREFIX.length()));
            } else {
                int index = key.indexOf("@");
                String parameter = (index > 0) ? key.substring(0, index) : key;
                Struct s = structs.get(parameter);
                if (s == null) {
                    s = new Struct();
                    structs.put(parameter, s);
                }

                if (index > 0) {
                    s.attributes.put(key.substring(index + 1), parameters.get(key)[0]);
                } else {
                    s.values = parameters.get(parameter);
                }
            }
        }

        // Remove all the existing checkbox, radio and action keys
        checkBoxes.removeAll(structs.keySet());
        radioButtons.removeAll(structs.keySet());

        // Add back in any left overs as null values
        for (String checkBox : checkBoxes) {
            structs.put(checkBox, new Struct());
        }

        for (String radioButton : radioButtons) {
            structs.put(radioButton, new Struct());
        }

        // Remove actions from the parameters as they should be ignored right now
        for (String actionKey : actions) {
            structs.remove(actionKey);
        }

        return structs;
    }

    private class Struct {
        Map<String, String> attributes = new HashMap<String, String>();
        String[] values;
    }
}