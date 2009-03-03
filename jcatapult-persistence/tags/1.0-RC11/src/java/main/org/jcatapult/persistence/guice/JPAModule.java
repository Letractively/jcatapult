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
 */
package org.jcatapult.persistence.guice;

import javax.persistence.EntityManager;

import org.jcatapult.persistence.service.jpa.EntityManagerProvider;
import org.jcatapult.persistence.txn.TransactionMethodInterceptor;
import org.jcatapult.persistence.txn.annotation.Transactional;

import com.google.inject.AbstractModule;
import static com.google.inject.matcher.Matchers.*;

/**
 * <p>
 * This module should be used when JPA is required. This sets up the EntityManager
 * instances for injection using the {@link org.jcatapult.persistence.service.jpa.EntityManagerContext}
 * thread local. This thread local is setup by the
 * {@link org.jcatapult.servlet.JCatapultFilter} in web applications and
 * needs to be setup manually in other applications.
 * </p>
 *
 * @author  Brian Pontarelli and James Humphrey
 */
public class JPAModule extends AbstractModule {
    /**
     * Calls super then configures JPA.
     */
    @Override
    protected void configure() {
        configureJPA();
    }

    /**
     * Sets up the JPA {@link org.jcatapult.persistence.service.jpa.EntityManagerProvider} and also bind the
     * {@link Transactional} annotation to the {@link org.jcatapult.persistence.txn.TransactionMethodInterceptor}.
     */
    protected void configureJPA() {
        bind(EntityManager.class).toProvider(new EntityManagerProvider());
        bindInterceptor(any(), annotatedWith(Transactional.class), new TransactionMethodInterceptor());
    }
}
