package org.jcatapult.migration.scripts;

/**
 * Represents a database type
 *
 * @author jhumphrey
 */
public enum DatabaseType {
    MYSQL("mysql"),
    PSQL("psql");

    String type;

    DatabaseType(String type) {
        this.type = type;
    }
}
