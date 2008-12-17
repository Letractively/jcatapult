/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action.account.creditCard;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.security.SecurityContext;

import com.google.inject.Inject;

import org.jcatapult.module.user.service.UpdateResult;
import org.jcatapult.module.user.service.UserService;

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