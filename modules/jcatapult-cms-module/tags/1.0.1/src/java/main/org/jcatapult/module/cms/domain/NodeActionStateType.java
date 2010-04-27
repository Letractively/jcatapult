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
package org.jcatapult.module.cms.domain;

/**
 * <p>
 * This class tracks the different states of the content within the CMS.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public enum NodeActionStateType {
    /**
     * The content was submitted for approval.
     */
    PENDING,

    /**
     * The content was approved by an admin of some sort.
     */
    APPROVED,

    /**
     * The content was rejected but is able to be re-submitted for approval again.
     */
    REJECTED,

    /**
     * The content was declined and cannot be re-submitted for approval.
     */
    DECLINED,

    /**
     * The content has been archived but is still active.
     */
    ARCHIVED
}