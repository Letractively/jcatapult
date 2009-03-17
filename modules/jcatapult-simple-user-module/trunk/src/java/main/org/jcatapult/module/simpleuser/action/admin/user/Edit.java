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
package org.jcatapult.module.simpleuser.action.admin.user;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.annotation.ActionPrepareMethod;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.module.simpleuser.domain.DefaultUser;

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
            user = (DefaultUser) userService.findById(id);
        }
    }

    public String get() {
        user = (DefaultUser) userService.findById(id);
        if (user == null) {
            messageStore.addActionError(MessageScope.REQUEST, "missing.user");
            return "error";
        }

        associations = userService.getAssociationIds(id);
        return "input";
    }

    public String post() {
        if (!userService.persist(user, associations, password)) {
            messageStore.addFieldError(MessageScope.REQUEST, "user.username", "user.username.exists");
            return "input";
        }

        return "success";
    }
}