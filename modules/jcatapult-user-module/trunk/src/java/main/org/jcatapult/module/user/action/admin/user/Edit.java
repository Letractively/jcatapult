/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action.admin.user;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.annotation.ActionPrepareMethod;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.scope.MessageScope;

/**
 * <p>
 * This class is the action that edits existing Users.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(value = "{id}", overridable = true)
@Forward(code = "error", page = "index.ftl")
@Redirect(uri = "/admin/user/")
public class Edit extends Prepare {
    public Integer id;

    @ActionPrepareMethod
    public void prepare() {
        super.prepare();

        if (id != null) {
            user = userService.findById(id);
        }
    }

    public String get() {
        user = userService.findById(id);
        if (user == null) {
            messageStore.addActionError(MessageScope.REQUEST, "missing.user");
            return "error";
        }

        associations = userService.getAssociationIds(user);
        return "input";
    }

    public String post() {
        userService.persist(user, associations, password);
        return "success";
    }
}