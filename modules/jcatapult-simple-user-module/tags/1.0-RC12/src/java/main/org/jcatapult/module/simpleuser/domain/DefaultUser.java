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
package org.jcatapult.module.simpleuser.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.jcatapult.user.domain.AbstractUser;

/**
 * <p>
 * This class is the main Jcatapult User object. It provides both a common set
 * of attributes that are usable for most applications as well as an inheritance
 * strategy for applications that need to add information to the user.
 * </p>
 *
 * <p>
 * This domain maps to the table named <strong>users</strong>. It also provides
 * the inheritance strategy of <strong>SINGLE_TABLE</strong> since this will be
 * the most common. Most applications will not have multiple user types and if
 * they do, they probably won't be using a pre-built module for them.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@Table(name="users")
@SuppressWarnings("unchecked")
public class DefaultUser extends AbstractUser<DefaultRole> {
    private static final long serialVersionUID = 1;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Set<DefaultRole> roles = new HashSet<DefaultRole>();

    public Set<DefaultRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<DefaultRole> roles) {
        this.roles = roles;
    }

    public void addRole(DefaultRole role) {
        this.roles.add(role);
    }

    // ------------------ Helpers ------------------------

    /**
     * Convienence method to figure out if a user has the given role.
     *
     * @param   role The user role to check for.
     * @return  True if the user has it, false otherwise.
     */
    public boolean hasRole(String role) {
        for (DefaultRole r : roles) {
            if (r.getName().equals(role)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return  True if the user is an admin. This checks for the role named <strong>admin</strong>.
     */
    public boolean isAdmin() {
        return hasRole("admin");
    }
}