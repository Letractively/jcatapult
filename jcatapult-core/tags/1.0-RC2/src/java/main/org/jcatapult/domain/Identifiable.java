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
package org.jcatapult.domain;

/**
 * <p>
 * This interface marks a class as identifiable using a Integer based
 * primary key or unique identifier.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface Identifiable {
    /**
     * @return  The primary key or null if this entity has not been persisted.
     */
    Integer getId();

    /**
     * Sets the new primary key for the enitty.
     *
     * @param   id The primary key.
     */
    void setId(Integer id);
}