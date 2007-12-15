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
package org.jcatapult.dbmgr.database;

import java.sql.Connection;
import javax.sql.DataSource;

/**
 * Factory interface used to supply database connections and datasources
 *
 * User: jhumphrey
 * Date: Nov 26, 2007
 */
public interface DatabaseProvider {

    /**
     * Returns a datasource
     *
     * @return a javax.sql.Datasource object
     */
    public DataSource getDatasource();

    /**
     * Returns a connection
     *
     * @return a java.sql.Connection
     */
    public Connection getConnection();
}
