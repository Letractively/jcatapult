package org.jcatapult.migration.scripts;

import java.sql.Connection;

/**
 * Context for running migration scripts
 *
 * @author jhumphrey
 */
public interface ScriptContext {

    /**
     * Returns a Connection to a database
     *
     * @return the database connection
     */
    public Connection getConnection();
}
