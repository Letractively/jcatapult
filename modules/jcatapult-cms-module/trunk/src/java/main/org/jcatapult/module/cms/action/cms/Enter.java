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
package org.jcatapult.module.cms.action.cms;

import org.jcatapult.config.Configuration;
import org.jcatapult.module.cms.domain.CMSMode;
import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.scope.annotation.Session;

import com.google.inject.Inject;

/**
 * <p>
 * This class puts the CMS into edit mode.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Action
public class Enter {
    @Session public CMSMode cmsMode;
    public String startURI;

    @Inject
    public Enter(Configuration configuration) {
        this.startURI = configuration.getString("inversoft.modules.cms.start-uri", "/");
    }

    public String get() {
        cmsMode = CMSMode.EDIT;
        return "success";
    }
}