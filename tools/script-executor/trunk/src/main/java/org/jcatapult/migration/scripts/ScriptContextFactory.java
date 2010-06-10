package org.jcatapult.migration.scripts;

/**
 * Factory for creating ScriptContext objects
 *
 * @author jhumphrey
 */
public class ScriptContextFactory {
    private static ScriptContextFactory ourInstance = new ScriptContextFactory();

    /**
     * Returns this instance
     *
     * @return this instance
     */
    public static ScriptContextFactory getInstance() {
        return ourInstance;
    }

    private ScriptContextFactory() {
    }

    /**
     * Creates a script context
     *
     * @param databaseType the database type
     * @param dbUrl the database url
     * @param dbUsername the database username
     * @param dbPassword the database password
     * @return ScriptContext
     */
    public ScriptContext createScriptContext(DatabaseType databaseType, String dbUrl, String dbUsername, String dbPassword) {

        switch (databaseType) {
            case MYSQL:
                return new MysqlScriptContext(dbUrl, dbUsername, dbPassword);
            case PSQL:
                return new PostgresScriptContext(dbUrl, dbUsername, dbPassword);
            default:
                return null;

        }
    }
}
