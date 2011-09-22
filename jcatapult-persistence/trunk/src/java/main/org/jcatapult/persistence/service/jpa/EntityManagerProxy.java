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
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

import java.util.Map;

import com.google.inject.Inject;

/**
 * <p> This class is an EntityManager proxy. This only calls the EntityManagerProvider when it is absolutely necessary.
 * This delays the the creation of the EntityManager until it is actually being used. </p>
 *
 * @author Brian Pontarelli
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

  // JPA 2.0 methods

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
    proxy = provider.get();
    return proxy.find(entityClass, primaryKey, properties);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
    proxy = provider.get();
    return proxy.find(entityClass, primaryKey, lockMode);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
    proxy = provider.get();
    return proxy.find(entityClass, primaryKey, lockMode, properties);
  }

  @Override
  public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
    proxy = provider.get();
    proxy.lock(entity, lockMode, properties);
  }

  @Override
  public void refresh(Object entity, Map<String, Object> properties) {
    proxy = provider.get();
    proxy.refresh(entity, properties);
  }

  @Override
  public void refresh(Object entity, LockModeType lockMode) {
    proxy = provider.get();
    proxy.refresh(entity, lockMode);
  }

  @Override
  public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
    proxy = provider.get();
    proxy.refresh(entity, lockMode, properties);
  }

  @Override
  public void detach(Object entity) {
    proxy = provider.get();
    proxy.detach(entity);
  }

  @Override
  public LockModeType getLockMode(Object entity) {
    proxy = provider.get();
    return proxy.getLockMode(entity);
  }

  @Override
  public void setProperty(String propertyName, Object value) {
    proxy = provider.get();
    proxy.setProperty(propertyName, value);
  }

  @Override
  public Map<String, Object> getProperties() {
    proxy = provider.get();
    return proxy.getProperties();
  }

  @Override
  public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
    proxy = provider.get();
    return proxy.createQuery(criteriaQuery);
  }

  @Override
  public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
    proxy = provider.get();
    return proxy.createQuery(qlString, resultClass);
  }

  @Override
  public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
    proxy = provider.get();
    return proxy.createNamedQuery(name, resultClass);
  }

  @Override
  public <T> T unwrap(Class<T> cls) {
    proxy = provider.get();
    return proxy.unwrap(cls);
  }

  @Override
  public EntityManagerFactory getEntityManagerFactory() {
    proxy = provider.get();
    return proxy.getEntityManagerFactory();
  }

  @Override
  public CriteriaBuilder getCriteriaBuilder() {
    proxy = provider.get();
    return proxy.getCriteriaBuilder();
  }

  @Override
  public Metamodel getMetamodel() {
    proxy = provider.get();
    return proxy.getMetamodel();
  }
}
