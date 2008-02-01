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
package org.jcatapult.email;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jcatapult.email.domain.Email;
import org.jcatapult.email.service.EmailTransportService;
import org.jcatapult.test.JCatapultBaseTest;

import com.google.inject.AbstractModule;

/**
 * <p>
 * This class provides tests with the ability to setup email handling.
 * This class relies on the usage of the JCatapult base tests in order
 * to work properly, but the concepts are simple that you could write
 * it yourself if you aren't using those base test classes.
 * </p>
 *
 * <p>
 * <b>NOTE:</b> This class is thread safe and can be used in parallel test
 * cases.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class EmailTestHelper {
    private static ThreadLocal<Email> emailResult = new ThreadLocal<Email>();
    private static ThreadLocal<Future<Email>> future = new ThreadLocal<Future<Email>>();

    /**
     * Returns the email result for the last test run. This is a thread safe retrieval.
     *
     * @return  The email result or null if there isn't one.
     */
    public static Email getEmailResult() {
        return emailResult.get();
    }

    /**
     * Mocks out an {@link EmailTransportService} so that an SMTP server is not required to run the
     * tests. This will mock return the mail and also provides the email via the {@link #getEmailResult()}
     * method on this class. This class is thread safe and can be used in parallel test cases.
     *
     * @param   test The test to setup for email handling.
     */
    public static void setup(JCatapultBaseTest test) {
        emailResult.remove();
        future.set(new MockFuture(false));

        final EmailTransportService ets = new EmailTransportService() {
            public Future<Email> sendEmail(Email email) {
                return future.get();
            }
        };

        test.addModules(new AbstractModule() {
            protected void configure() {
                bind(EmailTransportService.class).toInstance(ets);
            }
        });
    }

    /**
     * This method informs the mock that it should simulate a timeout when sending an email.
     */
    public static void timeout() {
        future.set(new MockFuture(true));
    }

    /**
     * This method informs the mock that it should reset itself and NOT simulate a timeout when sending
     * an email.
     */
    public static void reset() {
        future.set(new MockFuture(false));
    }

    public static class MockFuture implements Future<Email> {
        private final boolean timeout;
        private boolean cancelled;

        public MockFuture(boolean timeout) {
            this.timeout = timeout;
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            return true;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public boolean isDone() {
            return !timeout;
        }

        public Email get() throws InterruptedException, ExecutionException {
            if (timeout) {
                throw new AssertionError("Timeout set and get() was called. You should be calling " +
                    "get(long, TimeUnit) from you code.");
            }
            return emailResult.get();
        }

        public Email get(long duration, TimeUnit unit) throws TimeoutException {
            if (timeout) {
                throw new TimeoutException("Timeout");
            }
            return emailResult.get();
        }
    }
}