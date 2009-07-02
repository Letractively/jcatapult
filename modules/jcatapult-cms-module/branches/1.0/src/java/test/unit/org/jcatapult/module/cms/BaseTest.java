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

import org.jcatapult.module.cms.service.DefaultContentService;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.persistence.test.JPABaseTest;
import org.jcatapult.security.UserAdapter;
import org.jcatapult.security.SecurityContext;
import org.jcatapult.security.EnhancedSecurityContext;
import org.jcatapult.security.servlet.JCatapultSecurityContextProvider;
import org.jcatapult.security.spi.SecurityContextProvider;
import org.jcatapult.security.spi.EnhancedSecurityContextProvider;
import org.junit.Ignore;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import static net.java.util.CollectionTools.*;

/**
 * <p>
 * This is a base test that provides database setup for the tests.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Ignore
public class BaseTest extends JPABaseTest {
    @Inject public DefaultContentService service;
    @Inject public PersistenceService persistenceService;
    protected User publisher = new User();
    protected User editor = new User();

    public BaseTest() {
        addModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(UserAdapter.class).toInstance(new UserAdapter<User>() {
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
                });

                bind(SecurityContextProvider.class).to(JCatapultSecurityContextProvider.class);
                requestStaticInjection(SecurityContext.class);
                bind(EnhancedSecurityContextProvider.class).to(JCatapultSecurityContextProvider.class);
                requestStaticInjection(EnhancedSecurityContext.class);
            }
        });
    }
}