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
package org.jcatapult.module.user.action.account.creditCard;

import org.jcatapult.module.user.service.UpdateResult;
import org.jcatapult.module.user.service.UserService;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.security.SecurityContext;

import com.google.inject.Inject;

/**
 * <p>
 * This class deletes the credit card.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirect(uri = "/account/summary")
@Forward(code = "error", page = "confirm-delete.ftl")
public class Delete {
    private final UserService userService;
    private final MessageStore messageStore;
    public Integer id;

    @Inject
    public Delete(UserService userService, MessageStore messageStore) {
        this.userService = userService;
        this.messageStore = messageStore;
    }

    public String execute() {
        String username = SecurityContext.getCurrentUsername();
        UpdateResult result = userService.deleteCreditCard(id, username);
        if (result == UpdateResult.ERROR || result == UpdateResult.MISSING) {
            messageStore.addActionError(MessageScope.REQUEST, "error");
            return "error";
        }

        return "success";
    }
}