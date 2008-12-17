/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.crud.action;

import java.util.List;

import org.jcatapult.mvc.action.annotation.ActionPrepareMethod;
import org.jcatapult.mvc.scope.annotation.ActionSession;

import com.google.inject.Inject;

import org.jcatapult.crud.service.SearchCriteria;
import org.jcatapult.crud.service.SearchService;

/**
 * <p>
 * This is a generic base action for performing searches.
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
        if (searchCriteria == null) {
            searchCriteria = getDefaultCriteria();
        }
    }

    /**
     * Performs the search.
     *
     * @return  Always success.
     */
    public String execute() {
        if (clear) {
            searchCriteria = getDefaultCriteria();
        }

        results = searchService.find(searchCriteria);
        totalCount = searchService.totalCount(searchCriteria);
        return "success";
    }

    /**
     * @return  A new default search criteria. Must be implemented by sub-classes.
     */
    protected abstract U getDefaultCriteria();
}