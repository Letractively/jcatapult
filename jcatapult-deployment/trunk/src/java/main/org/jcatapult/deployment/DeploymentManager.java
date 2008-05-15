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
import org.jcatapult.deployment.service.DeployerService;
import org.jcatapult.deployment.service.XmlServiceException;
import org.jcatapult.deployment.service.DeployXmlService;
import org.jcatapult.deployment.service.ProjectXmlService;
import org.jcatapult.deployment.service.ValidationService;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.java.lang.StringTools;

/**
 * User: jhumphrey
 * Date: Mar 25, 2008
 */
public class DeploymentManager {

    Injector injector = Guice.createInjector(new DeploymentModule());
    DeployXmlService deployXmlService = injector.getInstance(DeployXmlService.class);
    ProjectXmlService projectXmlService = injector.getInstance(ProjectXmlService.class);
    CLIManager cliManager = injector.getInstance(CLIManager.class);
    DeployerService deployerService = injector.getInstance(DeployerService.class);
    ValidationService<DeploymentProperties> deployValidationService = injector.getInstance(Key.get(new TypeLiteral<ValidationService<DeploymentProperties>>() {}));
    ValidationService<Project> projectValidationService = injector.getInstance(Key.get(new TypeLiteral<ValidationService<Project>>() {}));


    /**
     * Main point of entry into the deployment manager api
     *
     * @param args arguments
     */
    public static void main(String[] args) {
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

                // deploy the resources
                deployerService.deploy(deploymentInfo);
            }
        }
    }
}
