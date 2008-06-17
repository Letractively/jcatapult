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
package org.jcatapult.mvc.action;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This class defines the mechanism used to locate action invocation objects.
 * During a request, the invocation is set using the {@link #set(ActionInvocation)}
 * method and then can be retrieved any number of times using the {@link #get()}
 * method.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(DefaultActionInvocationStore.class)
public interface ActionInvocationStore {
    /**
     * Gets the action invocation. Once the action invocation is set using the {@link #set(ActionInvocation)}
     * method, multiple calls to this method for a single request will always return the same value.
     *
     * @return  The action invocation or null if it hasn't been set yet.
     */
    ActionInvocation get();

    /**
     * Sets the invocation into the provider so that it can be fetched later.
     *
     * @param   invocation The invocation to set.
     */
    void set(ActionInvocation invocation);
}