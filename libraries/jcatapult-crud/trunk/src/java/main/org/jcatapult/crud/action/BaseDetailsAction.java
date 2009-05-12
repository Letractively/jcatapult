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
 * This is a generic base action for showing details of single row in the search
 * results. This class is helpful because it handles next and prev buttons on
 * the details page.
 * </p>
 *
 * <p>
 * An implementation might look like this:
 * </p>
 *
 * <pre>
 * @@Action
 * public class Details extends BaseDetailsAction&lt;User> {
 *   // Needed to calculate next and prev buttons
 *   @@ActionSession(action = Index.class)
 *   public List&lt;T> results;
 *
 *   // Needed to calculate next and prev buttons
 *   @@ActionSession
 *   public SearchCriteria&lt;T> searchCriteria;
 *
 *   protected Class&lt;User> getType() {
 *     return User.class;
 *   }
 *
 *   protected List&lt;User> getResults() {
 *     return results;
 *   }
 *
 *   protected SearchCriteria&lt;User> getSearchCriteria() {
 *     return searchCriteria;
 *   }
 * }
 * </pre>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public abstract class BaseDetailsAction<T extends Identifiable> {
    protected SearchService searchService;

    /**
     * The ID of the entity to display the details for. (Input)
     */
    public int id;

    /**
     * The index within the current search results page for this entity. (Output)
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
     * The entity.  (Output)
     */
    public T result;
    
    public long totalCount;
    
    public int numberOfPages;

    /**
     * Sub-classes must implement this to provide the result type.
     *
     * @return  The result type.
     */
    protected abstract Class<T> getType();

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
     * Sets in the search service.
     *
     * @param   searchService The search service to use.
     */
    @Inject
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostParameterMethod
    public void checkResults() {
        SearchCriteria<T> criteria = getSearchCriteria();
        List<T> results = getResults();
        if (results != null && criteria != null) {
            totalCount = searchService.totalCount(criteria);
            numberOfPages = (int) totalCount / criteria.getNumberPerPage();
            if (numberOfPages <= 0) {
                numberOfPages = 1;
            }

            if (index == results.size()) {
                boolean hasNextPage = criteria.getPage() != numberOfPages;
                if (hasNextPage) {
                    results.clear();
                    results.addAll(searchService.find(criteria));
                    index = 0;
                }
            }
        }
    }

    /**
     * Performs the search.
     *
     * @return  Always success.
     */
    public String execute() {
        result = searchService.findById(getType(), id);
        if (result == null) {
            return "missing";
        }

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

        return "success";
    }
}