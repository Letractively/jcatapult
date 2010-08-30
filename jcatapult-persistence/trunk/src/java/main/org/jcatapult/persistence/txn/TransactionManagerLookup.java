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

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the method by which transaction managers are looked up for a specific
 * request.
 * </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(DefaultTransactionManagerLookup.class)
public interface TransactionManagerLookup {
    /**
     * @return  The list of managers that should participate in the current transaction.
     */
    List<TransactionManager> lookupManagers();
}
