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

import java.util.Map;

import net.java.util.Pair;

/**
 * <p>
 * This interface defines a set of search criteria that is used
 * to perform a search for entity Objects.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface SearchCriteria<T> {

    /**
     * The name of the sort property. This must be a JavaBean property name.
     *
     * @return  The sort property.
     */
    String getSortProperty();

    /**
     * Sets the name of the sort property. This must be a JavaBean property name.
     *
     * @param   sortProperty The sort property.
     */
    void setSortProperty(String sortProperty);

    /**
     * @return  The number of results being displayed per page.
     */
    int getNumberPerPage();

    /**
     * Sets the number of results being displayed per page.
     *
     * @param   numberPerPage The number of results displayed per page.
     */
    void setNumberPerPage(int numberPerPage);

    /**
     * @return  The page being displayed (1 based).
     */
    int getPage();

    /**
     * Sets the page being displayed.
     *
     * @param   page The page to display (1 based).
     */
    void setPage(int page);

    /**
     * @return  Determines if all of the results are to be displayed.
     */
    boolean isShowAll();

    /**
     * Sets whether or not all the results are being displayed.
     *
     * @param   showAll True to display all the results, false to paginate.
     */
    void setShowAll(boolean showAll);

    /**
     * @return  The additional parameters for this search criteria and their values. This includes
     *          the page, numberPerPage, or showAll.
     */
    Map<String, Object> getParameters();

    /**
     * @return  A valid JPA query string that uses the search criteria and the parameters that are
     *          used in the query.
     */
    Pair<String, Map<String, Object>> toJPAQuery();

    /**
     * @return  A valid JPA count query string that uses the search criteria and the parameters that are
     *          used in the query.
     */
    Pair<String, Map<String,Object>> toJPACountQuery();

    /**
     * @return  The result type.
     */
    Class<T> getResultType();
}