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
package org.jcatapult.mvc.messages;

import java.util.Locale;
import java.util.Map;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the mechanism by which errors are added and fetched.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(DefaultMessageStore.class)
public interface MessageStore {
    /**
     * Usually called by the {@link org.jcatapult.mvc.parameters.ParameterWorkflow}, this method adds
     * a conversion error for the given parameter. The values can be token replaced within the localized
     * error message.
     *
     * @param   field The name of the field that the conversion error failed for.
     * @param   locale The locale used for localization.
     * @param   attributes The parameter attributes, which might be useful for error messaging stuff
     * @param   values The values attempting to be set into the field.
     * @throws  MissingMessageException If the conversion message is missing.
     */
    void addConversionError(String bundle, String field, Locale locale, Map<String, String> attributes, String... values)
    throws MissingMessageException ;
}