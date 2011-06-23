/*
 * Copyright (c) 2001-2011, JCatapult.org, All Rights Reserved
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
package org.jcatapult.persistence.hibernate;

import java.sql.Types;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * This class is an extension of the main MySQL 5 dialect that adds support for UUID handling using columns of type
 * <strong>binary(16)</strong>.
 *
 * @author Brian Pontarelli
 */
public class MySQL5InnoDBWithUUIDDialect extends MySQL5InnoDBDialect {
  public MySQL5InnoDBWithUUIDDialect() {
    super();
    registerColumnType(Types.BINARY, "binary");
  }
}
