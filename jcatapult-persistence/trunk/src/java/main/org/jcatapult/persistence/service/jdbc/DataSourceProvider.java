/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.service.jdbc;

import javax.sql.DataSource;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This class is a provider for a JDBC DataSource. This uses the {@link JDBCService} to get the data source from.
 *
 * @author Brian Pontarelli
 */
public class DataSourceProvider implements Provider<DataSource> {
  private final JDBCService service;

  @Inject
  public DataSourceProvider(JDBCService service) {
    this.service = service;
  }

  @Override
  public DataSource get() {
    return service.getDataSource();
  }
}
