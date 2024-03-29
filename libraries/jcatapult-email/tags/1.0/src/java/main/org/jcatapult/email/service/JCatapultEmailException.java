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
package org.jcatapult.email.service;

/**
 * Thrown from the {@link org.jcatapult.email.service.JavaMailEmailTransportService} on exception
 *
 * User: jhumphrey
 * Date: Apr 21, 2008
 */
public class JCatapultEmailException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public JCatapultEmailException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public JCatapultEmailException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public JCatapultEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public JCatapultEmailException(Throwable cause) {
        super(cause);
    }
}
