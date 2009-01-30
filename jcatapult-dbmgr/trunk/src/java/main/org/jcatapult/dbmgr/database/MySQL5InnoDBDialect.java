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
import java.util.List;
import java.util.Iterator;

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

    public void addForeignKey(String name, String reference, boolean nullable) {
        Column column = new Column();
        column.name = name;
        column.type = "integer";
        column.nullable = nullable;
        columns.add(column);

        Key key = new Key();
        key.tableName = tableName;
        key.reference = reference;
        key.column = column;
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

    public String endScript() {
        System.out.println("End");
        for (Key key : keys) {
            if (!key.primary) {
                String id = makeID();
                build.append("alter table ").append(key.tableName).
                    append(" add index FK").append(id).append("(").append(key.column.name).append("),").
                    append(" add constraint FK").append(id).append(" foreign key (").append(key.column.name).append(")").
                    append(" references ").append(key.reference).append(";\n");
            }
        }
        
        return build.toString();
    }

    private String makeID() {
        return "" + System.currentTimeMillis();
    }

    public class Column {
        private String name;
        private String type;
        private boolean nullable;
        private boolean autoIncrement;
    }

    public class Key {
        private String tableName;
        private boolean primary;
        private Column column;
        public String reference;
    }
}
