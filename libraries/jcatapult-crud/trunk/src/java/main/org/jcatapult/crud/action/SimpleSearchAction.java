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

import org.jcatapult.crud.service.SimpleSearchCriteria;
import org.jcatapult.persistence.domain.Identifiable;

/**
 * <p>
 * This class is an easy way to add an action to your application
 * that hooks into the JCatapult CRUD library. Simply extend this
 * class and pass in the type of your CRUD to the constructor.
 * That's all.
 * </p>
 *
 * <p>
 * An implementation might look like this:
 * </p>
 *
 * <pre>
 * @@Action
 * public class Index extends SimpleSearchAction&lt;Category> {
 *    public Index() {
 *        super(Category.class);
 *    }
 * }
 * </pre>
 * 
 * @author Brian Pontarelli
 */
public abstract class SimpleSearchAction<T extends Identifiable> extends BaseSearchAction<T, SimpleSearchCriteria<T>> {
    private final Class<T> type;

    public SimpleSearchAction(Class<T> type) {
        this.type = type;
    }

    public SimpleSearchAction(Class<T> type, String sortProperty, int numberPerPage, int page, boolean showAll) {
        this.type = type;
        this.searchCriteria = new SimpleSearchCriteria<T>(type, sortProperty, numberPerPage, page, showAll);
    }

    protected SimpleSearchCriteria<T> getDefaultCriteria() {
        return new SimpleSearchCriteria<T>(type);
    }
}
