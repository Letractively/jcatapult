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

import java.lang.annotation.Annotation;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.jcatapult.domain.Identifiable;
import org.jcatapult.domain.SoftDeletable;
import org.jcatapult.persistence.PersistenceService;
import org.jcatapult.persistence.Transaction;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the default implementation of the PersistenceService
 * and provides default JPA behavior. It requires that the
 * {@link EntityManagerContext} be constructed correctly using the
 * filter as described in that classes comments (click on the link
 * and read up on how to setup the filter).
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings(value = {"unchecked"})
public class JPAPersistenceService implements PersistenceService {
    private EntityManager entityManager;
    private boolean verifyEntityClasses = true;

    /**
     * Constructs a new JPAPersistenceService that uses the given EntityManager to communicate
     * with the database.
     *
     * @param   entityManager The entity manager to use.
     */
    @Inject
    public JPAPersistenceService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setVerifyEntityClasses(boolean verifyEntityClasses) {
        this.verifyEntityClasses = verifyEntityClasses;
    }

    /**
     * {@inheritDoc}
     */
    public void clearCache() {
        entityManager.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void reload(Object obj) {
        entityManager.refresh(obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object obj) {
        return entityManager.contains(obj);
    }

    /**
     * Starts a new {@link EntityTransaction} if the current {@link EntityManager} doesn't already have
     * an active transaction. If it does have an active transaction, then it still creates a transaction
     * but it is a proxy that will ignore commit and rollback calls since it is effectively an outer
     * transaction.
     *
     * @return  The transaction.
     */
    public Transaction startTransaction() {
        return startTransaction(entityManager);
    }

    private Transaction startTransaction(EntityManager em) {
        final EntityTransaction transaction = em.getTransaction();
        final boolean local = !transaction.isActive();
        if (local) {
            transaction.begin();
        }

        return new Transaction() {
            public void commit() {
                if (local) {
                    transaction.commit();
                }
            }

            public void rollback() {
                if (local) {
                    transaction.rollback();
                }
            }

            public void setRollbackOnly() {
                transaction.setRollbackOnly();
            }

            public boolean getRollbackOnly() {
                return transaction.getRollbackOnly();
            }

            public boolean isActive() {
                return transaction.isActive();
            }
        };
    }

    /**
     * Locks the object using the JPA {@link EntityManager#lock(Object,LockModeType)} method on the entity manager.
     *
     * @param   obj The object to lock.
     * @param   read The read flag to determine the lock mode.
     */
    public void lock(Object obj, boolean read) {
        if (read) {
            entityManager.lock(obj, LockModeType.READ);
        } else {
            entityManager.lock(obj, LockModeType.WRITE);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> findAllByType(Class<T> type) {
        return findAllByTypeInternal(type, true);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends SoftDeletable> List<T> findAllByType(Class<T> type, boolean includeDeleted) {
        return findAllByTypeInternal(type, includeDeleted);
    }

    /**
     * This is the internal method that performs the find but also determines if the Object is a
     * {@link org.jcatapult.domain.SoftDeletable} and appends "where eb.deleted = false" if the includeDeleted is false.
     *
     * @param   type The type to find.
     * @param   includeDeleted Determines if this should return all the instances of the Object
     *          including those instances that are marked as deleted and are {@link org.jcatapult.domain.SoftDeletable}
     *          objects.
     * @return  The list.
     */
    protected <T> List<T> findAllByTypeInternal(Class<T> type, boolean includeDeleted) {
        verify(type);
        StringBuilder queryString = new StringBuilder("select eb from ").append(stripPackage(type)).append(" eb");
        if (SoftDeletable.class.isAssignableFrom(type) && !includeDeleted) {
            queryString.append(" where eb.deleted = false");
        }

        Query q = entityManager.createQuery(queryString.toString());
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public <T> long count(Class<T> type) {
        return countInternal(type, true);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends SoftDeletable> long count(Class<T> type, boolean includeDeleted) {
        return countInternal(type, includeDeleted);
    }

    /**
     * This is the internal method that performs the count but also determines if the Object is a
     * {@link org.jcatapult.domain.SoftDeletable} and appends "where eb.deleted = false" if the includeDeleted is false.
     *
     * @param   type The type to count.
     * @param   includeDeleted Determines if this should count all the instances of the Object
     *          including those instances that are marked as deleted and are {@link org.jcatapult.domain.SoftDeletable}
     *          objects.
     * @return  The count.
     */
    protected <T> long countInternal(Class<T> type, boolean includeDeleted) {
        verify(type);
        StringBuilder queryString = new StringBuilder("select count(eb) from ").append(stripPackage(type)).append(" eb");
        if (SoftDeletable.class.isAssignableFrom(type) && !includeDeleted) {
            queryString.append(" where eb.deleted = false");
        }

        Query q = entityManager.createQuery(queryString.toString());
        return (Long) q.getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> findByType(Class<T> type, int start, int number) {
        return findByTypeInternal(type, start, number, true);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends SoftDeletable> List<T> findByType(Class<T> type, int start, int number, boolean includeInactive) {
        return findByTypeInternal(type, start, number, includeInactive);
    }

    /**
     * This is the internal method that handles finding by type. If the type is {@link org.jcatapult.domain.SoftDeletable}
     * this and the includeInactive flag is false method also appends "where eb.active = true" to
     * the query.
     *
     * @param   type The type to find.
     * @param   start The start location within the results for pagination.
     * @param   number The number to fetch.
     * @param   includeDeleted Determines if this should return all the instances of the Object
     *          including those instances that are marked as deleted and are {@link org.jcatapult.domain.SoftDeletable}
     *          objects.
     * @return  The list of objects found.
     */
    protected <T> List<T> findByTypeInternal(Class<T> type, int start, int number, boolean includeDeleted) {
        verify(type);

        StringBuilder queryString = new StringBuilder("select eb from ").append(stripPackage(type)).append(" eb");
        if (SoftDeletable.class.isAssignableFrom(type) && !includeDeleted) {
            queryString.append(" where eb.deleted = false");
        }

        Query q = entityManager.createQuery(queryString.toString());
        q.setFirstResult(start);
        q.setMaxResults(number);
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> queryAll(Class<T> type, String query, Object... params) {
        verify(type);
        Query q = entityManager.createQuery(query);
        addParams(q, params);
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> query(Class<T> type, String query, int start, int number, Object... params) {
        verify(type);
        Query q = entityManager.createQuery(query);
        addParams(q, params);
        q.setFirstResult(start);
        q.setMaxResults(number);
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T queryFirst(Class<T> type, String query, Object... params) {
        verify(type);
        Query q = entityManager.createQuery(query);
        q.setFirstResult(0);
        q.setMaxResults(1);
        addParams(q, params);
        List<T> results = q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public long queryCount(String query, Object... params) {
        Query q = entityManager.createQuery(query);
        addParams(q, params);
        return (Long) q.getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> namedQueryAll(Class<T> type, String query, Object... params) {
        verify(type);
        Query q = entityManager.createNamedQuery(query);
        addParams(q, params);
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> namedQuery(Class<T> type, String query, int start, int number, Object... params) {
        verify(type);
        Query q = entityManager.createNamedQuery(query);
        addParams(q, params);
        q.setFirstResult(start);
        q.setMaxResults(number);
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T namedQueryFirst(Class<T> type, String query, Object... params) {
        verify(type);
        Query q = entityManager.createNamedQuery(query);
        addParams(q, params);
        List<T> results = q.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Identifiable> T findById(Class<T> type, int id) {
        verify(type);
        T t = null;
        try {
            t = entityManager.find(type, id);
        } catch (EntityNotFoundException enfe) {
            // This is okay, just return null
        }

        return t;
    }

    /**
     * {@inheritDoc}
     */
    public boolean persist(Object obj) {
        // Check for and possibly start a tranasction
        Transaction transaction = startTransaction(entityManager);
        boolean uniqueConstraint = false;
        boolean exception = false;
        try {
            if (entityManager.contains(obj) ||
                    (obj instanceof Identifiable && ((Identifiable) obj).getId() == null)) {
                entityManager.persist(obj);
            } else {
                entityManager.merge(obj);
            }
        } catch (EntityExistsException e) {
            uniqueConstraint = true;
            exception = true;
        } catch (PersistenceException pe) {
            exception = true;
            throw pe;
        } finally {
            // Always rollback if the transaction is local
            if (!exception && !transaction.getRollbackOnly()) {
                transaction.commit();
            } else {
                transaction.rollback();
            }
        }

        return !uniqueConstraint;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Identifiable> boolean delete(Class<T> type, int id) {
        T t = findById(type, id);
        if (t != null) {
            // Check for and possibly start a transaction
            Transaction transaction = startTransaction(entityManager);

            // Remove it for normal entities and soft delete for others
            if (t instanceof SoftDeletable) {
                ((SoftDeletable) t).setDeleted(true);
                entityManager.persist(t);
            } else {
                entityManager.remove(t);
            }

            // No need for a try-catch block because a call to commit that fails will rollback the
            // transaction automatically for us.
            transaction.commit();

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Object obj) {
        // Check for and possibly start a transaction
        Transaction transaction = startTransaction(entityManager);

        // Remove it for normal entities and soft delete for others
        if (obj instanceof SoftDeletable) {
            ((SoftDeletable) obj).setDeleted(true);
            entityManager.persist(obj);
        } else {
            entityManager.remove(obj);
        }

        // No need for a try-catch block because a call to commit that fails will rollback the
        // transaction automatically for us.
        transaction.commit();
    }

    /**
     * {@inheritDoc}
     */
    public int execute(String statement, Object... params) {
        // Check for and possibly start a transaction
        Transaction transaction = startTransaction(entityManager);

        // Create the update and execute it
        Query query = entityManager.createQuery(statement);
        addParams(query, params);
        int results = query.executeUpdate();

        // No need for a try-catch block because a call to commit that fails will rollback the
        // transaction automatically for us.
        transaction.commit();

        return results;
    }

    /**
     * Ensures that the entity class contains the correct annotation to be persisted.
     *
     * @param   type The entity type to check.
     * @throws  PersistenceException If the class is missing the Entity annotation.
     */
    protected void verify(Class<?> type) {
        if (!verifyEntityClasses) {
            return;
        }

        Annotation entityAntn = type.getAnnotation(Entity.class);
        if (entityAntn == null) {
            throw new PersistenceException("Invalid Identifiable class [" + type.getName() + "]. The " +
                "class doesn't contain the @Entity annotation at the class declaration.");
        }
    }

    /**
     * Strips the package name to create a JPA persistable name that can be used in EJB-QL. For
     * example:
     *
     * <table>
     * <tr><th>Class</th><th>Return</th></tr>
     * <tr><td>org.jcatapult.project.domain.Photo</td><td>Photo</td>
     * <tr><td>org.jcatapult.project.Foo</td><td>Foo</td>
     * <tr><td>NoPackageClass</td><td>NoPackageClass</td>
     * </table>
     *
     * @param   type The class to strip the package name from the fully qualified class name.
     * @return  The name.
     */
    protected String stripPackage(Class<?> type) {
        String className = type.getName();
        int index = className.lastIndexOf(".");
        if (index > 0) {
            return className.substring(index + 1);
        }

        return className;
    }

    /**
     * Sets the given parameters to the query. These are positional parameters not named parameters.
     *
     * @param   q The Query to set the parameters into.
     * @param   params The parameters to set as positional parameters into the query.
     */
    protected void addParams(Query q, Object... params) {
        if (params.length > 0) {
            int count = 1;
            for (Object param : params) {
                q.setParameter(count++, param);
            }
        }
    }
}