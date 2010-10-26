/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.service.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import com.google.inject.Inject;

/**
 * <p>
 * This class is an EntityManager proxy. This only calls the EntityManagerProvider when it is
 * absolutely necessary. This delays the the creation of the EntityManager until it is actually
 * being used.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class EntityManagerProxy implements EntityManager {
    private final EntityManagerProvider provider;
    private EntityManager proxy;

    @Inject
    public EntityManagerProxy(EntityManagerProvider provider) {
        this.provider = provider;
    }

    public void persist(Object o) {
        proxy = provider.get();
        proxy.persist(o);
    }

    public <T> T merge(T t) {
        proxy = provider.get();
        return proxy.merge(t);
    }

    public void remove(Object o) {
        proxy = provider.get();
        proxy.remove(o);
    }

    public <T> T find(Class<T> tClass, Object o) {
        proxy = provider.get();
        return proxy.find(tClass, o);
    }

    public <T> T getReference(Class<T> tClass, Object o) {
        proxy = provider.get();
        return proxy.getReference(tClass, o);
    }

    public void flush() {
        proxy = provider.get();
        proxy.flush();
    }

    public void setFlushMode(FlushModeType flushModeType) {
        proxy = provider.get();
        proxy.setFlushMode(flushModeType);
    }

    public FlushModeType getFlushMode() {
        proxy = provider.get();
        return proxy.getFlushMode();
    }

    public void lock(Object o, LockModeType lockModeType) {
        proxy = provider.get();
        proxy.lock(o, lockModeType);
    }

    public void refresh(Object o) {
        proxy = provider.get();
        proxy.refresh(o);
    }

    public void clear() {
        proxy = provider.get();
        proxy.clear();
    }

    public boolean contains(Object o) {
        proxy = provider.get();
        return proxy.contains(o);
    }

    public Query createQuery(String s) {
        proxy = provider.get();
        return proxy.createQuery(s);
    }

    public Query createNamedQuery(String s) {
        proxy = provider.get();
        return proxy.createNamedQuery(s);
    }

    public Query createNativeQuery(String s) {
        proxy = provider.get();
        return proxy.createNativeQuery(s);
    }

    public Query createNativeQuery(String s, Class aClass) {
        proxy = provider.get();
        return proxy.createNativeQuery(s, aClass);
    }

    public Query createNativeQuery(String s, String s1) {
        proxy = provider.get();
        return proxy.createNativeQuery(s, s1);
    }

    public void joinTransaction() {
        proxy = provider.get();
        proxy.joinTransaction();
    }

    public Object getDelegate() {
        proxy = provider.get();
        return proxy.getDelegate();
    }

    public void close() {
        proxy = provider.get();
        proxy.close();
    }

    public boolean isOpen() {
        proxy = provider.get();
        return proxy.isOpen();
    }

    public EntityTransaction getTransaction() {
        proxy = provider.get();
        return proxy.getTransaction();
    }
}
