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
package org.jcatapult.module.user.action.admin.user;

import org.jcatapult.module.user.domain.User;
import org.jcatapult.module.user.service.UserService;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;

import com.google.inject.Inject;

/**
 * <p>
 * This class fetches an existing User. This uses the an ID and fetches the
 * User for that ID.
 * </p>
 *
 * <h3>Localization</h3>
 * <p>
 * These keys can be used to override the default error message
 * inside the application.
 * </p>
 * <dl>
 * <dt>unique</dt>
 * <dd>The error message if there is a unique key violation.</dd>
 * </dl>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Forward(code = "error", page = "index.jsp")
public class Details {
    private final UserService userService;
    private final MessageStore messageStore;
    public Integer id;
    public User user;

    @Inject
    public Details(UserService userService, MessageStore messageStore) {
        this.userService = userService;
        this.messageStore = messageStore;
    }

    public String execute() {
        user = userService.findById(id);
        if (user == null) {
            messageStore.addActionError(MessageScope.REQUEST, "missing");
            return "error";
        }

        return "success";
    }
}