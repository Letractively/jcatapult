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
package org.jcatapult.module.cms.service;

import org.jcatapult.module.cms.domain.Node;

/**
 * <p>
 * This is an interface that models all of the results. This is useful
 * for figuring out if any action was a success or not.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface Result<T extends Node> {
    /**
     * @return  True if the action was successful, false if not.
     */
    boolean isSuccess();

    /**
     * @return  True if the action was successful but is now pending approval.
     */
    boolean isPending();

    T getNode();
}