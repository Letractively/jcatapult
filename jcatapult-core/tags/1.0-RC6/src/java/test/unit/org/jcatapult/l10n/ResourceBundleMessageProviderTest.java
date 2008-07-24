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
package org.jcatapult.l10n;

import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Test;

import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This class tests the resource bundle message provider.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ResourceBundleMessageProviderTest {
    @Test
    public void testSearch() {
        ResourceBundleMessageProvider provider = new ResourceBundleMessageProvider(Locale.US);
        assertEquals("American English Message", provider.getMessage("org.jcatapult.l10n.Test", "key"));
        assertEquals("Package Message", provider.getMessage("org.jcatapult.l10n.NonExistent", "key"));
        assertEquals("Super Package Message", provider.getMessage("org.jcatapult.badPackage.Test", "key"));

        provider = new ResourceBundleMessageProvider(Locale.GERMAN);
        assertEquals("Default Message", provider.getMessage("org.jcatapult.l10n.Test", "key"));
    }

    @Test
    public void testFormat() {
        Map<String, String> attributes = map("c", "c", "a", "a");
        ResourceBundleMessageProvider provider = new ResourceBundleMessageProvider(Locale.US);
        assertEquals("American English Message b a c", provider.getMessage("org.jcatapult.l10n.Test", "format_key", attributes, "b"));
        assertEquals("Package Message b a c", provider.getMessage("org.jcatapult.l10n.NonExistent", "format_key", attributes, "b"));
        assertEquals("Super Package Message b a c", provider.getMessage("org.jcatapult.badPackage.Test", "format_key", attributes, "b"));

        provider = new ResourceBundleMessageProvider(Locale.GERMAN);
        assertEquals("Default Message b a c", provider.getMessage("org.jcatapult.l10n.Test", "format_key", attributes, "b"));
    }

    @Test
    public void testMissing() {
        ResourceBundleMessageProvider provider = new ResourceBundleMessageProvider(Locale.US);
        try {
            provider.getMessage("org.jcatapult.l10n.Test", "bad_key");
            fail("Should have failed");
        } catch (MissingMessageException e) {
            // Expected
        }
    }
}