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
package org.jcatapult.module.simpleuser.action.account;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.user.domain.User;
import org.jcatapult.user.service.UserService;

import com.google.inject.Inject;

/**
 * <p>
 * This action generates the account summary page that can be used by
 * application users to view their own account details. This action is
 * secure from hacking because it fetches the user's account based
 * on the username in the {@link SecurityContext} rather than an
 * HTTP parameter.
 * </p>
 *
 * <h3>Localization keys</h3>
 * <p>
 * These keys can be used to override the default error message
 * inside the application.
 * </p>
 * <dl>
 * <dt>error</dt>
 * <dd>The general error message. Added as an action error.</dd>
 * <dt>title</dt>
 * <dd>The title for the HTML page.</dd>
 * <dt>heading</dt>
 * <dd>The heading of the page.</dd>
 * <dt>login</dt>
 * <dd>The heading for the login section of the summary page.</dd>
 * <dt>contact</dt>
 * <dd>The heading for the contact info section of the summary page.</dd>
 * </dl>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
public class Summary {
    private final UserService userService;

    /**
     * The User fetched from the UserService {@link UserService#findByLogin(String)} method.
     */
    public User user;

    @Inject
    public Summary(UserService userService) {
        this.userService = userService;
    }

    /**
     * Fetches the current user by grabbing the current username from the {@link SecurityContext}
     * and then looking up the user from the UserService method {@link UserService#findByLogin(String)}.
     *
     * @return  Always success, even if the user doesn't exist any longer.
     */
    public String execute() {
        String username = SecurityContext.getCurrentUsername();
        user = userService.findByLogin(username);
        return "success";
    }
}