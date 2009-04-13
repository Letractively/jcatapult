/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
 */
package org.jcatapult.user.guice;

import org.jcatapult.security.UserAdapter;
import org.jcatapult.security.guice.SecurityModule;
import org.jcatapult.security.login.AuthenticationListener;
import org.jcatapult.security.login.AuthenticationService;
import org.jcatapult.user.security.DefaultAuthenticationListener;
import org.jcatapult.user.security.DefaultAuthenticationService;
import org.jcatapult.user.security.DefaultUserAdapter;

/**
 * <p>
 * This class is a default user module that sets up the the
 * {@link DefaultAuthenticationService} and the {@link DefaultUserAdapter} for the
 * JCatapult security framework.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultUserModule extends SecurityModule {
    @Override
    protected void configure() {
        bind(AuthenticationListener.class).to(DefaultAuthenticationListener.class);
        super.configure();
    }

    protected Class<? extends AuthenticationService> getAuthenticationService() {
        return DefaultAuthenticationService.class;
    }

    protected Class<? extends UserAdapter> getUserAdapter() {
        return DefaultUserAdapter.class;
    }
}
