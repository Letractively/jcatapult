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