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

import java.io.InputStream;

import net.java.util.Version;

/**
 * Domain object to model a jcatapult SQL script.  All jcatapult scripts will
 * have the following naming convention:
 *
 * Version-Label.sql
 *
 * Where Version is a Major, Minor, or Patch number and the Label is any arbitrary
 * set of characters describing the sql file.
 *
 * Ex:
 * 1.0-seed-for-table-x.sql
 *
 * JCatapult SQL Scripts will also be of a certain type, where the type refers to
 * whether it's a base, alter, or seed script
 *
 * User: jhumphrey
 * Date: Dec 12, 2007
 */
public class SQLScript implements Comparable<SQLScript> {

    private InputStream inputStream;

    private String filename;

    private SQLScriptType type;

    private Version version;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public SQLScriptType getType() {
        return type;
    }

    public void setType(SQLScriptType type) {
        this.type = type;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    /**
     * Delegates to the Version object for comparison
     */
    public int compareTo(SQLScript sqlScript) {
        return version.compareTo(sqlScript.getVersion());
    }

    @Override
    public String toString() {
        return filename;
    }
}
