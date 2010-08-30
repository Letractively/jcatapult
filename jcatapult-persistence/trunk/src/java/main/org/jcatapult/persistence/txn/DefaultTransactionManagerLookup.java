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

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.jcatapult.persistence.service.jdbc.ConnectionContext;
import org.jcatapult.persistence.service.jpa.EntityManagerContext;
import org.jcatapult.persistence.txn.jdbc.JDBCTransactionManager;
import org.jcatapult.persistence.txn.jpa.JPATransactionManager;

/**
 * <p>
 * This class uses the the {@link EntityManagerContext} and the {@link ConnectionContext} to
 * determine if the request is using JDBC and/or JPA.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultTransactionManagerLookup implements TransactionManagerLookup {
    private final Injector injector;

    @Inject
    public DefaultTransactionManagerLookup(Injector injector) {
        this.injector = injector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionManager> lookupManagers() {
        List<TransactionManager> managers = new ArrayList<TransactionManager>();

        // Check on JDBC
        if (ConnectionContext.get() != null) {
            managers.add(injector.getInstance(JDBCTransactionManager.class));
        }

        // Check on JPA
        if (EntityManagerContext.get() != null) {
            managers.add(injector.getInstance(JPATransactionManager.class));
        }

        return managers;
    }
}
