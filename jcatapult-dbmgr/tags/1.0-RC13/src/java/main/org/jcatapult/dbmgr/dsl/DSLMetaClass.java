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
package org.jcatapult.dbmgr.dsl;

import java.util.Map;

import org.jcatapult.dbmgr.database.Dialect;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import groovy.lang.Closure;
import groovy.lang.MetaClassImpl;

/**
 * <p>
 * This class is the Groovy meta class that handles the database DSL language
 * keywords as methods or properties, depending.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class DSLMetaClass extends MetaClassImpl {
    private final Dialect dialect;

    public DSLMetaClass(Class type, Dialect dialect) {
        super(type);
        this.dialect = dialect;
    }

    @Override
    public Object invokeMissingMethod(Object o, String s, Object[] objects) {
        if (s.equals("table")) {
            handleTable(objects);
        } else if (s.equals("id")) {
            handleID(objects);
        } else if (s.equals("column")) {
            handleColumn(objects);
        } else if (s.equals("foreignKey")) {
            handleForeignKey(objects);
        } else if (s.equals("insert")) {
            handleInsert(objects);
        }

        if (s.equals("now")) {
            return handleNow(objects);
        } else if (s.equals("date")) {
            return handleDate(objects);
        } else if (s.equals("datetime")) {
            return handleDateTime(objects);
        } else if (s.equals("string")) {
            if (objects.length == 1) {
                int size = (Integer) objects[0];
                return "varchar(" + size + ")";
            }

            return "varchar(255)";
        }

        return null;
    }

    private void handleInsert(Object[] objects) {
        if (objects.length != 2 || !(objects[1] instanceof Map)) {
            throw new DSLException("The insert command takes 2 parameters, a table name and a Map of values to insert.");
        }

        String tableName = objects[0].toString();
        Map<String, Object> values = (Map<String, Object>) objects[1];
        dialect.insert(tableName, values);
    }

    private void handleTable(Object[] objects) {
        if (objects.length != 2 || !(objects[1] instanceof Closure)) {
            throw new DSLException("The table command takes 2 parameters, a name and a closure.");
        }

        String name = objects[0].toString();
        Closure closure = (Closure) objects[1];
        dialect.startCreateTable(name);
        closure.call();
        dialect.endCreateTable();
    }

    private void handleID(Object[] objects) {
        if (objects.length != 1) {
            throw new DSLException("The id command takes 1 parameter, a name.");
        }

        String name = objects[0].toString();
        dialect.addId(name);
    }

    private void handleColumn(Object[] objects) {
        if (objects.length == 1) {
            throw new DSLException("The column command must have at least 2 parameters, a name and type.");
        }

        String name = objects[0].toString();

        String type;
        if (objects[1] instanceof Class) {
            type = ((Class) objects[1]).getSimpleName();
        } else {
            type = objects[1].toString();
        }

        boolean nullable = false;
        if (objects.length == 3) {
            nullable = Boolean.parseBoolean(objects[2].toString());
        }

        dialect.addColumn(name, type, nullable);
    }

    private void handleForeignKey(Object[] objects) {
        if (objects.length == 2) {
            throw new DSLException("The foreignKey command must have at least 3 parameters, a name, a reference, and an id.");
        }

        String name = objects[0].toString();
        String reference = objects[1].toString();
        String id = objects[2].toString();

        boolean nullable = false;
        if (objects.length == 4) {
            nullable = Boolean.parseBoolean(objects[2].toString());
        }

        dialect.addForeignKey(name, reference, id, nullable);
    }

    private DateTime handleNow(Object[] objects) {
        if (objects.length != 0) {
            throw new DSLException("The now command takes no parameters.");
        }

        return new DateTime();
    }

    private LocalDate handleDate(Object[] objects) {
        if (objects.length != 1) {
            throw new DSLException("The date command takes 1 parameters, the ISO date string.");
        }

        return ISODateTimeFormat.localDateParser().parseDateTime(objects[0].toString()).toLocalDate();
    }

    private DateTime handleDateTime(Object[] objects) {
        if (objects.length != 1) {
            throw new DSLException("The datetime command takes 1 parameters, the ISO date string.");
        }

        return ISODateTimeFormat.dateTimeParser().parseDateTime(objects[0].toString());
    }

    @Override
    public Object invokeMissingProperty(Object o, String s, Object o1, boolean b) {
        if (s.equals("nullable")) {
            return true;
        } else {
            return s; // This is a column type
        }
    }
}
