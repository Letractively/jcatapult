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
package org.jcatapult.migration.database;

/**
 * User: jhumphrey
 * Date: Nov 27, 2007
 */
public class DatabaseProviderFactory {
    public static DatabaseProvider getProvider(String dbType, String dbURL) {

        DatabaseProvider dp = null;

        if (dbType.toLowerCase().equals("mysql5")) {
            dp = new MySQL5DatabaseProvider(dbURL);
        }

        if (dp == null) {
            throw new IllegalArgumentException("No Database Provider exists for the db-type [" + dbType + "]");
        }

        return dp;
    }
}
