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
package org.jcatapult.persistence.txn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jcatapult.persistence.txn.DefaultTransactionResultProcessor;
import org.jcatapult.persistence.txn.TransactionResultProcessor;

/**
 * <p>
 * This annotation can be placed on methods and used by Guice to add
 * transactional handling to any method. This annotation causes Guice
 * to create proxies and inject a method interceptor that handles the
 * transactions. The actual handling of the transaction is done via
 * the {@link} interface. This allows custom handling to be easily
 * plugged into any application.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Transactional {
    Class<? extends TransactionResultProcessor> processor() default DefaultTransactionResultProcessor.class;
}