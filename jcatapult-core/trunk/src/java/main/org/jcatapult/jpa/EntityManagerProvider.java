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
package org.jcatapult.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Provider;

/**
 * <p>
 * This class is a Guice provider that provider access to the
 * {@link EntityManagerContext} thread local holder for JPA
 * EntityManager. This allows the current JPA EntityManager to
 * be injected into any Guice created class.
 * </p>
 *
 * @author  James Humphrey and Brian Pontarelli
 */
public class EntityManagerProvider implements Provider<EntityManager> {
    public EntityManager get() {
        return new EntityManagerProxy();
    }
}