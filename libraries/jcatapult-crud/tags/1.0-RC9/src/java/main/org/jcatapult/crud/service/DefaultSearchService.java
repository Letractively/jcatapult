/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
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