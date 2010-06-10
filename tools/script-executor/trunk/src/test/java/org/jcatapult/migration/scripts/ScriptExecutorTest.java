package org.jcatapult.migration.scripts;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author jhumphrey
 */
public class ScriptExecutorTest {

    @Test
    public void testHelpWithArg() {
        try {
            ScriptExecutor.main("help");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testHelpWithOutArg() {
        try {
            ScriptExecutor.main();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testFailCases() {

        try {
            ScriptExecutor.parseArgs("foo");
            Assert.fail();
        } catch (Exception e) {
            // success
        }

        try {
            ScriptExecutor.parseArgs("foo", "bar");
            Assert.fail();
        } catch (Exception e) {
            // success
        }

        try {
            ScriptExecutor.parseArgs("foo", "bar", "baz");
            Assert.fail();
        } catch (Exception e) {
            // success
        }
    }

    @Test
    public void testSuccessCases() {

        try {
            ScriptExecutor.parseArgs("-j", "foo", "-f", "bar", "-u", "baz", "-p", "gux", "-d", "waldo");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testMakeScriptWithoutExtension() {

        try {
            ScriptExecutor.makeScript("src/test/groovy/PostgresTestScript");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testMakeScriptWithExtension() {

        try {
            ScriptExecutor.makeScript("src/test/groovy/PostgresTestScript.groovy");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
