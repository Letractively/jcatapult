/*
 * Copyright (c) 2009, JCatapult.org, All Rights Reserved
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