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

import org.aopalliance.intercept.MethodInvocation;
import org.jcatapult.persistence.test.JPABaseTest;
import org.jcatapult.persistence.txn.annotation.Transactional;
import org.junit.Test;

import com.google.inject.AbstractModule;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * <p> This class tests the transaction method interceptor. </p>
 *
 * @author Brian Pontarelli
 */
public class TransactionMethodInterceptorTest extends JPABaseTest {
  private final TxnMgr txnMgr = new TxnMgr();

  public TransactionMethodInterceptorTest() {
    addModules(new AbstractModule() {
      @Override
      protected void configure() {
        bind(TransactionContextManager.class).toInstance(txnMgr);
      }
    });
  }

  @Transactional
  public void annotatedMethod() {
  }

  @Test
  public void topLevelCommit() throws Throwable {
    MethodInvocation invocation = createStrictMock(MethodInvocation.class);
    expect(invocation.getMethod()).andReturn(this.getClass().getMethod("annotatedMethod"));
    expect(invocation.proceed()).andReturn(null);
    replay(invocation);

    txnMgr.context = createStrictMock(TransactionContext.class);
    expect(txnMgr.context.isStarted()).andReturn(false);
    txnMgr.context.start();
    expect(txnMgr.context.isRollbackOnly()).andReturn(false);
    txnMgr.context.commit();
    replay(txnMgr.context);

    TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();
    interceptor.invoke(invocation);
    assertNull(txnMgr.context);

    verify(invocation);
  }

  @Test
  public void topLevelRollback() throws Throwable {
    RuntimeException re = new RuntimeException();
    MethodInvocation invocation = createStrictMock(MethodInvocation.class);
    expect(invocation.getMethod()).andReturn(this.getClass().getMethod("annotatedMethod"));
    expect(invocation.proceed()).andThrow(re);
    replay(invocation);

    txnMgr.context = createStrictMock(TransactionContext.class);
    expect(txnMgr.context.isStarted()).andReturn(false);
    txnMgr.context.start();
    expect(txnMgr.context.isRollbackOnly()).andReturn(false);
    txnMgr.context.rollback();
    replay(txnMgr.context);

    TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();
    try {
      interceptor.invoke(invocation);
      fail("Should have re-thrown the exception");
    } catch (Throwable throwable) {
      // Expected
      assertSame(throwable, re);
    }
    assertNull(txnMgr.context);

    verify(invocation);
  }

  @Test
  public void topLevelRollbackOnly() throws Throwable {
    MethodInvocation invocation = createStrictMock(MethodInvocation.class);
    expect(invocation.getMethod()).andReturn(this.getClass().getMethod("annotatedMethod"));
    expect(invocation.proceed()).andReturn(null);
    replay(invocation);

    txnMgr.context = createStrictMock(TransactionContext.class);
    expect(txnMgr.context.isStarted()).andReturn(false);
    txnMgr.context.start();
    expect(txnMgr.context.isRollbackOnly()).andReturn(true);
    txnMgr.context.rollback();
    replay(txnMgr.context);

    TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();
    interceptor.invoke(invocation);
    assertNull(txnMgr.context);

    verify(invocation);
  }

  @Test
  public void nested() throws Throwable {
    MethodInvocation invocation = createStrictMock(MethodInvocation.class);
    expect(invocation.getMethod()).andReturn(this.getClass().getMethod("annotatedMethod"));
    expect(invocation.proceed()).andReturn(null);
    replay(invocation);

    txnMgr.context = createStrictMock(TransactionContext.class);
    expect(txnMgr.context.isStarted()).andReturn(true);
    replay(txnMgr.context);

    TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();
    interceptor.invoke(invocation);
    assertNotNull(txnMgr.context);

    verify(invocation);
  }

  @Test
  public void nestedFailure() throws Throwable {
    RuntimeException re = new RuntimeException();
    MethodInvocation invocation = createStrictMock(MethodInvocation.class);
    expect(invocation.getMethod()).andReturn(this.getClass().getMethod("annotatedMethod"));
    expect(invocation.proceed()).andThrow(re);
    replay(invocation);

    txnMgr.context = createStrictMock(TransactionContext.class);
    expect(txnMgr.context.isStarted()).andReturn(true);
    txnMgr.context.setRollbackOnly();
    replay(txnMgr.context);

    TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();
    try {
      interceptor.invoke(invocation);
      fail("Should have re-thrown the exception");
    } catch (Throwable throwable) {
      // Expected
      assertSame(throwable, re);
    }
    assertNotNull(txnMgr.context);

    verify(invocation);
  }

  public static class TxnMgr implements TransactionContextManager {
    public TransactionContext context;

    @Override
    public TransactionContext start() throws Exception {
      return context;
    }

    @Override
    public TransactionContext getCurrent() {
      return context;
    }

    @Override
    public void tearDownTransactionContext() {
      context = null;
    }
  }
}
