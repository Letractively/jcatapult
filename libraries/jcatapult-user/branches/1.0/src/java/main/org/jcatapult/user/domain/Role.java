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
package org.jcatapult.user.domain;

import java.io.Serializable;

import org.jcatapult.persistence.domain.Identifiable;

/**
 * <p>
 * This is a marker interface for roles.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface Role extends Identifiable, Comparable<Role>, Serializable {
    /**
     * @return  The name of the role.
     */
    String getName();

    /**
     * Sets the role name.
     *
     * @param   name The role name.
     */
    void setName(String name);
}