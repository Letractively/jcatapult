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
package org.jcatapult.mvc.result.form;

/**
 * <p>
 * This interface defines the method that forms can be prepared.
 * Form preparation is generally handled by a class that is properly
 * annotated with the {@link FormPreparer} annotation and that provides
 * a single prepare method that conforms to the same signature as
 * the action classes do, allowing for different handling for post,
 * get and extensions.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface FormPreparer {
    /**
     * Prepare the form using the FormPreparer that is registered under the given URI.
     *
     * @param   uri The URI of the FormPreparer.
     */
    void prepare(String uri);
}