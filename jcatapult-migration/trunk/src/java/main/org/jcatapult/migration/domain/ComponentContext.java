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
package org.jcatapult.migration.domain;

import net.java.util.Version;

/**
 * Models component context.
 *
 * Every JCatapult Component has a jar and a ersion stored in the
 * {@link org.jcatapult.migration.DatabaseManager#VERSION_TABLE} table.  If the component version is not yet
 * stored in the database, then the database version will be null
 *
 * User: jhumphrey
 * Date: Dec 13, 2007
 */
public class ComponentContext {
    private ComponentJar componentJar;
    private Version databaseVersion;

    /**
     * Constructor
     *
     * @param componentJar the component jar
     * @param databaseVersion the version of the component stored in the
     * {@link org.jcatapult.migration.DatabaseManager#VERSION_TABLE}
     */
    public ComponentContext(ComponentJar componentJar, Version databaseVersion) {
        this.componentJar = componentJar;
        this.databaseVersion = databaseVersion;
    }

    public ComponentJar getComponentJar() {
        return componentJar;
    }

    public Version getDatabaseVersion() {
        return databaseVersion;
    }
}
