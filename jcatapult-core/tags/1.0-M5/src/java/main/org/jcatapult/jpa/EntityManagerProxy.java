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
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

/**
 * <p>
 * This class is a proxy that allows an EntityManager to be injected into
 * services but lazily initialized from the {@link EntityManagerContext}.
 * The issue is that if a service needs an EntityManager and the
 * {@link org.jcatapult.servlet.JCatapultFilter} hasn't been invoked yet,
 * the {@link EntityManagerContext} will be empty, which causes Guice to
 * explode. This class allows the injection of the EntityManager to occur
 * and the the EntityManagerContext to be called only when the methods
 * are invoked, which <strong>SHOULD</strong> always be during a request
 * that goes through the filter.
 * </p>
 *
 * <p>
 * <strong>NOTE:</strong> This class is NOT thread safe in anyway at all!
 * Don't even think about using it across threads or you will suffer major
 * consequences. You have been warned ;)
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class EntityManagerProxy implements EntityManager {
    private EntityManager entityManager;

    public void persist(Object o) {
        init();
        entityManager.persist(o);
    }

    public <T> T merge(T t) {
        init();
        return entityManager.merge(t);
    }

    public void remove(Object o) {
        init();
        entityManager.remove(o);
    }

    public <T> T find(Class<T> tClass, Object o) {
        init();
        return entityManager.find(tClass, o);
    }

    public <T> T getReference(Class<T> tClass, Object o) {
        init();
        return entityManager.getReference(tClass, o);
    }

    public void flush() {
        init();
        entityManager.flush();
    }

    public void setFlushMode(FlushModeType flushModeType) {
        init();
        entityManager.setFlushMode(flushModeType);
    }

    public FlushModeType getFlushMode() {
        init();
        return entityManager.getFlushMode();
    }

    public void lock(Object o, LockModeType lockModeType) {
        init();
        entityManager.lock(o, lockModeType);
    }

    public void refresh(Object o) {
        init();
        entityManager.refresh(o);
    }

    public void clear() {
        init();
        entityManager.clear();
    }

    public boolean contains(Object o) {
        init();
        return entityManager.contains(o);
    }

    public Query createQuery(String s) {
        init();
        return entityManager.createQuery(s);
    }

    public Query createNamedQuery(String s) {
        init();
        return entityManager.createNamedQuery(s);
    }

    public Query createNativeQuery(String s) {
        init();
        return entityManager.createNativeQuery(s);
    }

    public Query createNativeQuery(String s, Class aClass) {
        init();
        return entityManager.createNativeQuery(s, aClass);
    }

    public Query createNativeQuery(String s, String s1) {
        init();
        return entityManager.createNativeQuery(s, s1);
    }

    public void joinTransaction() {
        init();
        entityManager.joinTransaction();
    }

    public Object getDelegate() {
        init();
        return entityManager.getDelegate();
    }

    public void close() {
        init();
        entityManager.close();
    }

    public boolean isOpen() {
        init();
        return entityManager.isOpen();
    }

    public EntityTransaction getTransaction() {
        init();
        return entityManager.getTransaction();
    }

    protected void init() {
        if (entityManager == null) {
            entityManager = EntityManagerContext.get();
        }
    }
}