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
package org.jcatapult.security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * <p>
 * This class is used to proxy the User object instances so that they can be
 * re-attached to the JPA entity manager for the current execute thread.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class UserCGLIBCallback implements MethodInterceptor {
    private Object user;
    private EntityManager entityManager;
    private boolean detached = true;

    public UserCGLIBCallback(Object user, EntityManager entityManager) {
        this.user = user;
        this.entityManager = entityManager;
    }

    /**
     * Re-attaches the user object if necessary.
     *
     * @param   target The User object.
     * @param   method The method invoked.
     * @param   args The arguments to the method.
     * @param   methodProxy The AOP method proxy.
     * @return  The call to {@link MethodProxy#invokeSuper(Object, Object[])}.
     * @throws  Throwable If the method invoked throws anything.
     */
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy)
    throws Throwable {
        if (detached) {
            user = entityManager.merge(user);
            SecurityContext.update(user);
        }

        try {
            return method.invoke(user, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw e;
        }
    }
}