package org.jcatapult.dbmgr.utils;

import net.java.util.Version;

/**
 * Utilities for interacting with database SQL script files located in the project db directories
 *
 * @author jhumphrey
 */
public class ScriptUtils {

    /**
     * <p>Extracts the version number from the sql seed, alter, and base scripts.  If the filename is a base script (tables.sql) then
     * this method returns 0 as the version number since the tables.sql file is versionless</p>
     *
     * @param fileName the sql filename.  Must end with '.sql'
     * @return Version object
     */
    public static Version extractVersionFromFileName(String fileName) {

        Version v;

        if (!ScriptUtils.isValidFileNameFormat(fileName)) {
            throw new RuntimeException("Script file [" + fileName + "] is not a valid filename format.  The filename must end with '.sql', " +
                "not be equal to '.sql', and be equal to 'tables.sql' or start with a valid version number (e.g. 1.0.sql, 1.0-seed-tables.sql)." +
                "For more information please reference the JCatapult Database Manager docs: http://code.google.com/p/jcatapult/wiki/GuideDatabaseManagement");
        }

        if (fileName.equals("tables.sql")) {
            return Version.ZERO;
        }

        String fileNameWithStrippedExt = fileName.split(".sql")[0];

        if (fileNameWithStrippedExt.matches(ScriptUtils.getVersionRegEx())) {
            v = new Version(fileNameWithStrippedExt);
        } else {
            String[] tokens = fileNameWithStrippedExt.split(ScriptUtils.getVersionRegEx());
            int lastVersionStringIndex = fileNameWithStrippedExt.indexOf(tokens[1]);
            String versionString = fileNameWithStrippedExt.substring(0, lastVersionStringIndex);
            v = new Version(versionString);
        }

        return v;
    }


    public void test(String str) {
        String two = "(?>alpha|ALPHA|beta|BETA|milestone|MILESTONE|rc|RC|snapshot|SNAPSHOT)([0-9]+)?)";
        String one = "((TWO|((a|A|b|B|m|M)[0-9]+)?)";
        String regex = "(-ONE)?";
        System.out.println(str.matches(regex));
    }

    /**
     * <p>The regular expression string used to match against a Version number</p>
     *
     * @return the Version regular expression
     */
    public static String getVersionRegEx() {
        return "[0-9]+\\Q.\\E[0-9]+(\\Q.\\E[0-9]+)?(-((?>alpha|ALPHA|beta|BETA|milestone|MILESTONE|rc|RC|snapshot|SNAPSHOT)(([0-9]+)?)|(a|A|b|B|m|M)[0-9]+))?";

    }

    /**
     * <p>Returns true if the script file is a valid filename format.  A valid filename format must meet the criteria listed below</p>
     *
     * <ul>
     *  <li>File name must end with '.sql' but not be equal to '.sql'.  AND..</li>
     *  <li>Can be equal 'tables.sql'.  OR...</li>
     *  <li>If not equal to 'tables.sql' it must start with a valid version number</li>
     * </ul>
     *
     * <p>Valid Examples:</p>
     * <ul>
     * <li>tables.sql</li>
     * <li>1.0.sql</li>
     * <li>1.0.1.sql</li>
     * <li>1.0.1-patch-script.sql</li>
     * <li>1.0-A1-alpha-script.sql</li>
     * </ul>
     *
     * @param fileName script file name
     * @return true if valid, false otherwise
     */
    public static boolean isValidFileNameFormat(String fileName) {

        // test 1: if it doesn't end with '.sql' then it's not valid
        if (!fileName.endsWith(".sql")) {
            return false;
        }

        // test 2: if the file name is just '.sql' then it's not valid
        if (fileName.equals(".sql")) {
            return false;
        }

        // test 3: if the filename is 'tables.sql' then it's valid
        if (fileName.equals("tables.sql")) {
            return true;
        }

        // test 4: make sure that the filename starts with a valid version number
        String[] tokens = fileName.split(ScriptUtils.getVersionRegEx());
        if (tokens[0].equals("")) {
            return true;
        }

        return false;
    }
}
