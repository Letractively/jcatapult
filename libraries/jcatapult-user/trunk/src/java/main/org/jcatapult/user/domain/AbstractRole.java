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
package org.jcatapult.user.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.jcatapult.mvc.validation.annotation.Required;
import org.jcatapult.persistence.domain.IdentifiableImpl;

/**
 * <p>
 * This class is a mappable super class that provides standard columns for
 * all of the Role methods.
 * </p>
 *
 * <p>
 * The columns are as follows:
 * </p>
 *
 * <table border="1">
 * <tr><th>Name</th><th>Type</th><th>Description</th><th>Required?</th><th>Unique?</th><th>Additional info/constraints</th></tr>
 * <tr><td>name</td><td>varchar(255)</td><td>The name of the Role.</td><td>Yes</td><td>Yes</td><td>None</td></tr>
 * </table>
 *
 * @author  Brian Pontarelli
 */
@MappedSuperclass
public abstract class AbstractRole extends IdentifiableImpl implements Role {
    @Required
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;

        Role that = (Role) o;
        return name.equals(that.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int compareTo(Role o) {
        return name.compareTo(o.getName());
    }
}