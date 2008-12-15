/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action.admin.user;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.annotation.ActionPrepareMethod;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.scope.MessageScope;

import com.inversoft.module.user.action.BaseUserFormAction;

/**
 * <p>
 * This class is the action that adds a new User. The majority of
 * the logic is in the {@link BaseUserFormAction} class. This class provides
 * handling of the form rendering when the HTTP method is GET and
 * handles the insertion of the new User when the HTTP method it
 * POST.
 * </p>
 *
 * <p>
 * If the persistence fails, this adds an error message and returns
 * {@code input}. Persistence will only fail when there is a unique
 * key violation on the User Object. Any other failures will result
 * in an exception that is thrown out of the action.
 * </p>
 *
 * <p>
 * After a successful addition, this method will redirect to
 * {@code /admin/user/}.
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
@Redirect(uri = "/admin/user/")
public class Add extends Prepare {

    @ActionPrepareMethod
    public void prepare() {
        super.prepare();
        user = userService.createUser();
    }

    /**
     * Renders the form with a new empty User.
     *
     * @return  The code {@code input}.
     */
    public String get() {
        return "input";
    }

    /**
     * Adds the user.
     *
     * @return  The code {@code input} if there is a unique key violation. The code {@code success}
     *          in all other cases.
     */
    public String post() {
        if (!userService.persist(user, associations, password)) {
            messageStore.addActionError(MessageScope.REQUEST, "unique");
            return "input";
        }

        return "success";
    }
}