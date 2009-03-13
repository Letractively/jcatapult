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
package org.jcatapult.module.user.action.admin.user;

import java.util.List;

import org.jcatapult.module.user.action.BaseUserFormAction;
import org.jcatapult.module.user.domain.DefaultRole;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.result.form.annotation.FormPrepareMethod;
import org.jcatapult.persistence.service.PersistenceService;

import com.google.inject.Inject;

/**
 * <p>
 * This class prepares the form that adds and edits Users.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action(overridable = true)
public class Prepare extends BaseUserFormAction {
    protected PersistenceService persistenceService;
    public List<DefaultRole> roles;

    @Inject
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @FormPrepareMethod
    public void prepare() {
        roles = persistenceService.findAllByType(DefaultRole.class);
    }
}