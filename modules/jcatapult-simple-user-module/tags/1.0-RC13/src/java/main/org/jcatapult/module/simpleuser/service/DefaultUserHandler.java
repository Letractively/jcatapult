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
package org.jcatapult.module.simpleuser.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcatapult.config.Configuration;
import org.jcatapult.module.simpleuser.domain.DefaultRole;
import org.jcatapult.module.simpleuser.domain.DefaultUser;
import org.jcatapult.persistence.domain.Identifiable;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.user.service.AbstractUserHandler;

import com.google.inject.Inject;

/**
 * <p>
 * This implements the UserHandler for the Jcatapult default domain
 * classes. These classes are {@link DefaultUser} and {@link DefaultRole}.
 * </p>
 *
 * @author Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class DefaultUserHandler extends AbstractUserHandler<DefaultUser, DefaultRole> {

    @Inject
    public DefaultUserHandler(PersistenceService persistenceService, Configuration configuration) {
        super(persistenceService, configuration);
    }

    /**
     * {@inheritDoc}
     */
    public DefaultUser createUser() {
        return new DefaultUser();
    }

    /**
     * {@inheritDoc}
     */
    public Class<DefaultUser> getUserType() {
        return DefaultUser.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<DefaultRole> getRoleType() {
        return DefaultRole.class;
    }

    /**
     * Gets the roles only.
     *
     * @param   user The user to get the roles from.
     * @return  The Map that contains the role association IDs.
     */
    public Map<String, Integer[]> getAssociationIds(DefaultUser user) {
        Map<String, Integer[]> values = new HashMap<String, Integer[]>();
        List<Integer> rolesIds = new ArrayList<Integer>();
        for (Identifiable ident : user.getRoles()) {
            rolesIds.add(ident.getId());
        }
        values.put("roles", rolesIds.toArray(new Integer[rolesIds.size()]));
        return values;
    }
}