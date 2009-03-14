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
package org.jcatapult.user.domain;

import org.jcatapult.persistence.domain.Auditable;
import org.joda.time.DateTime;

/**
 * <p>
 * This class provides all the auditing information for the User including
 * creation, update, and login times. 
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface AuditableUser<T extends Role> extends User<T>, Auditable {
    /**
     * @return  The time when the user last logged in.
     */
    DateTime getLastLogin();

    /**
     * Sets the time when the user last logged in.
     *
     * @param   lastLogin The last login.
     */
    void setLastLogin(DateTime lastLogin);
}
