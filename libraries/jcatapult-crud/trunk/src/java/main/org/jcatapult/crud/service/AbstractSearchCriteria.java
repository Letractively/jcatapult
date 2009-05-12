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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.java.util.Pair;
import org.jcatapult.persistence.domain.SoftDeletable;

/**
 * <p>
 * This is an abstract search criteria that sets the defaults as:
 * </p>
 *
 * <table>
 * <tr><th>Property</th><th>Default</th></tr>
 * <tr><td>sortProperty</td><td>null</td></tr>
 * <tr><td>numberPerPage</td><td>20</td></tr>
 * <tr><td>page</td><td>1</td></tr>
 * <tr><td>showAll</td><td>false</td></tr>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractSearchCriteria<T> implements SearchCriteria<T>, Serializable {
    protected String sortProperty;
    protected int numberPerPage = 20;
    protected int page = 1;
    protected boolean showAll = false;

    protected AbstractSearchCriteria() {
    }

    protected AbstractSearchCriteria(String sortProperty, int numberPerPage, int page, boolean showAll) {
        this.sortProperty = sortProperty;
        this.numberPerPage = numberPerPage;
        this.page = page;
        this.showAll = showAll;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public int getNumberPerPage() {
        return numberPerPage;
    }

    public void setNumberPerPage(int numberPerPage) {
        this.numberPerPage = numberPerPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("searchCriteria.sortProperty", sortProperty);
        map.put("searchCriteria.numberPerPage", numberPerPage);
        map.put("searchCriteria.page", page);
        map.put("searchCriteria.showAll", showAll);
        return map;
    }

    public Pair<String, Map<String, Object>> toJPAQuery() {
        QueryBuilder builder = new QueryBuilder("e from " + getResultType().getSimpleName() + " e", getSortProperty());
        buildJPAQuery(builder);
        return builder.toJPAQuery();
    }

    public Pair<String, Map<String, Object>> toJPACountQuery() {
        QueryBuilder builder = new QueryBuilder("count(e) from " + getResultType().getSimpleName() + " e", null);
        buildJPACountQuery(builder);
        return builder.toJPAQuery();
    }

    /**
     * Implemented by sub-classes to construct a JPA query using the given QueryBuilder.
     *
     * @param   builder The builder.
     */
    protected abstract void buildJPAQuery(QueryBuilder builder);

    /**
     * Implemented by sub-classes to construct a JPA count query using the given QueryBuilder.
     *
     * @param   builder The builder.
     */
    protected abstract void buildJPACountQuery(QueryBuilder builder);

    /**
     * A simple builder class for building query strings.
     */
    public class QueryBuilder {
        private final StringBuilder where = new StringBuilder();
        private final Map<String, Object> params = new HashMap<String, Object>();
        private String select;
        private String orderBy;

        public QueryBuilder(String select, String orderBy) {
            this.select = select;
            if (orderBy != null) {
                this.orderBy = "e." + orderBy;
            }
        }

        /**
         * <p>
         * Sets the select of the query, such as <strong>e from User e</strong>. This could also be
         * a count query. This should not include the <strong>select</strong> keyword. An example
         * usage is:
         * </p>
         *
         * <pre>
         * select("e from User e")
         * </pre>
         *
         * <p>
         * You must always alias the Entity to <strong>e</strong>.
         * </p>
         *
         * @param   select The select query.
         * @return  This builder.
         */
        public QueryBuilder select(String select) {
            this.select = select;
            return this;
        }

        /**
         * <p>
         * Adds a where clause to the query. The parameters in the where clause must be named parameters
         * like this: <strong>age &lt; :age</strong>. You can set named parameters into this builder
         * using the <strong>withParameter</strong> methods. This should not include the <strong>where</strong>
         * keyword. An example usage is like this:
         * </p>
         *
         * <pre>
         * where("e.age &lt; :age")
         * </pre>
         *
         * <p>
         * If there is already a where clause in the query, this <strong>ands</strong> the given where
         * clause to the already existing clause.
         * </p>
         *
         * <p>
         * You must always alias the Entity to <strong>e</strong>.
         * </p>
         *
         * @param   where The clause.
         * @return  This builder.
         */
        public QueryBuilder where(String where) {
            if (this.where.length() > 0) {
                this.where.append(" and ");
            }

            this.where.append(where);
            return this;
        }

        /**
         * <p>
         * Adds a where clause to the query. The parameters in the where clause must be named parameters
         * like this: <strong>age &lt; :age</strong>. You can set named parameters into this builder
         * using the <strong>withParameter</strong> methods. This should not include the <strong>where</strong>
         * keyword. An example usage is like this:
         * </p>
         *
         * <pre>
         * where("e.age &lt; :age")
         * </pre>
         *
         * <p>
         * If there is already a where clause in the query, this <strong>ands</strong> the given where
         * clause to the already existing clause.
         * </p>
         *
         * <p>
         * You must always alias the Entity to <strong>e</strong>.
         * </p>
         *
         * @param   where The clause.
         * @return  This builder.
         */
        public QueryBuilder andWhere(String where) {
            if (this.where.length() > 0) {
                this.where.append(" and ");
            }

            this.where.append(where);
            return this;
        }

        /**
         * <p>
         * Adds a where clause to the query. The parameters in the where clause must be named parameters
         * like this: <strong>age &lt; :age</strong>. You can set named parameters into this builder
         * using the <strong>withParameter</strong> methods. This should not include the <strong>where</strong>
         * keyword. An example usage is like this:
         * </p>
         *
         * <pre>
         * andWhere("e.age &lt; :age")
         * </pre>
         *
         * <p>
         * If there is already a where clause in the query, this <strong>ors</strong> the given where
         * clause to the already existing clause.
         * </p>
         *
         * <p>
         * You must always alias the Entity to <strong>e</strong>.
         * </p>
         *
         * @param   where The clause.
         * @return  This builder.
         */
        public QueryBuilder orWhere(String where) {
            if (this.where.length() > 0) {
                this.where.append(" or ");
            }

            this.where.append(where);
            return this;
        }

        /**
         * Adds a named parameter to the builder.
         *
         * @param   name The name of the parameter.
         * @param   param The parameter.
         * @return  This builder.
         */
        public QueryBuilder withParameter(String name, Object param) {
            this.params.put(name, param);
            return this;
        }


        /**
         * <p>
         * Adds a order by clause to the query. This should not include the <strong>order by</strong>
         * keyword, but MUST include the alias for the objects (i.e. 'e'). An example usage is like
         * this:
         * </p>
         *
         * <pre>
         * orderBy("e.age desc")
         * </pre>
         *
         * @param   orderBy The clause.
         * @return  This builder.
         */
        public QueryBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        protected Pair<String, Map<String, Object>> toJPAQuery() {
            StringBuilder build = new StringBuilder();
            build.append("select ").append(select);
            if (where.length() > 0) {
                build.append(" where ").append(where);
                if (SoftDeletable.class.isAssignableFrom(getResultType())) {
                    build.append(" and e.deleted = false");
                }
            } else {
                if (SoftDeletable.class.isAssignableFrom(getResultType())) {
                    build.append(" where e.deleted = false");
                }
            }

            if (orderBy != null) {
                build.append(" order by ").append(orderBy);
            }

            return new Pair<String, Map<String, Object>>(build.toString(), params);
        }
    }
}