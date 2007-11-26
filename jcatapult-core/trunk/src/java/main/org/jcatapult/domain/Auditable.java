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
 * This interface marks a class as being auditable where the user and time the
 * class was saved to the database is saved as well as the user and time it was
 * last updated is also saved.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface Auditable {
    DateTime getInsertDate();
    void setInsertDate(DateTime insertDate);

    DateTime getUpdateDate();
    void setUpdateDate(DateTime updateDate);

    String getInsertUser();
    void setInsertUser(String insertUser);

    String getUpdateUser();
    void setUpdateUser(String updateUser);
}