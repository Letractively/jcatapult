/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.crud.guice;

import com.google.inject.AbstractModule;

import com.inversoft.crud.control.SearchResults;

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