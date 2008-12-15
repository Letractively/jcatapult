/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.crud.service;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines a generic search contract for entity objects.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(DefaultSearchService.class)
public interface SearchService {
    /**
     * Performs the search.
     *
     * @param   search The search criteria.
     * @return  The results (if any).
     */
    <T, U extends SearchCriteria<T>> List<T> find(U search);

    /**
     * Calculates the total count based on the given search criteria.
     *
     * @param   search The search criteria.
     * @return  The total count.
     */
    <T> long totalCount(SearchCriteria<T> search);
}