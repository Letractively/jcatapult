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
package org.jcatapult.user.service;

import java.util.ArrayList;
import static java.util.Arrays.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.PersistenceException;

import org.jcatapult.config.Configuration;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.user.domain.Role;
import org.jcatapult.user.domain.User;

import com.google.inject.Inject;
import net.java.error.ErrorList;

/**
 * <p>
 * This class is an abstract base class that provides some of the methods
 * of the UserHandler.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractUserHandler<T extends User<U>, U extends Role> implements UserHandler<T, U> {
    protected final PersistenceService persistenceService;
    protected String defaultRoleName;

    @Inject
    public AbstractUserHandler(PersistenceService persistenceService, Configuration configuration) {
        this.persistenceService = persistenceService;
        this.defaultRoleName = configuration.getString("jcatapult.user.default-role", "user");
    }

    /**
     * @return  An empty map, since the basic of all cases is that the User has no associations.
     */
    public Map<String, int[]> getDefaultAssociations() {
        return new HashMap<String,int[]>();
    }

    /**
     * @return  This returns a Set of roles that contains a single role name is equal to the value
     *          from the Configuration under the key <strong>jcatapult.user.default-role</strong>.
     */
    public Set<U> getDefaultRoles() {
        U role = persistenceService.queryFirst(getRoleType(),
            "select r from " + getRoleType().getSimpleName() + " r where r.name = ?1", "user");
        if (role == null) {
            return new HashSet<U>();
        }

        return new HashSet<U>(asList(role));
    }

    /**
     * This pulls the IDs from the given map under the key <strong>roles</strong> and fetches all the
     * Roles with those IDS and stuffs them into the User.
     *
     * @param   user The User to setup the associations for.
     * @param   associations The Map of association IDs.
     */
    public void associate(T user, Map<String, int[]> associations) {
        int[] roleIds = associations.get("roles");
        if (roleIds != null && roleIds.length > 0) {
            List<Integer> ids = new ArrayList<Integer>();
            for (int roleId : roleIds) {
                ids.add(roleId);
            }
            
            List<U> roles = persistenceService.queryAll(getRoleType(),
                "select r from " + getRoleType().getSimpleName() + " r where r.id in (?1)", ids);
            for (U role : roles) {
                ids.remove(role.getId());
            }

            if (ids.size() > 0) {
                throw new PersistenceException("Invalid role ID(s) " + ids);
            }
            
            user.getRoles().addAll(roles);
        }
    }

    /**
     * Does nothing since the Abstract classes have validation annotations and anyone implementing
     * the interfaces can use the annotations or override this method.
     *
     * @param   user The user.
     * @param   associations The association IDs.
     * @param   existing Whether or not the user is existing or not.
     * @param   password The password.
     * @param   passwordConfirm The password confirm.
     * @return  Any validation errors.
     */
    public ErrorList validate(T user, Map<String, int[]> associations, boolean existing, String password, String passwordConfirm) {
        return null;
    }

    /**
     * Does nothing.
     *
     * @param   user The user.
     */
    public void prepare(T user) {
    }

    /**
     * Fetches all of the roles in the given User and places an array in the Map under the key
     * <strong>roles</strong> that contains all of the IDs for those roles.
     *
     * @param   id The ID of the user.
     * @return  The map of associations.
     */
    public Map<String, int[]> getAssociationIds(int id) {
        T user = persistenceService.findById(getUserType(),id);
        Map<String, int[]> values = new HashMap<String, int[]>();
        int[] ids = new int[user.getRoles().size()];
        int index = 0;
        for (U testRole : user.getRoles()) {
            ids[index++] = testRole.getId();
        }
        values.put("roles", ids);
        return values;
    }
}
