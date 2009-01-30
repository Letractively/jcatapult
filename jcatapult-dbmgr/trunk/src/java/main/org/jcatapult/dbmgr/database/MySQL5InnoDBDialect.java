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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import static net.java.lang.ObjectTools.*;

/**
 * <p>
 * This class is the mysql 5 with innoDB SQL dialect.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class MySQL5InnoDBDialect implements Dialect {
    private String tableName;
    private List<Column> columns = new ArrayList<Column>();
    private List<Key> keys = new ArrayList<Key>();
    private StringBuilder build = new StringBuilder();

    public void startScript() {
        System.out.println("Start");
        // Nothing
    }

    public void startCreateTable(String name) {
        System.out.println("Table");
        tableName = name;
    }

    public void addColumn(String name, String type, boolean nullable) {
        Column column = new Column();
        column.name = name;
        column.type = type;
        column.nullable = nullable;
        columns.add(column);
    }

    public void addForeignKey(String name, String reference, String id, boolean nullable) {
        Column column = new Column();
        column.name = name;
        column.type = "integer";
        column.nullable = nullable;
        columns.add(column);

        Key key = new Key();
        key.tableName = tableName;
        key.reference = reference;
        key.column = column;
        key.id = id;
        key.primary = false;
        keys.add(key);
    }

    public void addId(String name) {
        Column column = new Column();
        column.name = name;
        column.type = "integer";
        column.nullable = false;
        column.autoIncrement = true;
        columns.add(column);

        Key key = new Key();
        key.primary = true;
        key.column = column;
        keys.add(key);
    }

    public void endCreateTable() {
        build.append("create table ").append(tableName).append("(");
        boolean first = true;
        for (Column column : columns) {
            if (!first) {
                build.append(",");
            }

            build.append(column.name).append(" ").append(column.type);
            if (!column.nullable) {
                build.append(" not null");
            }
            if (column.autoIncrement) {
                build.append(" auto_increment");
            }
            first = false;
        }
        columns.clear();

        for (Iterator<Key> i = keys.iterator(); i.hasNext();) {
            Key key = i.next();
            if (key.primary) {
                build.append(",");
                build.append("primary key (").append(key.column.name).append(")");
                i.remove();
            }
        }
        
        build.append(") Engine=InnoDB;\n");
    }

    public void insert(String tableName, Map<String, Object> values) {
        build.append("insert into ").append(tableName).append(" (").append(join(values.keySet(), ",")).append(") values (");
        boolean first = true;
        for (Object o : values.values()) {
            if (!first) {
                build.append(",");
            }

            if (o instanceof DateTime) {
                // We have to assume that the MySQL instance is using the system timezone and convert
                // the date time to that and then format to the mysql lame ass format
                DateTime dt = (DateTime) o;
                dt = dt.withZone(DateTimeZone.getDefault());
                String format = DateTimeFormat.forPattern("YYYY-MM-dd hh:mm:ss").print(dt);
                build.append("'").append(format).append("'");
            } else if (o instanceof LocalDate) {
                LocalDate ld = (LocalDate) o;
                String format = DateTimeFormat.forPattern("YYYY-MM-dd").print(ld);
                build.append("'").append(format).append("'");
            } else if (o instanceof String){
                build.append("'").append(o.toString()).append("'");
            } else {
                build.append(o.toString());
            }

            first = false;
        }
        build.append(");");
    }

    public String endScript() {
        System.out.println("End");
        for (Key key : keys) {
            if (!key.primary) {
                build.append("alter table ").append(key.tableName).
                    append(" add index ").append(key.id).append("(").append(key.column.name).append("),").
                    append(" add constraint ").append(key.id).append(" foreign key (").append(key.column.name).append(")").
                    append(" references ").append(key.reference).append(";\n");
            }
        }
        
        return build.toString();
    }

    public class Column {
        public String name;
        public String type;
        public boolean nullable;
        public boolean autoIncrement;
    }

    public class Key {
        public String tableName;
        public boolean primary;
        public Column column;
        public String reference;
        public String id;
    }
}
