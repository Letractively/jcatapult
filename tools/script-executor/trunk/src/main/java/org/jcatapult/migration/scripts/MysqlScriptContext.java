package org.jcatapult.migration.scripts;

import java.sql.Connection;

/**
 * @author jhumphrey
 */
public class MysqlScriptContext extends BaseScriptContext {

    /**
     * {@inheritDoc}
     */
    public MysqlScriptContext(String dbUrl, String dbUsername, String dbPassword) {
        super(dbUrl, dbUsername, dbPassword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
    }
}
