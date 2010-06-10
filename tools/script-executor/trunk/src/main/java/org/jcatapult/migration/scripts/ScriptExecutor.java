package org.jcatapult.migration.scripts;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import groovy.lang.GroovyClassLoader;

/**
 * Script executor contains the main for running this application.
 *
 * @author jhumphrey
 */
public class ScriptExecutor {

    public static final OptionSet OPTION_SET = new OptionSet();

    public static void main(String... args) throws ParseException {

        if (args.length == 0 || args[0].equals("help")) {
            printHelp(OPTION_SET.options);
        } else {

            CommandLine line = parseArgs(args);

            // extract options values from the command line
            String scriptAbsPath = line.getOptionValue(OPTION_SET.scriptAbsPathOpt.getOpt());
            String dbUrl = line.getOptionValue(OPTION_SET.jdbcUrlOpt.getOpt());
            String dbUsername = line.getOptionValue(OPTION_SET.usernameOpt.getOpt());
            String dbPassword = line.getOptionValue(OPTION_SET.passwordOpt.getOpt());
            String dbType = line.getOptionValue(OPTION_SET.dbTypeOpt.getOpt());

            // make the script
            Script script = ScriptExecutor.makeScript(scriptAbsPath);

            // create the script context
            ScriptContextFactory factory = ScriptContextFactory.getInstance();
            ScriptContext context = factory.createScriptContext(DatabaseType.valueOf(dbType.toUpperCase()), dbUrl, dbUsername, dbPassword);

            // execute the script
            script.execute(context);
        }
    }

    public static CommandLine parseArgs(String... args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        return parser.parse(OPTION_SET.options, args);
    }

    private static void printHelp(Options options) {
        HelpFormatter help = new HelpFormatter();
        help.printHelp("java -jar script-executor.jar [options]", options);
    }

    public static Script makeScript(String scriptAbsPath) {
        if (!scriptAbsPath.endsWith(".groovy")) {
            scriptAbsPath += ".groovy";
        }
        File scriptFile = new File(scriptAbsPath);
        if (!scriptFile.exists()) {
            System.err.println("Invalid script [" + scriptAbsPath + "]");
        }

        GroovyClassLoader gcl = new GroovyClassLoader();
        File libDir = new File(scriptFile.getParentFile(), "lib");
        if (libDir.exists()) {
            File[] libs = libDir.listFiles();
            for (File lib : libs) {
                try {
                    gcl.addURL(lib.toURI().toURL());
                } catch (MalformedURLException e) {
                    System.err.println("Unable to load Script library [" + lib.getAbsolutePath() + "]");
                    System.exit(1);
                }
            }
        }

        try {
            Class clazz = gcl.parseClass(scriptFile);
            return (Script) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unable to run script");
            System.exit(1);
            return null;
        }
    }

    private static class OptionSet {
        Option jdbcUrlOpt = new Option("j", "url", true, "JDBC URL");
        Option scriptAbsPathOpt = new Option("f", "file", true, "The script absolute path");
        Option usernameOpt = new Option("u", "username", true, "The database username");
        Option passwordOpt = new Option("p", "password", true, "The database password");
        Option dbTypeOpt = new Option("d", "database", true, "The database type (mysql | psql)");

        Options options = new Options();

        private OptionSet() {

            jdbcUrlOpt.setRequired(true);
            options.addOption(jdbcUrlOpt);


            scriptAbsPathOpt.setRequired(true);
            options.addOption(scriptAbsPathOpt);


            usernameOpt.setRequired(true);
            options.addOption(usernameOpt);

            passwordOpt.setRequired(true);
            options.addOption(passwordOpt);

            dbTypeOpt.setRequired(true);
            options.addOption(dbTypeOpt);
        }
    }
}
