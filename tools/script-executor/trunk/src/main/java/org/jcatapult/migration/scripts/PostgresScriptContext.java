package org.jcatapult.migration.scripts;

/**
 * Context for migration running against Postgres
 *
 * @author jhumphrey
 */
public class PostgresScriptContext extends BaseScriptContext {

    /**
     * {@inheritDoc}
     */
    public PostgresScriptContext(String dbUrl, String dbUsername, String dbPassword) {
        super(dbUrl, dbUsername, dbPassword);
    }

    @Override
    protected void initDriver() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }
}
