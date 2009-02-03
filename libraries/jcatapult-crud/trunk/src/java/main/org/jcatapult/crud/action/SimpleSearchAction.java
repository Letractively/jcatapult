/*
 * Copyright (c) 2001-2009, Inversoft, All Rights Reserved
 */
package org.jcatapult.crud.action;

import org.jcatapult.crud.service.SimpleSearchCriteria;

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
 *
 * @author Brian Pontarelli
 */
public class SimpleSearchAction<T> extends BaseSearchAction<T, SimpleSearchCriteria<T>> {
    private final Class<T> type;

    public SimpleSearchAction(Class<T> type) {
        this.type = type;
    }

    protected SimpleSearchCriteria<T> getDefaultCriteria() {
        return new SimpleSearchCriteria<T>(type);
    }
}
