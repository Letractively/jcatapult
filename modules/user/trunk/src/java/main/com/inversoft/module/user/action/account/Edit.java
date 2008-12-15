/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action.account;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.annotation.ActionPrepareMethod;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.security.SecurityContext;

import com.inversoft.module.user.action.BaseUserFormAction;
import com.inversoft.module.user.service.UpdateResult;
import com.inversoft.module.user.service.UserService;

/**
 * <p>
 * This action generates the edit users form that can be used by
 * application users to edit their own account. This action is
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
 * <dd>The error message if the user update fails. Added as an action error.</dd>
 * <dt>title</dt>
 * <dd>The title for the HTML page.</dd>
 * <dt>heading</dt>
 * <dd>The heading of the page.</dd>
 * <dt>password</dt>
 * <dd>A message to tell the user that they can leave the password field
 *  blank to keep their current pasword.</dd>
 * </dl>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirect(uri = "summary")
public class Edit extends BaseUserFormAction {
    /**
     * Turns off password checking by setting the variable {@link #checkPassword} to false.
     */
    public Edit() {
        checkPassword = false;
    }

    /**
     * Creates a new user so that it can be edited.
     */
    @ActionPrepareMethod
    public void prepare() {
        user = userService.createUser();
    }

    /**
     * Handles form render and grabs the user.
     *
     * @return  {@code input}.
     */
    public String get() {
        user = userService.currentUser();
        return "input";
    }

    /**
     * Handles form submission and updates the user.
     *
     * @return  The result, either {@code error} or {@code success}. {@code error} is only returned
     *          if the update failed according to the method
     *          {@link UserService#update(com.inversoft.module.user.domain.User, String)}.
     */
    public String post() {
        UpdateResult result = userService.update(user, password);
        if (result == UpdateResult.ERROR) {
            messageStore.addActionError(MessageScope.REQUEST, "error");
            return "error";
        }

        return "success";
    }
}