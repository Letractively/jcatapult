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
package org.jcatapult.module.user.service;

import java.util.Map;
import java.util.Set;

import org.jcatapult.module.user.domain.Role;
import org.jcatapult.module.user.domain.User;

import com.google.inject.ImplementedBy;
import net.java.error.ErrorList;

/**
 * <p>
 * This class provides the implementation specific handling of User
 * instances. It also provides access to the implementation types.
 * This is the best place to provide custom User and Role implementation
 * support rather than using the Jcatapult defaults.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(DefaultUserHandler.class)
public interface UserHandler<T extends User, U extends Role> {
    /**
     * @return  An instance of the User implementation.
     */
    T createUser();

    /**
     * @return  The user type.
     */
    Class<T> getUserType();

    /**
     * @return  The role type.
     */
    Class<U> getRoleType();

    /**
     * @return  The set of default association IDs that the User should have. This should not include
     *          the roles.
     */
    Map<String, Integer[]> getDefaultAssociations();

    /**
     * @return  The set of default roles that the User should have.
     */
    Set<Role> getDefaultRoles();

    /**
     * Finds and adds the associated Objects to the given user based on the IDs in the Map.
     *
     * @param   user The User to setup the associations for.
     * @param   associations The Map of association IDs.
     */
    void associate(T user, Map<String, Integer[]> associations);

    /**
     * Validates the User.
     *
     * @param   user The User to validate.
     * @param   associations The map of association IDs to validate along with the User.
     * @param   existing Determines if the validation is for an existing user or a new user.
     * @param   password (Optional) The password to validate.
     * @param   passwordConfirm (Optional) The password confirmation to validate.
     * @return  Any errors found.
     */
    ErrorList validate(T user, Map<String, Integer[]> associations, boolean existing, String password,
        String passwordConfirm);

    /**
     * Prepares the given User object to be persisted. This method is useful when a User has OneToMany
     * associations with another entity and a mappedBy value. In these cases the associated entity
     * will contain a reference back to the User. In order to handle these cases the User must be
     * set into those associated objects. This method can set up those associations.
     *
     * @param   user The user to prepare.
     */
    void prepare(T user);

    /**
     * Fetches the association IDs from the given User object.
     *
     * @param   user The user to get the associations for.
     * @return  The associations Map.
     */
    Map<String, Integer[]> getAssociationIds(T user);
}