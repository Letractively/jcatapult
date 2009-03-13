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
package org.jcatapult.module.simpleuser.action.admin.user;

import org.jcatapult.crud.action.BaseSearchAction;
import org.jcatapult.module.simpleuser.service.UserSearchCriteria;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.user.domain.User;
import org.jcatapult.user.service.UserHandler;

import com.google.inject.Inject;

/**
 * <p>
 * This class is an action that lists out and sorts the Users.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
public class Index extends BaseSearchAction<User, UserSearchCriteria> {
    private final UserHandler userHandler;

    @Inject
    public Index(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    protected UserSearchCriteria getDefaultCriteria() {
        return new UserSearchCriteria(userHandler.getUserType());
    }
}