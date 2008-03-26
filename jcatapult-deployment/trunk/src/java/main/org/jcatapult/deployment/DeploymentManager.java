package org.jcatapult.deployment;

import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.TreeSet;
import java.util.List;

import org.jcatapult.deployment.service.XmlService;
import org.jcatapult.deployment.service.XmlServiceException;
import org.jcatapult.deployment.service.CLIService;
import org.jcatapult.deployment.service.BetterSimpleCompletor;
import org.jcatapult.deployment.service.DeployerService;
import org.jcatapult.deployment.domain.jaxb.Deploy;
import org.jcatapult.deployment.domain.jaxb.Server;
import org.jcatapult.deployment.domain.jaxb.Environment;
import org.jcatapult.deployment.domain.DeployInfo;
import org.jcatapult.deployment.guice.DeploymentModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

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
    XmlService<Deploy, File> xmlService = injector.getInstance(Key.get(new TypeLiteral<XmlService<Deploy, File>>(){}));
    CLIService cliService = injector.getInstance(CLIService.class);
    DeployerService deployerService = injector.getInstance(DeployerService.class);
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
                Deploy deploy = xmlService.unmarshall(deployConfigFile);
                List<Server> servers = deploy.getServer();
                TreeSet<String> serverStrings = new TreeSet<String>();
                for (Server server : servers) {
                    serverStrings.add(server.getHost());
                }

                DeployInfo deployInfo = new DeployInfo();
                deployInfo.setProjectXml(new File("project.xml").getAbsoluteFile());
                deployInfo.setProjectName(deploy.getProjectName());

                // set server strings into completor
                completor.setCandidates(serverStrings);

                // get the server host
                String serverHost = cliService.ask("Please select the server host you are deploying to:",
                    "Remote deploying to ", "Invalid server host.  Use tab completion to view available server hosts",
                    serverStrings.first(), completor);
                deployInfo.setServerHost(serverHost);

                // get the available environments for the selected server
                List<Environment> envs = null;
                for (Server server : servers) {
                    if (server.getHost().equals(serverHost)) {
                        deployInfo.setServerUsername(server.getUsername());
                        deployInfo.setServerPassword(server.getPassword());
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
                String envType = cliService.ask("Please select the environment:", "deploy environment set to: ",
                    "Invalid environment type.  Use tab completion to view available environment types",
                    envStrings.first(), completor);
                deployInfo.setEnvType(envType);

                // loop through environments to get match against chosen environment type
                for (Environment env : envs) {
                    if (env.getType().equals(envType)) {
                        deployInfo.setHomeDir(env.getHomeDir());
                        deployInfo.setWorkDir(env.getWorkDir());
                        deployInfo.setFileDir(env.getFileDir());
                        deployInfo.setWebDir(env.getWebDir());
                        deployInfo.setDbName(env.getDbName());
                        deployInfo.setDbPassword(env.getDbPassword());
                        deployInfo.setDbUsername(env.getDbUsername());
                    }
                }

                // get versions
                File deployArchive = new File(System.getProperty("user.home") + "/.jcatapult/deploy-archive");
                final String projectName = deployInfo.getProjectName();
                File[] releaseFiles = null;
                if (deployArchive.exists() && deployArchive.isDirectory()) {
                     releaseFiles = deployArchive.listFiles(new FilenameFilter() {

                        public boolean accept(File dir, String filename) {
                            boolean accept = false;
                            if (filename.contains(projectName)) {
                                accept = true;
                            }
                            return accept;
                        }
                    });
                }

                // if no release versions exists then exit
                if (releaseFiles == null || releaseFiles.length == 0) {
                    System.out.println("No release versions exists within [" + deployArchive.getAbsolutePath() +
                        "] for project [" + projectName + "].  You must run a release prior to running the deployer.");
                    System.exit(1);
                }

                // set environment strings into completor
                TreeSet<String> versionStrings = new TreeSet<String>();
                for (File releaseFile : releaseFiles) {
                    versionStrings.add(releaseFile.getName());
                }
                completor.setCandidates(versionStrings);

                // get the env type
                String version = cliService.ask("Please select the version to deploy:", "deploy version set to: ",
                    "Invalid deploy version.  Use tab completion to view available versions",
                    versionStrings.first(), completor);
                deployInfo.setReleaseVersion(version);

                // deploy the resources
                deployerService.deploy(deployInfo);
            }
        }
    }
}
