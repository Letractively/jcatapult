/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.crud.guice;

import com.google.inject.AbstractModule;

import org.jcatapult.crud.control.SearchResults;

/**
 * <p>
 * This is a module that registers the CRUD search result component.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class CrudModule extends AbstractModule {
    /**
     * Binds the SearchResults component.
     */
    protected void configure() {
        bind(SearchResults.class);
    }
}