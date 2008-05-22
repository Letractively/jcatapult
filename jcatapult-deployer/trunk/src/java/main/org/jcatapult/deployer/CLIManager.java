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
import java.io.FilenameFilter;
import java.util.List;
import java.util.TreeSet;

import static org.jcatapult.deployer.DeploymentManager.DEPLOY_ARCHIVE_DIR;
import org.jcatapult.deployer.domain.Deploy;
import org.jcatapult.deployer.domain.DeploymentInfo;
import org.jcatapult.deployer.domain.Domain;
import org.jcatapult.deployer.domain.Environment;
import org.jcatapult.deployer.domain.Project;
import org.jcatapult.deployer.service.BetterSimpleCompletor;
import org.jcatapult.deployer.service.CLIService;
import org.jcatapult.deployer.service.ValidationService;

import com.google.inject.Inject;

/**
 * <p>Manages command line interpretation</p>
 *
 * @author jhumphrey
 */
public class CLIManager {

    private BetterSimpleCompletor completor;
    private CLIService cliService;
    ValidationService<Project> validationService;

    @Inject
    public CLIManager(BetterSimpleCompletor completor, CLIService cliService, ValidationService<Project> validationService) {
        this.completor = completor;
        this.cliService = cliService;
        this.validationService = validationService;
    }

    /**
     * <p>Manages collecting data from user input to populate the
     * {@link org.jcatapult.deployer.domain.DeploymentInfo} bean</p>
     *
     * @param props the {@link org.jcatapult.deployer.domain.Deploy} bean
     * @param project the {@link org.jcatapult.deployer.domain.Project} bean
     * @return returns a {@link org.jcatapult.deployer.domain.DeploymentInfo} bean, which contains the information
     * for performing artifact deployment
     */
    public DeploymentInfo manage(Deploy props, Project project) {

        // deployer info stores all the data necessary to process the deployer
        DeploymentInfo deploymentInfo = new DeploymentInfo();

        // get deploy and set deploy domain into the deployer info
        Domain domain = getDomain(props.getDomains());
        deploymentInfo.setDeployDomain(domain.getName());

        // get the deploy environment and set it into the deployer info
        Environment env = getDeployEnv(domain.getEnvs());
        deploymentInfo.setEnv(env);

        // set the project into the deployer info
        deploymentInfo.setProject(project);

        // get the jar to be deployed
        String deployArchive = getDeployArchive(project.getName());
        deploymentInfo.setDeployArchive(deployArchive);

        return deploymentInfo;
    }

    /**
     * queries the user to provide the jar to deploy
     *
     * @param projectName the project name
     * @return returns the jar to deploy
     */
    private String getDeployArchive(final String projectName) {

        // load from deploy archive
        File[] releaseFiles;
        releaseFiles = DEPLOY_ARCHIVE_DIR.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String filename) {
                boolean accept = false;
                if (filename.contains(projectName)) {
                    accept = true;
                }
                return accept;
            }
        });

        // if no release versions exists then exit
        if (releaseFiles == null || releaseFiles.length == 0) {
            throw new DeploymentException("No release archives exists within [" + DEPLOY_ARCHIVE_DIR.getAbsolutePath() +
                "] for project [" + projectName + "].  Please create a deploy archive prior executing the deployer.  For " +
                "more information on creating deployable archives, please reference the JCatapult Deployer ducmentation " +
                "at http://code.google.com/p/jcatapult/wiki/GuideDeployer");
        }

        // set version strings into completor
        TreeSet<String> versionStrings = new TreeSet<String>();
        for (File releaseFile : releaseFiles) {
            versionStrings.add(releaseFile.getName());
        }
        completor.setCandidates(versionStrings);

        return cliService.ask("Please select the archive to deploy:", "Deploy archive set to: ",
            "Invalid archive.  Use tab to view available archives",
            versionStrings.first(), completor);
    }

    /**
     * Gets deploy domain input from the CLI if there's more than one deploy domain descriptor defined in the deploy.xml
     *
     * @param domains the list of {@link org.jcatapult.deployer.domain.Domain} objects
     * @return the {@link org.jcatapult.deployer.domain.Domain} object
     */
    private Domain getDomain(List<Domain> domains) {

        String domainString = domains.get(0).getName();

        // this block populates a tree set to be used for tab completion in the cli service.
        // this only gets interpreted if there are more than one deploy descriptor defined
        String msgFrag = "Deploying to domain: ";
        if (domains.size() > 1) {
            TreeSet<String> domainNames = new TreeSet<String>();
            for (Domain domain : domains) {
                domainNames.add(domain.getName());
            }

            completor.setCandidates(domainNames);

            domainString = cliService.ask("Please select the domain you are deploying to:",
                msgFrag, "Invalid domain.  Use tab to view available domains",
                domainNames.first(), completor);
        } else {
            System.out.println(msgFrag + domainString);
        }

        Domain domain = null;
        for (Domain d : domains) {
            if (d.getName().equals(domainString)) {
                domain = d;
            }
        }

        return domain;
    }

    /**
     * Gets environment input from the CLI if there's more than one environment descriptor defined for the selected deploy domain
     *
     * @param envs list of {@link org.jcatapult.deployer.domain.Environment} objects associated to the selected deploy domain
     * @return {@link org.jcatapult.deployer.domain.Environment} object
     */
    private Environment getDeployEnv(List<Environment> envs) {

        String envType = envs.get(0).getName();

        // this block populates a tree set to be used for tab completion in the cli service.
        // this only gets interpreted if there are more than one environment descriptor defined
        String msgFrag = "Deploying to environment: ";
        if (envs.size() > 1) {
            TreeSet<String> envTypes = new TreeSet<String>();
            for (Environment env : envs) {
                envTypes.add(env.getName());
            }

            completor.setCandidates(envTypes);

            envType = cliService.ask("Please select the environment you are deploying to:",
                msgFrag, "Invalid environment.  Use tab to view available environments",
                envTypes.first(), completor);
        } else {
            System.out.println(msgFrag + envType);
        }

        Environment env = null;
        for (Environment e : envs) {
            if (e.getName().equals(envType)) {
                env = e;
            }
        }

        return env;
    }
}
