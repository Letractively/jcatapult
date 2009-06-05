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

/**
 * <p>
 * This class is a simple search criteria for Objects that do not require any
 * special handling or that want to implement a search.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class SimpleSearchCriteria<T> extends AbstractSearchCriteria<T> {
    private final Class<T> type;

    /**
     * Constructs the SimpleSearchCriteria for the given type.
     *
     * @param   type The type.
     */
    public SimpleSearchCriteria(Class<T> type) {
        this.type = type;
    }

    /**
     * Constructs the SimpleSearchCriteria for the given type and using the given initial sort property,
     * number of results per page, page number and show all flag.
     *
     * @param   type The type.
     * @param   sortProperty The initial sort property.
     * @param   numberPerPage The number per page.
     * @param   page The initial page number.
     * @param   showAll The show all flag.
     */
    public SimpleSearchCriteria(Class<T> type, String sortProperty, int numberPerPage, int page, boolean showAll) {
        super(sortProperty, numberPerPage, page, showAll);
        this.type = type;
    }

    /**
     * This method doesn't do anything to the query builder.
     *
     * @param   builder The builder.
     */
    protected void buildJPAQuery(QueryBuilder builder) {
    }

    /**
     * This method doesn't do anything to the query builder.
     *
     * @param   builder The builder.
     */
    protected void buildJPACountQuery(QueryBuilder builder) {
    }

    /**
     * @return  The type.
     */
    public Class<T> getResultType() {
        return type;
    }
}
