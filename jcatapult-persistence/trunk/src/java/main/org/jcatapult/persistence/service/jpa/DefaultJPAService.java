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
package org.jcatapult.persistence.service.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.jcatapult.guice.Closable;

/**
 * <p>
 * This class implements the JPA service. It is a singleton and in the constructor
 * it sets up the EntityManagerFactory.
 * </p>
 * <p>
 * This class is a singleton since it constructs the EntityManagerFactory in the
 * constructor and holds a reference to it.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Singleton
public class DefaultJPAService implements JPAService, Closable {
    private static final Logger logger = Logger.getLogger(DefaultJPAService.class.getName());
    private EntityManagerFactory emf;

    /**
     * @param   jpaEnabled If true, JPA will be setup, false it will not. A boolean flag controlled
     *          by the jcatapult configuration property named <strong>jcatapult.jpa.enabled</strong>
     *          that controls whether or not JPA will be initialized and then setup during each request.
     * @param   persistentUnit  The name of the JPA persistent unit to use if JPA is being setup.
     *          This is controlled by the jcatapult configuration property named <strong>jcatapult.jpa.unit</strong>.
     */
    @Inject
    public DefaultJPAService(@Named("jcatapult.jpa.enabled") boolean jpaEnabled,
                             @Named("jcatapult.jpa.unit") String persistentUnit) {
        logger.fine("JPA is " + (jpaEnabled ? "enabled" : "disabled"));

        if (jpaEnabled) {
            emf = Persistence.createEntityManagerFactory(persistentUnit);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManagerFactory getFactory() {
        return emf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityManager setupEntityManager() {
        EntityManager em = EntityManagerContext.get();
        if (em != null) {
            return em;
        }
        
        if (emf != null) {
            em = emf.createEntityManager();
            EntityManagerContext.set(em);
            return em;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDownEntityManager() {
        // Clear out the context just to be safe.
        EntityManager entityManager = EntityManagerContext.get();
        if (entityManager != null) {
            EntityManagerContext.remove();
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (emf != null) {
            emf.close();
        }
    }
}
