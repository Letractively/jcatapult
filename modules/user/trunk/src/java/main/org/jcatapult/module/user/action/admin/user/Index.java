/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.action.admin.user;

import org.jcatapult.mvc.action.annotation.Action;

import com.google.inject.Inject;

import com.inversoft.crud.action.BaseSearchAction;
import com.inversoft.module.user.domain.User;
import com.inversoft.module.user.service.UserHandler;
import com.inversoft.module.user.service.UserSearchCriteria;

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