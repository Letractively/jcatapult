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
package org.jcatapult.persistence.txn;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jcatapult.guice.GuiceContainer;
import org.jcatapult.persistence.txn.annotation.Transactional;

/**
 * <p>
 * This is the AOP method interceptor that provides transaction handling for methods.
 * This transaction handling is generic such that any database connectivity can be used.
 * This includes JDBC, JPA, etc.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class TransactionMethodInterceptor implements MethodInterceptor {
    /**
     * Intercepts method invocations that have been tagged with the {@link Transactional} annotation. This delegates for
     * the most part to the {@link TransactionManager} interface that is injected for the transaction handling. The only
     * thing this class does is to place a try-catch-finally block around the method invocation and store the results of
     * the method invocation to pass to the transaction manager.
     *
     * @param   methodInvocation The method invocation.
     * @return  The result of the method invocation.
     * @throws  Throwable Any exception that the method throws.
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        TransactionResultProcessor processor = processor(methodInvocation);
        TransactionManagerLookup lookup = GuiceContainer.getInjector().getInstance(TransactionManagerLookup.class);

        List<TransactionManager> managers = lookup.lookupManagers();
        List<TransactionState> states = startTransactions(managers);

        Object result = null;
        Throwable t = null;
        try {
            result = methodInvocation.proceed();
        } catch (Throwable throwable) {
            t = throwable;
            throw throwable;
        } finally {
            endTransactions(processor, managers, states, result, t);
        }

        return result;
    }

    /**
     * Called by the invoke method to get the TransactionResultProcessor from the method invocation.
     *
     * @param   methodInvocation The method invocation.
     * @return  The result processor.
     * @throws  Throwable If anything failed.
     */
    protected TransactionResultProcessor processor(MethodInvocation methodInvocation) throws Throwable {
        Transactional annotation = methodInvocation.getMethod().getAnnotation(Transactional.class);
        Class<? extends TransactionResultProcessor> processorClass = annotation.processor();
        return processorClass.newInstance();
    }

    /**
     * Starts all of the transactions for each manager.
     *
     * @param   managers The managers.
     * @return  The transaction states for each manager.
     * @throws  Throwable If starting any of the transactions failed.
     */
    protected List<TransactionState> startTransactions(List<TransactionManager> managers) throws Throwable {
        List<TransactionState> states = new ArrayList<TransactionState>();
        for (TransactionManager manager : managers) {
            states.add(manager.startTransaction());
        }
        return states;
    }

    /**
     * Ends the transactions for each manager.
     *
     * @param   processor The result processor.
     * @param   managers The managers.
     * @param   states The transaction states.
     * @param   result The result.
     * @param   t The thrown exception.
     * @throws  Throwable If ending any of the transactions failed.
     */
    @SuppressWarnings("unchecked")
    private void endTransactions(TransactionResultProcessor processor, List<TransactionManager> managers,
                                 List<TransactionState> states, Object result, Throwable t) throws Throwable {
        for (int i = 0; i < managers.size(); i++) {
            TransactionManager manager = managers.get(i);
            TransactionState state = states.get(i);
            manager.endTransaction(result, t, state, processor);
        }
    }
}
