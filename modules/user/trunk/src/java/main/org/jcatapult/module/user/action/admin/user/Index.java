/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action.admin.user;

import org.jcatapult.mvc.action.annotation.Action;

import com.google.inject.Inject;

import org.jcatapult.crud.action.BaseSearchAction;
import org.jcatapult.module.user.domain.User;
import org.jcatapult.module.user.service.UserHandler;
import org.jcatapult.module.user.service.UserSearchCriteria;

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