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
package org.jcatapult.module.cms;

import java.util.Set;

import org.jcatapult.email.EmailTestHelper;
import org.jcatapult.module.cms.service.ContentService;
import org.jcatapult.persistence.test.JPABaseTest;
import org.jcatapult.security.UserAdapter;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.security.servlet.JCatapultSecurityContextProvider;
import org.jcatapult.security.spi.SecurityContextProvider;
import org.jcatapult.security.spi.EnhancedSecurityContextProvider;
import org.junit.Ignore;

import com.google.inject.Inject;
import com.google.inject.AbstractModule;
import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This is a base integration test.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Ignore
public class BaseFunctionalTest extends JPABaseTest {
    @Inject public ContentService contentService;
    protected User publisher = new User();
    protected User editor = new User();
    protected UserAdapter<User> userAdapter;

    public BaseFunctionalTest() {
        EmailTestHelper.setup(this);
        
        userAdapter = new UserAdapter<User>() {
            public String getUsername(User user) {
                return "foo";
            }

            public String getPassword(User user) {
                return "bar";
            }

            public Set<String> getRoles(User user) {
                if (user == publisher) {
                    return set("publisher");
                }

                return set("editor");
            }
        };

        addModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(UserAdapter.class).toInstance(userAdapter);

                bind(SecurityContextProvider.class).to(JCatapultSecurityContextProvider.class);
                requestStaticInjection(SecurityContext.class);
                bind(EnhancedSecurityContextProvider.class).to(JCatapultSecurityContextProvider.class);
                requestStaticInjection(EnhancedSecurityContext.class);
            }
        });
    }
}