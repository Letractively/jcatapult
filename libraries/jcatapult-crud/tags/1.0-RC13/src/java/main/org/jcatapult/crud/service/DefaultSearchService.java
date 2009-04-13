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
package org.jcatapult.crud.service;

import java.util.List;
import java.util.Map;

import org.jcatapult.persistence.service.PersistenceService;

import com.google.inject.Inject;
import net.java.util.Pair;

/**
 * <p>
 * This is the default implementation of the search service.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DefaultSearchService implements SearchService {
    private final PersistenceService persistenceService;

    @Inject
    public DefaultSearchService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * {@inheritDoc}
     */
    public <T, U extends SearchCriteria<T>> List<T> find(U search) {
        Pair<String, Map<String, Object>> pair = search.toJPAQuery();
        if (search.isShowAll()) {
            return persistenceService.queryAllWithNamedParameters(search.getResultType(), pair.first, pair.second);
        }

        int number = search.getNumberPerPage();
        int start = (search.getPage() - 1) * number;
        return persistenceService.queryWithNamedParameters(search.getResultType(), pair.first, start, number, pair.second);
    }

    /**
     * {@inheritDoc}
     */
    public <T> long totalCount(SearchCriteria<T> search) {
        Pair<String, Map<String, Object>> pair = search.toJPACountQuery();
        return persistenceService.queryCountWithNamedParameters(pair.first, pair.second);
    }
}