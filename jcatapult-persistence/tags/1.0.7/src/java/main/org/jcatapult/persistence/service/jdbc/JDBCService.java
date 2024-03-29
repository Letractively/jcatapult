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
import java.sql.Connection;

import com.google.inject.ImplementedBy;

/**
 * <p> This interface defines the operations for JDBC. </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(DefaultJDBCService.class)
public interface JDBCService {
  /**
   * @return The data source or null if JDBC is disabled.
   */
  DataSource getDataSouce();

  /**
   * This method checks the current context for an existing connection or creates a new connection and puts it in the
   * current context.
   *
   * @return The connection or null if JDBC is disabled.
   */
  Connection setupConnection();

  /**
   * Removes the connection (if any) from the current context.
   */
  void tearDownConnection();
}
