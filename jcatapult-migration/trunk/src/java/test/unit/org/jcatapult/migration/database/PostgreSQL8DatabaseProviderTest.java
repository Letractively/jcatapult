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

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

/**
 * User: jhumphrey
 * Date: Nov 28, 2007
 */
@Ignore
public class PostgreSQL8DatabaseProviderTest {

    @Test
    public void testProviderGetConnection() throws SQLException {
        PostgreSQL8DatabaseProvider p = new PostgreSQL8DatabaseProvider("jdbc:postgresql://localhost/postgres8_database_provider_test?user=dev&password=dev");
        Connection c = p.getConnection();
        Assert.assertNotNull(c);
    }

    @Test
    public void testProviderGetDatasource() throws SQLException {
        PostgreSQL8DatabaseProvider p = new PostgreSQL8DatabaseProvider("jdbc:postgresql://localhost/postgres8_database_provider_test?user=dev&password=dev");
        DataSource d = p.getDatasource();
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getConnection());
    }
}
