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
 */
package org.jcatapult.domain;

import org.joda.time.DateTime;

/**
 * <p>
 * This interface defines an Entity that has time stamp information
 * for when the database record was created and last updated.
 * </p>
 *
 * @author Brian Pontarelli
 */
public interface TimeStampable {
    /**
     * @return  The date the Entity was inserted on or null if the entity is trasient.
     */
    DateTime getInsertDate();

    /**
     * @param   dateTime The date the Entity was inserted on or null if the entity is trasient.
     */
    void setInsertDate(DateTime dateTime);

    /**
     * @return  The date the Entity was updated on or null if the entity is trasient.
     */
    DateTime getUpdateDate();

    /**
     * @param   dateTime The date the Entity was updated on or null if the entity is trasient.
     */
    void setUpdateDate(DateTime dateTime);
}