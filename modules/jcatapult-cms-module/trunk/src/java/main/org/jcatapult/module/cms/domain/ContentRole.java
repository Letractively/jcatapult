/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.cms.domain;

/**
 * <p>
 * This enum contains the roles for the CMS system. These are
 * directly equal to the role names in the database.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public enum ContentRole {
    /**
     * The role of a content editor. They are allowed to submit content but not publish (approve) it
     * into a live site.
     */
    editor,

    /**
     * The role of a content publisher. They are allowed to submit content and publish (approve) it
     * into a live site.
     */
    publisher
}