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
package org.jcatapult.module.cms.service;

/**
 * <p>
 * This exception is thrown when looking for visible content that is
 * not visible but does exist.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class InvisibleException extends Exception {
    public InvisibleException() {
        super();
    }

    public InvisibleException(String message) {
        super(message);
    }

    public InvisibleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvisibleException(Throwable cause) {
        super(cause);
    }
}