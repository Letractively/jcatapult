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
package org.jcatapult.module.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.persistence.domain.IdentifiableImpl;

/**
 * <p>
 * This class maps all the roles of the application.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Entity
@Table(name = "roles")
public class DefaultRole extends IdentifiableImpl implements Role {
    private static final long serialVersionUID = 1;

    @Required
    @Column(unique = true, nullable = false)
    private String name;

    public DefaultRole() {
    }

    public DefaultRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultRole that = (DefaultRole) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }
}