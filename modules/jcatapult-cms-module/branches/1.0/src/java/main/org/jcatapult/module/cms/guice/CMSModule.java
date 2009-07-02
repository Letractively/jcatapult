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
package org.jcatapult.module.cms.guice;

import org.jcatapult.module.cms.result.CMSContentNode;
import org.jcatapult.module.cms.result.CMSMetaNode;
import org.jcatapult.module.cms.result.CMSTitleNode;

import com.google.inject.AbstractModule;

/**
 * <p>
 * This class is a Guice module that sets up the CMS controls.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CMSModule extends AbstractModule {
    /**
     * Configures the controls.
     */
    protected void configure() {
        bind(CMSContentNode.class);
        bind(CMSTitleNode.class);
        bind(CMSMetaNode.class);
    }
}