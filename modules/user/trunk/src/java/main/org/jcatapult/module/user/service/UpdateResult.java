/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.service;

/**
 * <p>
 * This class stores the various result from a user update.
 * </p>
 *
 * @author Brian Pontarelli
 */
public enum UpdateResult {
    /**
     * Occurs when the login name supplied has been removed, deleted, etc.
     */
    MISSING,

    /**
     * Occurs when the user could not be updated to the database.
     */
    ERROR,

    /**
     * Occurs when the user is updated successfully.
     */
    SUCCESS
}