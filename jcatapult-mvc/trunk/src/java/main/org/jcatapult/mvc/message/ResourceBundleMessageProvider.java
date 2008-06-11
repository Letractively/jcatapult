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
package org.jcatapult.mvc.message;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>
 * This implements the MessageProvider using ResourceBundles. However, it adds
 * the additional step of looking for multiple bundle if the message isn't
 * initially found. The search method is:
 * </p>
 *
 * <pre>
 * bundle = com.example.action.Foo
 * locale = en_US
 *
 * com.example.action.Foo_en_US
 * com.example.action.Foo_en
 * com.example.action.Foo
 * com.example.action.package_en_US
 * com.example.action.package_en
 * com.example.action.package
 * com.example.package_en_US
 * com.example.package_en
 * com.example.package
 * ...
 * </pre>
 *
 * <p>
 * This continues to look up packages until if finds the message.
 * </p>
 *
 * TODO add toekn replacement
 *
 * @author  Brian Pontarelli
 */
public class ResourceBundleMessageProvider implements MessageProvider {
    /**
     * {@inheritDoc}
     */
    public String getMessage(String bundle, String key, Locale locale, Map<String, String> attributes,
            String... values) {
        String message = getMessage(bundle, key, locale);
        if (message != null) {
            // Token replace
        }

        return message;
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(String bundle, String key, Locale locale) {
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle(bundle, locale);
        } catch (MissingResourceException e) {
        }

        if (rb == null) {
            int index = bundle.lastIndexOf('.');
            if (index == -1) {
                return null;
            }

            String baseName = bundle.substring(0, index);
            while (rb == null) {
                try {
                    rb = ResourceBundle.getBundle(baseName + ".package", locale);
                } catch (MissingResourceException e) {
                    index = baseName.lastIndexOf('.');
                    if (index == -1) {
                        break;
                    }

                    bundle = bundle.substring(0, index);

                }
            }
        }

        if (rb != null) {
            try {
                return rb.getString(key);
            } catch (MissingResourceException e) {
            }
        }

        return null;
    }
}