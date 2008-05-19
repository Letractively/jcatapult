package org.jcatapult.deployment;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jcatapult.deployment.domain.DeploymentInfo;
import org.jcatapult.deployment.domain.Deploy;
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
 * <p>Main entry point to the JCatapult Deployment framework.
 *
 * <p>Usage:</p>
 *
 * DeploymentManager project-dir-path deploy-config-path
 *
 * <p>The JCatapult distribution ships with a deployer installation that provides a deploy script for convenience.
 * The deploy script assumes that you are executing the deployer from the root of a jcatapult project:</p>
 *
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeploymentManager {

    Injector injector = Guice.createInjector(new DeploymentModule());
    DeployXmlService deployXmlService = injector.getInstance(DeployXmlService.class);
    ProjectXmlService projectXmlService = injector.getInstance(ProjectXmlService.class);
    CLIManager cliManager = injector.getInstance(CLIManager.class);
    ValidationService<Deploy> deployValidationService = injector.getInstance(Key.get(new TypeLiteral<ValidationService<Deploy>>() {
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

        // check that deploy cache dir exists
        if (!(JCATAPULT_CACHE_DIR.exists() && JCATAPULT_CACHE_DIR.isDirectory())) {
            System.err.println("The JCatapult cache dir does not exist [" + JCATAPULT_CACHE_DIR.getAbsolutePath() + "]");
            System.exit(1);
        }

        // check that deployment root dir exists
        if (!(DEPLOYMENT_ROOT_DIR.exists() && DEPLOYMENT_ROOT_DIR.isDirectory())) {
            System.err.println("The deployment root dir does not exist [" + DEPLOYMENT_ROOT_DIR.getAbsolutePath() + "]");
            System.exit(1);
        }

        // check that the deploy archive exist
        if (!(DEPLOY_ARCHIVE_DIR.exists() && DEPLOY_ARCHIVE_DIR.isDirectory())) {
            System.err.println("The deploy archive dir does not exist [" + DEPLOY_ARCHIVE_DIR.getAbsolutePath() +
                "]. Please run a release prior to running the deployer");
            System.exit(1);
        }

        try {
            new DeploymentManager().manage(args);
        } catch (DeploymentException e) {
            System.err.println(e.getMessage());
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
        if (commands.length == 0 || (commands[0].equals("help") || commands[0].equals("usage")) || commands.length > 2) {
            System.out.println(getHelpMsg());
        } else if (commands.length == 2) {

            // Create a project bean and store the name of the project and the version that is getting deployed into it
            Project project = unmarshallProjectXml(commands[0]);

            // get the deployment properties
            Deploy props = unmarshallDeploymentsProps(commands[1]);

            // query user for input
            DeploymentInfo deploymentInfo = cliManager.manage(props, project);

            // validate that the deployment domain dir exists
            File deploymentDomainDir = new File(DEPLOYMENT_ROOT_DIR, deploymentInfo.getDeployDomain());
            if (!(deploymentDomainDir.exists() && deploymentDomainDir.isDirectory())) {
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

    /**
     * Helper method to build the help msg
     *
     * @return the help msg
     */
    private String getHelpMsg() {
        return "\nDeployer Usage: DeploymentManager [help|usage] <project dir path> <deploy xml path>\n\n" +
            "If you are running the deployer from the jcatapult installation then execute the following command from the shell:\n\n" +
            "Windows:\n" +
            "%JCATAPULT_HOME%\\deployer\\bin\\deploy.bat [help|usage]\n\n" +
            "Linux/Unix/Mac:\n" +
            "$JCATAPULT_HOME/deployer/bin/deploy [help|usage]";
    }

    /**
     * Helper method for unmarshalling the project.xml file
     *
     * @param projectDirPath the path to the project
     * @return {@link org.jcatapult.deployment.domain.Project} bean
     */
    private Project unmarshallProjectXml(String projectDirPath) {
        Project project;
        try {
            File projectDir = new File(projectDirPath).getAbsoluteFile();
            if (!(projectDir.exists() && projectDir.isDirectory())) {
                throw new DeploymentException("The project directory path does not exist [" + projectDir.getAbsolutePath() + "]");
            }
            File projectXml = new File(projectDir, "project.xml");
            if (!(projectXml.exists() && projectXml.isFile())) {
                throw new DeploymentException("Could not locate project.xml file located at [" + projectXml.getAbsolutePath() + "]");
            }
            project = projectXmlService.unmarshall(projectXml);
        } catch (XmlServiceException e) {
            throw new DeploymentException(e);
        }

        // validate project bean
        String projectErrors = projectValidationService.validate(project);
        if (!StringTools.isEmpty(projectErrors)) {
            throw new DeploymentException(projectErrors);
        }

        return project;
    }

    private Deploy unmarshallDeploymentsProps(String deployXmlPath) {
        Deploy props;
        try {
            File deployXmlFile = new File(deployXmlPath);
            if (!(deployXmlFile.exists() && deployXmlFile.isFile())) {
                throw new DeploymentException("The deploy xml configuration file does not yet exist at the path specified [" +
                    deployXmlFile.getAbsolutePath() + "]");
            }
            props = deployXmlService.unmarshall(deployXmlFile);
        } catch (XmlServiceException e) {
            throw new DeploymentException(e);
        }

        // validate the deploy props.  throw runtime exception if there are validation errors
        String deployErrors = deployValidationService.validate(props);
        if (!StringTools.isEmpty(deployErrors)) {
            throw new DeploymentException(deployErrors);
        }

        return props;
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
