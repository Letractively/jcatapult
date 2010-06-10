package org.jcatapult.migration.scripts;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author jhumphrey
 */
public class DatabaseTypeTest {

    @Test
    public void test() {

        try {
            String mysql = "mysql";
            DatabaseType.valueOf(mysql.toUpperCase());
        } catch (IllegalArgumentException e) {
            Assert.fail();
        }

        try {
            String psql = "psql";
            DatabaseType.valueOf(psql.toUpperCase());
        } catch (IllegalArgumentException e) {
            Assert.fail();
        }


    }
}
