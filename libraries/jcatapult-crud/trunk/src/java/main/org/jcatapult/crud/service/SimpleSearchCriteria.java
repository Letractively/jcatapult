/*
 * Copyright (c) 2001-2009, Inversoft, All Rights Reserved
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
