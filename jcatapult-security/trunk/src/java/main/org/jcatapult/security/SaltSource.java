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
package org.jcatapult.security;

/**
 * <p>
 * This interface defines a method for retrieving a salt that can be
 * used for encryption of passwords.
 * </p>
 *
 * @author Brian Pontarelli
 */
public interface SaltSource<T> {
    /**
     * Retrieves the default salt for the salt source. This salt should always be the same no matter
     * how many times this method is invoked. This makes the salt less secure but easier to manage.
     *
     * @return  The default salt.
     */
    String getSalt();

    /**
     * Returns a salt that is applicable to the specific object given. This salt source is generally
     * more secure because the salt is based on a variable object. However, this method must return
     * the same salt for the same object no matter how many times this method is invoked.
     *
     * @param   t The object used to get the salt.
     * @return  The salt.
     */
    String getSalt(T t);
}