package org.jcatapult.migration.scripts;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This test requires that you have Postgres installed on your workstation
 * and have a Database called 'script_executor' with grants for username
 * username 'script' with password 'executor'
 *
 * @author jhumphrey
 */
public class PostgresScriptContextTest {

    @Test
    public void testConnection() {
        String url = "jdbc:postgresql://localhost:5432/script_executor";
        String username = "script";
        String password = "executor";
        try {
            new PostgresScriptContext(url, username, password);
        } catch (Exception e) {
            System.err.println("This test requires that Postgres be " +
                "installed on the system and that a database called 'script_executor' " +
                "is created with grants for username 'script' with password 'executor'.  " +
                "Please refer to the src/test/database directory for sql migration to help with this setup");
            Assert.fail();
        }
    }
}
