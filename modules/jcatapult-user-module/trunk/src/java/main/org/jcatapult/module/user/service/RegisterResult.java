/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.service;

/**
 * <p>
 * This class stores the various result from a user registration.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public enum RegisterResult {
    /**
     * Occurs when the login name supplied already exists.
     */
    EXISTS,

    /**
     * Occurs when the user could not be updated to the database.
     */
    ERROR,

    /**
     * Occurs when the user is updated successfully.
     */
    SUCCESS
}