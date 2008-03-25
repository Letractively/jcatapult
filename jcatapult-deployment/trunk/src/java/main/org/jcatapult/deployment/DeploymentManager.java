package org.jcatapult.deployment;

import java.io.IOException;
import java.io.File;
import java.util.TreeSet;
import java.util.List;

import org.jcatapult.deployment.service.XmlService;
import org.jcatapult.deployment.service.XmlServiceException;
import org.jcatapult.deployment.service.CLIService;
import org.jcatapult.deployment.service.BetterSimpleCompletor;
import org.jcatapult.deployment.domain.jaxb.Deploy;
import org.jcatapult.deployment.domain.jaxb.Server;
import org.jcatapult.deployment.domain.jaxb.Environment;
import static org.jcatapult.deployment.domain.ServerInfo.*;
import org.jcatapult.deployment.guice.DeploymentModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeploymentManager {

    Injector injector = Guice.createInjector(new DeploymentModule());
    CLIService cliService = injector.getInstance(CLIService.class);
    BetterSimpleCompletor completor = injector.getInstance(BetterSimpleCompletor.class);

    /**
     * Main point of entry into the deployment manager api
     *
     * @param args arguments
     * @throws IOException thrown on errors
     * @throws org.jcatapult.deployment.service.XmlServiceException on xml service exception
     */
    public static void main(String[] args) throws IOException, XmlServiceException {
        String deployerHome = System.getProperty("deployer.home");
        if (deployerHome == null) {
            System.err.println("You must provide the deployer.home Java property");
            System.exit(1);
        } else {
            System.out.println("Using jCatapult Deployment Manager [" + deployerHome + "]");
        }

        new DeploymentManager().manage(args);
    }

    /**
     * Manages the deployment process
     *
     * @param args args from main method
     * @throws XmlServiceException thrown if there's an issue during unmarshalling of the xml file
     */
    private void manage(String... args) throws XmlServiceException {
        String helpMsg = "Usage: DeploymentManager <deploy file config path>";

        // get the file path of the local project deploy xml file
        File deployConfigFile = new File(args[0]);

        // get the xml service
        XmlService xs = injector.getInstance(XmlService.class);

        // configure command line options.  empty for now
        Options options = new Options();

        CommandLineParser parser = new PosixParser();
        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("Unable to run jcatapult deployer");
            return;
        }

        String[] commands = line.getArgs();
        if (commands.length == 0 && !commands[0].equals("help")) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("Usage: deploy <path to deploy config file>", options);
            System.exit(1);
        } else if (commands.length == 1) {
            if (commands[0].equals("help")) {
                System.out.println(helpMsg);
            } else {
                // get the available servers
                Deploy deploy = xs.unmarshallXml(deployConfigFile);
                List<Server> servers = deploy.getServer();
                TreeSet<String> serverStrings = new TreeSet<String>();
                for (Server server : servers) {
                    serverStrings.add(server.getHost());
                }

                // set server strings into completor
                completor.setCandidates(serverStrings);

                // get the server host
                serverHost = cliService.ask("Please select the server host you are deploying to:",
                    "Remote deploying to ", "Invalid server host.  Use tab completion to view available server hosts",
                    serverStrings.first(), completor);

                // get the available environments for the selected server
                List<Environment> envs = null;
                for (Server server : servers) {
                    if (server.getHost().equals(serverHost)) {
                        serverUsername = server.getUsername();
                        serverPassword = server.getPassword();
                        envs = server.getEnvironment();
                    }
                }
                TreeSet<String> envStrings = new TreeSet<String>();
                for (Environment env : envs) {
                    envStrings.add(env.getType());
                }

                // set environment strings into completor
                completor.setCandidates(envStrings);

                // get the env type
                envType = cliService.ask("Please select the environment:", "deploy environment set to: ",
                    "Invalid environment type.  Use tab completion to view available environment types",
                    envStrings.first(), completor);

                // loop through environments to get match against chosen environment type
                for (Environment env : envs) {
                    if (env.getType().equals(envType)) {
                        homeDir = env.getHomeDir();
                        workDir = env.getWorkDir();
                        fileDir = env.getFileDir();
                        webDir = env.getWorkDir();
                        dbName = env.getDbName();
                        dbPassword = env.getDbPassword();
                        dbUsername = env.getDbUsername();
                    }
                }

                // todo: now that server info has been collected, do deployment
            }
        }
    }
}
