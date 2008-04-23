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

import javax.persistence.EntityTransaction;

/**
 * <p>
 * This interface is passed between the methods of the {@link TransactionManager}
 * by the JCatapult transaction AOP support. You can implement this however
 * you need and your TransactionManager is guaranteed to always get back what
 * it instantiated. So, feel free to add more properties and cast away.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface TransactionState {
    /**
     * @return  The EntityTransaction that is created by the {@link TransactionManager}.
     */
    EntityTransaction transaction();

    /**
     * @return  True if the transaction is embedded, false otherwise.
     */
    boolean embedded();
}