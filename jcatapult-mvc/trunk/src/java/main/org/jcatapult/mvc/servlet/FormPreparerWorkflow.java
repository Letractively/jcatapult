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
package org.jcatapult.mvc.servlet;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the method that is used to prepare forms during
 * result rendering. The Form tag invokes this workflow in order to handle
 * the form preparation step using FormPreparers rather than actions.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(DefaultFormPreparerWorkflow.class)
public interface FormPreparerWorkflow {
    void prepare(String uri);
}