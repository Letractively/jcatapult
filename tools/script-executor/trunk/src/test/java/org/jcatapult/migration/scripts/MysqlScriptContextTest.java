package org.jcatapult.migration.scripts;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author jhumphrey
 */
public class MysqlScriptContextTest {

    @Test
    public void testConnection() {
        String url = "jdbc:mysql://localhost:3306/script_executor";
        String username = "script";
        String password = "executor";
        try {
            new MysqlScriptContext(url, username, password);
        } catch (Exception e) {
            System.err.println("This test requires that MySQL be " +
                "installed on the system and that a database called 'script_executor' " +
                "is created with grants for username 'script' with password 'executor'.  " +
                "Please refer to the src/test/database directory for sql migration to help with this setup");
            Assert.fail();
        }
    }
}
