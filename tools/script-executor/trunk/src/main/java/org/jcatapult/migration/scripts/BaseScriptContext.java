package org.jcatapult.migration.scripts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Base ScriptContext
 *
 * @author jhumphrey
 */
public abstract class BaseScriptContext implements ScriptContext {

    protected String dbUrl;
    protected String dbUsername;
    protected String dbPassword;
    protected Connection connection;

    /**
     * Constructs a base script context
     *
     * @param dbUrl the db url
     * @param dbUsername the db username
     * @param dbPassword the db password
     */
    protected BaseScriptContext(String dbUrl, String dbUsername, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;

        try {
            initDriver();
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() {
        return connection;
    }

    /**
     * Initializes the database driver
     *
     * @throws ClassNotFoundException driver class not found
     */
    protected abstract void initDriver() throws ClassNotFoundException;
}
