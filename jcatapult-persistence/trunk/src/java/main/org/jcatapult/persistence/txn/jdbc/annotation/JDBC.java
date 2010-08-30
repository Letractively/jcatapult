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
package org.jcatapult.persistence.txn.jdbc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.BindingAnnotation;

/**
 * <p>
 * This annotation is a binding annotation for the JDBC transaction manager.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface JDBC {
}
