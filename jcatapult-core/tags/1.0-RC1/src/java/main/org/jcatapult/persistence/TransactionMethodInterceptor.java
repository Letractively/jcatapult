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
package org.jcatapult.persistence;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jcatapult.guice.GuiceContainer;
import org.jcatapult.persistence.annotation.Transactional;

/**
 * <p>
 * This is the AOP method interceptor that JCatapult uses with Guice's
 * AOP engine in order to provide transaction support around methods.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class TransactionMethodInterceptor implements MethodInterceptor {
    /**
     * Intercepts method invocations that have been tagged with the
     * {@link org.jcatapult.persistence.annotation.Transactional} annotation. This delegates for the
     * most part to the {@link TransactionManager} interface that is injected for the transaction
     * handling. The only thing this class does is to place a try-catch-finally block around the
     * method invocation and store the results of the method invocation to pass to the transaction
     * manager.
     *
     * @param   methodInvocation The method invocation.
     * @return  The result of the method invocation.
     * @throws  Throwable Any exception that the method throws.
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Transactional annotation = methodInvocation.getMethod().getAnnotation(Transactional.class);
        Class<? extends TransactionResultProcessor> processorClass = annotation.processor();
        TransactionResultProcessor processor = processorClass.newInstance();

        TransactionManager txnManager = GuiceContainer.getInjector().getInstance(TransactionManager.class);
        TransactionState txn = txnManager.startTransaction();
        Object result = null;
        Throwable t = null;
        try {
            result = methodInvocation.proceed();
        } catch (Throwable throwable) {
            t = throwable;
            throw throwable;
        } finally {
            txnManager.endTransaction(result, t, txn, processor);
        }

        return result;
    }
}