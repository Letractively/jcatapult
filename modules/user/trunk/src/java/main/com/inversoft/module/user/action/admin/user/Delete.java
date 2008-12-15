/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action.admin.user;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;

import com.google.inject.Inject;

import com.inversoft.module.user.service.UserService;

/**
 * <p>
 * This class is the action that deletes one or more User(s)
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
@Redirect(uri = "/admin/user/")
public class Delete {
    private final UserService userService;
    public int[] ids;

    @Inject
    public Delete(UserService userService) {
        this.userService = userService;
    }

    public String execute() {
        if (ids != null && ids.length > 0) {
            userService.deleteMany(ids);
        }

        return "success";
    }
}