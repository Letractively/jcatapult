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
package org.jcatapult.mvc.result.control;

import java.util.Map;

import org.jcatapult.l10n.MessageProvider;
import org.jcatapult.mvc.message.scope.MessageType;

import com.google.inject.Inject;

/**
 * <p>
 * This class is an abstract control implementation for input tags.
 * In addition to the abstract control, it also provides support
 * for labels.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractInput extends AbstractControl {
    protected MessageProvider messageProvider;
    private final boolean labeled;

    public AbstractInput(boolean labeled) {
        this.labeled = labeled;
    }

    /**
     * Sets the message provider, which is used to lookup the value from the inputs label.
     *
     * @param   messageProvider The message provider.
     */
    @Inject
    public void setMessageProvider(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    /**
     * Adds a default ID if one doesn't exist.
     *
     * @param   attributes The attributes from the tag.
     * @param   parameterAttributes The parameter attributes from the tag.
     */
    protected void addAdditionalAttributes(Map<String, Object> attributes,
        Map<String, String> parameterAttributes) {
        String id = (String) attributes.get("id");
        if (id == null) {
            id = makeID((String) attributes.get("name"));
            attributes.put("id", id);
        }
    }

    /**
     * Converts the name attribute to an ID.
     *
     * @param   name The name attribute.
     * @return  The ID.
     */
    protected String makeID(String name) {
        return name.replace('.', '_');
    }

    /**
     * Overrides the parameter map cration from the AbstractControl and adds the label for input
     * tags. This also moves the <code>labelposition</code> attribute from the tag to the returned
     * Map (removes it from the attributes).
     *
     * @param   attributes The attributes from the tag.
     * @param   parameterAttributes The parameter attributes from the tag.
     * @return  The parameter map.
     */
    @Override
    protected Map<String, Object> makeParameters(Map<String, Object> attributes,
        Map<String, String> parameterAttributes) {
        Map<String, Object> map = super.makeParameters(attributes, parameterAttributes);
        String name = (String) attributes.get("name");
        if (labeled) {
            String bundleName = determineBundleName(attributes);
            if (bundleName == null) {
                throw new IllegalStateException("Unable to locate the label message for the field named [" +
                    name + "]. If you don't have an action class for the URL, you define the bundle to " +
                    "use to localize the form. This bundle is specified either on the control tag or the " +
                    "form tag.");
            }

            String label = messageProvider.getMessage(bundleName, name, locale, parameterAttributes);
            if (label != null) {
                map.put("label", label);
            } else {
                throw new IllegalStateException("Missing localized label for the field named [" + name + "]");
            }

            // Add the field messages and errors as a list or null
            map.put("field_messages", messageStore.getFieldMessages(MessageType.PLAIN).get(name));
            map.put("field_errors", messageStore.getFieldMessages(MessageType.ERROR).get(name));
        }

        return map;
    }

    /**
     * @return  Always null since the input tags don't have a start.
     */
    @Override
    protected String startTemplateName() {
        return null;
    }

    protected String determineBundleName(Map<String, Object> attributes) {
        String bundleName = null;
        if (attributes.containsKey("bundle")) {
            bundleName = (String) attributes.remove("bundle");
        } else if (action != null) {
            bundleName = action.getClass().getName();
        }

        return bundleName;
    }
}