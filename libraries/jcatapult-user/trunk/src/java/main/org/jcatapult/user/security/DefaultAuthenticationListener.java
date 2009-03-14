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
package org.jcatapult.user.security;

import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.security.login.AuthenticationListener;
import org.jcatapult.user.domain.AuditableUser;
import org.jcatapult.user.domain.User;
import org.joda.time.DateTime;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the JCatapult security framework to fetch Jcatapult user objects
 * from the database.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultAuthenticationListener implements AuthenticationListener<User> {
    private final PersistenceService persistenceService;

    @Inject
    public DefaultAuthenticationListener(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * If the User is auditable, this updates the last login date and persists the User.
     *
     * @param   user The User.
     */
    public void successfulLogin(User user) {
        if (user instanceof AuditableUser) {
            ((AuditableUser) user).setLastLogin(new DateTime());
            persistenceService.persist(user);
        }
    }

    public void failedLogin(User user) {
    }
}