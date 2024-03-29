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
package org.jcatapult.user.service;

/**
 * <p>
 * This class stores the various result from a user update.
 * </p>
 *
 * @author Brian Pontarelli
 */
public enum UpdateResult {
    /**
     * Occurs when the user ID supplied has been removed, deleted, etc.
     */
    MISSING,

    /**
     * Occurs when the user could not be updated to the database.
     */
    ERROR,

    /**
     * Occurs when the user is updated successfully.
     */
    SUCCESS
}