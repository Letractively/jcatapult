/*
 * Copyright (c) 2007, Inversoft LLC, All Rights Reserved
 */
package org.jcatapult.module.user.action.admin.user;

import java.util.List;
import java.util.Map;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.result.form.annotation.FormPrepareMethod;

import org.jcatapult.module.user.action.BaseUserFormAction;

/**
 * <p>
 * This class prepares the form that adds and edits Users.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
public class Prepare extends BaseUserFormAction {
    public Map<String, List<?>> items;

    @FormPrepareMethod
    public void prepare() {
        items = userService.getAssociationObjects();
    }
}