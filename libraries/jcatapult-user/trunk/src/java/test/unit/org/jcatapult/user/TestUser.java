/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.user;

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
 * This class is an entity for testing the user service.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Entity
@Table(name = "test_user")
public class TestUser extends AbstractUser<TestRole> {
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Set<TestRole> roles = new HashSet<TestRole>();
    
    public Set<TestRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<TestRole> roles) {
        this.roles = roles;
    }

    public void addRole(TestRole role) {
        this.roles.add(role);
    }
}