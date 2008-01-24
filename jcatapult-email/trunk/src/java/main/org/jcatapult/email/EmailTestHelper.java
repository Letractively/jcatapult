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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

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

        final EmailTransportService ets = new EmailTransportService() {
            public Future<Email> sendEmail(Email email) {
                emailResult.set(email);
                return new FutureTask<Email>(new Callable<Email>() {
                    public Email call() throws Exception {
                        return emailResult.get();
                    }
                });
            }
        };

        test.addModules(new AbstractModule() {
            protected void configure() {
                bind(EmailTransportService.class).toInstance(ets);
            }
        });
    }
}