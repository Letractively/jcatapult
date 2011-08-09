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
package org.jcatapult.security.servlet.saved;

import org.jcatapult.servlet.Workflow;

import com.google.inject.ImplementedBy;

/**
 * <p> This interface defines the portion of the security workflow that handles loading and executing saved requests.
 * Saved requests are requests made after a users session expired, that should be repeated after the user has
 * successfully logged in. </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(DefaultSavedRequestWorkflow.class)
public interface SavedRequestWorkflow extends Workflow {
}