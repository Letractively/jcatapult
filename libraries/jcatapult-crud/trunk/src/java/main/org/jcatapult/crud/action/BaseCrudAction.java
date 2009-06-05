/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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
package org.jcatapult.crud.action;

import java.util.List;

import org.jcatapult.crud.service.SearchCriteria;
import org.jcatapult.crud.service.SearchService;
import org.jcatapult.mvc.parameter.annotation.PostParameterMethod;
import org.jcatapult.persistence.domain.Identifiable;

import com.google.inject.Inject;

/**
 * <p>
 * This class is a base class that can provide next and previous result links
 * for any CRUD action. These links are determine using the current results,
 * the search criteria, the current index within the current results, and the
 * total count.
 * </p>
 *
 * @author Brian Pontarelli
 */
public abstract class BaseCrudAction<T extends Identifiable> {
    protected SearchService searchService;

    /**
     * The index within the current search results page for this entity. (Input)
     */
    public int index;

    /**
     * The ID of the next entity in the search results or null if there isn't one. (Output)
     */
    public Integer nextId;

    /**
     * The ID of the previous entity in the search results or null if there isn't one.  (Output)
     */
    public Integer prevId;

    /**
     * The total number of results in the search. (Output)
     */
    public long totalCount;

    /**
     * The total number of pages in the search results. Based on the search criteria, total results,
     * and number per page. (Output)
     */
    public int numberOfPages;

    @Inject
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Sub-classes must implement this to provide the results list from the action session.
     *
     * @return  The results.
     */
    protected abstract List<T> getResults();

    /**
     * Sub-classes must implement this to provide the search criteria from the action session.
     *
     * @return  The search criteria.
     */
    protected abstract SearchCriteria<T> getSearchCriteria();

    /**
     * This method determines if the index has pushed the current view beyond the current set of
     * results. This means that the next or previous set of results needs to be fetched.
     */
    @PostParameterMethod
    public void checkResults() {
        SearchCriteria<T> criteria = getSearchCriteria();
        List<T> results = getResults();
        if (results != null && criteria != null) {
            totalCount = searchService.totalCount(criteria);
            numberOfPages = (int) totalCount / criteria.getNumberPerPage();
            int remainder = (int) totalCount % criteria.getNumberPerPage();
            if (remainder > 0) {
                numberOfPages++;
            }

            if (index == results.size()) {
                boolean hasNextPage = criteria.getPage() != numberOfPages;
                if (hasNextPage) {
                    criteria.setPage(criteria.getPage() + 1);
                    results.clear();
                    results.addAll(searchService.find(criteria));
                    index = 0;
                }
            }

            if (index == -1) {
                boolean hasPrevPage = criteria.getPage() != 1;
                if (hasPrevPage) {
                    criteria.setPage(criteria.getPage() - 1);
                    results.clear();
                    results.addAll(searchService.find(criteria));
                    index = results.size() - 1;
                }
            }
        }
    }

    /**
     * Sets up the next and previous IDs that can be used in links or on forms. These are based on
     * the current search results, the search criteria, index, number of pages, etc. Sub-classes
     * will need to invoke this method in their execute method(s).
     */
    protected void setupNextPrevIds() {
        List<T> results = getResults();
        SearchCriteria<T> criteria = getSearchCriteria();
        if (results != null && criteria != null) {
            if (index != results.size() - 1) {
                nextId = results.get(index + 1).getId();
            } else {
                boolean hasNextPage = criteria.getPage() != numberOfPages;
                if (hasNextPage) {
                    int page = criteria.getPage();
                    criteria.setPage(page + 1);
                    List<T> nextResults = searchService.find(criteria);
                    criteria.setPage(page);
                    nextId = nextResults.get(0).getId();
                }
            }

            if (index != 0) {
                prevId = results.get(index - 1).getId();
            } else {
                boolean hasPrevPage = criteria.getPage() != 1;
                if (hasPrevPage) {
                    int page = criteria.getPage();
                    criteria.setPage(page - 1);
                    List<T> prevResults = searchService.find(criteria);
                    criteria.setPage(page);
                    prevId = prevResults.get(prevResults.size() - 1).getId();
                }
            }
        }
    }
}
