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
package org.jcatapult.module.cms.action.admin.cms;

import org.jcatapult.config.Configuration;
import org.jcatapult.module.cms.domain.CMSMode;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.scope.annotation.Session;

import com.google.inject.Inject;

/**
 * <p>
 * This class puts the CMS into display mode.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action
@Redirect(uri = "${uri}")
public class Exit {
    private final Configuration configuration;

    @Session public CMSMode cmsMode;
    public String uri;

    @Inject
    public Exit(Configuration configuration) {
        this.configuration = configuration;
    }

    public String get() {
        uri = configuration.getString("jcatapult.cms.exit-uri", "/admin/");
        cmsMode = CMSMode.DISPLAY;
        return "success";
    }
}