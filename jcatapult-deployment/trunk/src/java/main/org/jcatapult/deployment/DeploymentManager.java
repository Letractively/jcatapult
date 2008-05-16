package org.jcatapult.deployment;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jcatapult.deployment.domain.DeploymentInfo;
import org.jcatapult.deployment.domain.DeploymentProperties;
import org.jcatapult.deployment.domain.Project;
import org.jcatapult.deployment.guice.DeploymentModule;
import org.jcatapult.deployment.service.DeployXmlService;
import org.jcatapult.deployment.service.ProjectXmlService;
import org.jcatapult.deployment.service.ValidationService;
import org.jcatapult.deployment.service.XmlServiceException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import groovy.lang.GroovyClassLoader;
import net.java.lang.StringTools;

/**
 * <p>Main entry point to the JCatapult Deployment framework.  The main method must be provided
 * 1 argument, which is the location of the deploy.xml file.
 *
 * The JCatapult distribution ships with a deployer installation that provides a deploy script for convenience.
 * The deploy script assumes that your deploy.xml file is located in the following project context path:</p>
 *
 * <p>/deploy/remote/deploy.xml</p>
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeploymentManager {

    Injector injector = Guice.createInjector(new DeploymentModule());
    DeployXmlService deployXmlService = injector.getInstance(DeployXmlService.class);
    ProjectXmlService projectXmlService = injector.getInstance(ProjectXmlService.class);
    CLIManager cliManager = injector.getInstance(CLIManager.class);
    ValidationService<DeploymentProperties> deployValidationService = injector.getInstance(Key.get(new TypeLiteral<ValidationService<DeploymentProperties>>() {
    }));
    ValidationService<Project> projectValidationService = injector.getInstance(Key.get(new TypeLiteral<ValidationService<Project>>() {
    }));

    public static final File JCATAPULT_CACHE_DIR = new File(System.getProperty("user.home"), ".jcatapult");
    public static final File DEPLOYMENT_ROOT_DIR = new File(JCATAPULT_CACHE_DIR, "deployment");
    public static final File DEPLOY_ARCHIVE_DIR = new File(JCATAPULT_CACHE_DIR, "deploy-archive");

    /**
     * Main point of entry into the deployment manager api
     *
     * @param args arguments
     */
    public static void main(String[] args) {

        System.out.println("Executing JCatapult Deployment Manager");

        if (! (JCATAPULT_CACHE_DIR.exists() && JCATAPULT_CACHE_DIR.isDirectory()) ) {
            System.err.println("The JCatapult cache dir does not exist [" + JCATAPULT_CACHE_DIR.getAbsolutePath() + "]");
            System.exit(1);
        }

        if (! (DEPLOYMENT_ROOT_DIR.exists() && DEPLOYMENT_ROOT_DIR.isDirectory()) ) {
            System.err.println("The deployment root dir does not exist [" + DEPLOYMENT_ROOT_DIR.getAbsolutePath() + "]");
            System.exit(1);
        }

        if (! (DEPLOY_ARCHIVE_DIR.exists() && DEPLOY_ARCHIVE_DIR.isDirectory()) ) {
            System.err.println("The deploy archive dir does not exist [" + DEPLOY_ARCHIVE_DIR.getAbsolutePath() +
                "]. Please run a release prior to running the deployer");
            System.exit(1);
        }

        try {
            new DeploymentManager().manage(args);
        } catch (DeploymentException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Manages the deployment process
     *
     * @param args args from main method
     * @throws DeploymentException Runtime exception thrown if there's an issue during deployment.
     */
    private void manage(String... args) throws DeploymentException {
        String helpMsg = "Usage: DeploymentManager <deploy xml file path>";

        // get the file path of the local project deploy xml file
        File deployXmlFile = new File(args[0]);

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
                // get the deployment properties
                DeploymentProperties props;
                try {
                    props = deployXmlService.unmarshall(deployXmlFile);
                } catch (XmlServiceException e) {
                    throw new DeploymentException(e);
                }

                // validate the deploy props.  throw runtime exception if there are validation errors
                String deployErrors = deployValidationService.validate(props);
                if (!StringTools.isEmpty(deployErrors)) {
                    throw new DeploymentException(deployErrors);
                }

                // Create a project bean and store the name of the project and the version that is getting deployed into it
                Project project;
                try {
                    project = projectXmlService.unmarshall(new File("project.xml").getAbsoluteFile());
                } catch (XmlServiceException e) {
                    throw new DeploymentException(e);
                }

                // validate project bean
                String projectErrors = projectValidationService.validate(project);
                if (!StringTools.isEmpty(projectErrors)) {
                    throw new DeploymentException(projectErrors);
                }

                // query user for input
                DeploymentInfo deploymentInfo = cliManager.manage(props, project);

                // validate that the deployment domain dir exists
                File deploymentDomainDir = new File(DEPLOYMENT_ROOT_DIR, deploymentInfo.getDeployDomain());
                if (! (deploymentDomainDir.exists() && deploymentDomainDir.isDirectory()) ) {
                    throw new DeploymentException("The deployment domain dir does not exist [" + deploymentDomainDir + "]");
                }

                // set dirs
                deploymentInfo.setDeploymentDomainDir(deploymentDomainDir);
                deploymentInfo.setJcatapultCacheDir(JCATAPULT_CACHE_DIR);
                deploymentInfo.setDeployArchiveDir(DEPLOY_ARCHIVE_DIR);


                // load the deploy
                Deployer deployer = loadDeployer(deploymentInfo.getDeployDomain());
                deployer.deploy(deploymentInfo);
            }
        }
    }

    /**
     * Helper method to load the deployer.groovy file from disk
     *
     * @param deployDomain the domain.  this maps directly to the directory within .jcatapult/deployment
     * @return {@link Deployer} object
     */
    private Deployer loadDeployer(String deployDomain) {
        File deployerFile = new File(DEPLOYMENT_ROOT_DIR, deployDomain + "/deployer.groovy");
        if (!deployerFile.exists()) {
            throw new DeploymentException(deployerFile.getAbsolutePath() + " does not exist");
        }

        GroovyClassLoader gcl = new GroovyClassLoader();

        try {
            Class clazz = gcl.parseClass(deployerFile);
            return (Deployer) clazz.newInstance();
        } catch (Exception e) {
            throw new DeploymentException("Unable to execute deployer", e);
        }
    }


}
