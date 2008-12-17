/*
 * Copyright (c) 2001-2008, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.jcatapult.deployer;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jcatapult.deployer.domain.Deploy;
import org.jcatapult.deployer.domain.DeploymentInfo;
import org.jcatapult.deployer.domain.Project;
import org.jcatapult.deployer.guice.DeploymentModule;
import org.jcatapult.deployer.service.DeployXmlService;
import org.jcatapult.deployer.service.ProjectXmlService;
import org.jcatapult.deployer.service.ValidationService;
import org.jcatapult.deployer.service.XmlServiceException;
import org.jcatapult.deployer.utils.ArchiveUtils;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import groovy.lang.GroovyClassLoader;
import net.java.lang.StringTools;

/**
 * <p>Main entry point to the JCatapult Deployment framework.
 *
 * <p>Usage:</p>
 *
 * <pre>
 * DeploymentManager project-dir-path deploy-config-path
 * </pre>
 *
 * <p>The JCatapult distribution ships with a deployer installation that provides a deploy script for convenience.
 * The deploy script assumes that you are executing the deployer from the root of a jcatapult project:</p>
 *
 * <p>Please reference the <a href="http://code.google.com/p/jcatapult/wiki/GuideDeployer">Deployer Guide</a>
 * for more information on the JCatapult Deployer</p>
 *
 * @author jhumphrey
 */
public class DeploymentManager {

    /**
     * <p>The jcatapult cache directory.  This is set to System.getProperty("user.home")/.jcatapult</p>
     */
    public static final File JCATAPULT_CACHE_DIR = new File(System.getProperty("user.home"), ".jcatapult");

    /**
     * <p>The deploy root directory.  This is set to {@link DeploymentManager#JCATAPULT_CACHE_DIR}/deploy</p>
     */
    public static final File DEPLOY_ROOT_DIR = new File(JCATAPULT_CACHE_DIR, "deploy");

    /**
     * <p>The deploy archive directory.  This is set to {@link DeploymentManager#JCATAPULT_CACHE_DIR}/deploy-archive</p>
     */
    public static final File DEPLOY_ARCHIVE_DIR = new File(JCATAPULT_CACHE_DIR, "deploy-archive");

    private DeployXmlService deployXmlService;
    private ProjectXmlService projectXmlService;
    private CLIManager cliManager;
    private ValidationService<Deploy> deployValidationService;
    private ValidationService<Project> projectValidationService;
//    ValidationService<Deploy> deployValidationService = injector.getInstance(Key.get(new TypeLiteral<ValidationService<Deploy>>() {}));
//    ValidationService<Project> projectValidationService = injector.getInstance(Key.get(new TypeLiteral<ValidationService<Project>>() {}));

    /**
     * <p>Constructor.  All arguments are dependency injected by Guice</p>
     *
     * @param deployXmlService the {@link org.jcatapult.deployer.service.DeployXmlService} used for unmarshalling the deploy confguration xml file
     * @param projectXmlService the {@link org.jcatapult.deployer.service.ProjectXmlService} used for unmarshalling the project.xml
     * @param cliManager the {@link CLIManager} Manger for command line interpretation
     * @param deployValidationService the deploy {@link org.jcatapult.deployer.service.ValidationService} verifies that the information
     * unmarshalled into the Deploy domain bean is valid
     * @param projectValidationService the project {@link org.jcatapult.deployer.service.ValidationService} verifies that the information
     * unmarshalled into the Project domain bean is valid
     */
    @Inject
    public DeploymentManager(DeployXmlService deployXmlService, ProjectXmlService projectXmlService,
        CLIManager cliManager, ValidationService<Deploy> deployValidationService, ValidationService<Project> projectValidationService) {
        this.deployXmlService = deployXmlService;
        this.projectXmlService = projectXmlService;
        this.cliManager = cliManager;
        this.deployValidationService = deployValidationService;
        this.projectValidationService = projectValidationService;
    }

    /**
     * <p>Main point of entry into the deployment manager api</p>
     *
     * @param args arguments
     */
    public static void main(String[] args) {

        System.out.println("Executing JCatapult Deployment Manager");

        // do guice injection
        Injector injector = Guice.createInjector(new DeploymentModule());
        DeploymentManager dm = injector.getInstance(DeploymentManager.class);

        try {

            // check that deploy cache dir exists
            if (!(JCATAPULT_CACHE_DIR.exists() && JCATAPULT_CACHE_DIR.isDirectory())) {
                throw new DeploymentException("The JCatapult cache dir does not exist [" + JCATAPULT_CACHE_DIR.getAbsolutePath() + "]");
            }

            // check that deployer root dir exists
            if (!(DEPLOY_ROOT_DIR.exists() && DEPLOY_ROOT_DIR.isDirectory())) {
                throw new DeploymentException("The deployer root dir does not exist [" + DEPLOY_ROOT_DIR.getAbsolutePath() + "]");
            }

            // check that the deploy archive exist
            if (!(DEPLOY_ARCHIVE_DIR.exists() && DEPLOY_ARCHIVE_DIR.isDirectory())) {
                throw new DeploymentException("The deploy archive dir does not exist [" + DEPLOY_ARCHIVE_DIR.getAbsolutePath() +
                    "]. Please run a release prior to running the deployer");
            }

            dm.manage(args);
        } catch (DeploymentException e) {
            System.err.println("JCatapult Deplyer reference docs: http://code.google.com/p/jcatapult/wiki/GuideDeployer");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Helper method handle argument validation, project and deploy xml services for
     * unmarshalling, setting up the {@link DeploymentInfo} object and calling the deploy.groovy script deploy method
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

            // get the deployer properties
            Deploy props = unmarshallDeployXml(commands[1]);

            // query user for input
            DeploymentInfo deploymentInfo = cliManager.manage(props, project);

            // set the deploy props into the deployment info bean
            deploymentInfo.setDeploy(props);

            // validate that the deployer domain dir exists
            File deploymentDomainDir = new File(DEPLOY_ROOT_DIR, deploymentInfo.getDeployDomain());
            if (!(deploymentDomainDir.exists() && deploymentDomainDir.isDirectory())) {
                throw new DeploymentException("The deployer domain dir does not exist [" + deploymentDomainDir + "]");
            }

            // set dirs
            deploymentInfo.setDeploymentDomainDir(deploymentDomainDir);
            deploymentInfo.setJcatapultCacheDir(JCATAPULT_CACHE_DIR);
            deploymentInfo.setDeployArchiveDir(DEPLOY_ARCHIVE_DIR);

            deploymentInfo.setDeployArchiveVersion(ArchiveUtils.getVersionFromArchive(deploymentInfo.getDeployArchive()));

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
     * @return {@link org.jcatapult.deployer.domain.Project} bean
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

    /**
     * Helper method to unmarshall the deploy xml
     *
     * @param deployXmlPath the path to the deploy xml file
     * @return {@link org.jcatapult.deployer.domain.Deploy} bean
     */
    private Deploy unmarshallDeployXml(String deployXmlPath) {
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
        File deployerFile = new File(DEPLOY_ROOT_DIR, deployDomain + "/deployer.groovy");
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
