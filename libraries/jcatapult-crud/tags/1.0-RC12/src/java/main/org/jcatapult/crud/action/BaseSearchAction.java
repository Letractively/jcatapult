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
package org.jcatapult.crud.action;

import java.util.List;

import org.jcatapult.crud.service.SearchCriteria;
import org.jcatapult.crud.service.SearchService;
import org.jcatapult.mvc.action.annotation.ActionPrepareMethod;
import org.jcatapult.mvc.scope.annotation.ActionSession;

import com.google.inject.Inject;

/**
 * <p>
 * This is a generic base action for performing searches. In order to implement
 * a CRUD search and index page, simply extend this class and provide an
 * implementation of the {@link #getDefaultCriteria()} method. This will
 * require you to implement the {@link SearchCriteria} interface or to use
 * the {@link org.jcatapult.crud.service.AbstractSearchCriteria} or the
 * {@link org.jcatapult.crud.service.SimpleSearchCriteria}.
 * </p>
 *
 * <p>
 * An implementation might look like this:
 * </p>
 *
 * <pre>
 * @@Action
 * public class Index extends BaseSearchAction&lt;User, UserSearchCriteria&lt;User>> {
 *     protected UserSearchCriteria&lt;User> getDefaultCriteria() {
 *         return new UserSearchCriteria&lt;User>(User.class);
 *     }
 * }
 * </pre>
 *
 * <p>
 * The bulk of the search work happens in the FTL file and the <code>UserSearchCriteria</code>
 * class.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public abstract class BaseSearchAction<T, U extends SearchCriteria<T>> {
    protected SearchService searchService;

    /**
     * The results of the search or an empty list.
     */
    @ActionSession
    public List<T> results;

    /**
     * The search criteria, which is either the default or the current criteria that is stored in
     * the session.
     */
    @ActionSession
    public U searchCriteria;

    /**
     * The total number of BillingTransactions.
     */
    public long totalCount;

    /**
     * The current clear flag state.
     */
    public boolean clear = false;

    /**
     * Determines if the entity can be added via the CRUD interface.
     */
    public boolean addable = true;

    /**
     * Determines if the entity can be edited via the CRUD interface.
     */
    public boolean editable = true;

    /**
     * Determines if the entity details can be viewed via the CRUD interface.
     */
    public boolean detailable = true;

    /**
     * Determines if the entity can be deleted via the CRUD interface.
     */
    public boolean deletable = true;

    /**
     * Sets in the search service.
     *
     * @param   searchService The search service to use.
     */
    @Inject
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * This method prepares the SearchCriteria instance if it is null by calling the
     * {@link #getDefaultCriteria()} method.
     */
    @ActionPrepareMethod
    public void prepare() {
        if (searchCriteria == null || clear) {
            searchCriteria = getDefaultCriteria();
        }
    }

    /**
     * Performs the search.
     *
     * @return  Always success.
     */
    public String execute() {
        results = searchService.find(searchCriteria);
        totalCount = searchService.totalCount(searchCriteria);
        return "success";
    }

    /**
     * @return  A new default search criteria. Must be implemented by sub-classes.
     */
    protected abstract U getDefaultCriteria();
}