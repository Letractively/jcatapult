package org.jcatapult.deployment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.TreeSet;

import org.jcatapult.deployment.domain.Deploy;
import org.jcatapult.deployment.domain.DeploymentInfo;
import org.jcatapult.deployment.domain.DeploymentProperties;
import org.jcatapult.deployment.domain.Environment;
import org.jcatapult.deployment.domain.Project;
import org.jcatapult.deployment.service.BetterSimpleCompletor;
import org.jcatapult.deployment.service.CLIService;
import org.jcatapult.deployment.service.ProjectXmlService;
import org.jcatapult.deployment.service.ValidationService;
import org.jcatapult.deployment.service.XmlServiceException;

import com.google.inject.Inject;
import net.java.lang.StringTools;

/**
 * User: jhumphrey
 * Date: May 15, 2008
 */
public class CLIManager {

    private BetterSimpleCompletor completor;
    private CLIService cliService;
    private ProjectXmlService projectXmlService;
    ValidationService<Project> validationService;

    @Inject
    public CLIManager(BetterSimpleCompletor completor, CLIService cliService, ProjectXmlService projectXmlService,
        ValidationService<Project> validationService) {
        this.completor = completor;
        this.cliService = cliService;
        this.projectXmlService = projectXmlService;
        this.validationService = validationService;
    }

    public DeploymentInfo manage(DeploymentProperties props, Project project) {

        // deployment info stores all the data necessary to process the deployment
        DeploymentInfo deploymentInfo = new DeploymentInfo();

        // get deploy and set deploy domain into the deployment info
        Deploy deploy = getDeploy(props.getDeploys());
        deploymentInfo.setDeployDomain(deploy.getDomain());


        // get the deploy environment and set it into the deployment info
        Environment env = getDeployEnv(deploy.getEnvs());
        deploymentInfo.setEnv(env);

        // set the project into the deployment info
        deploymentInfo.setProject(project);

        // get the version to be deployed and set it into the deployment info
        String deployVersion = getDeployVersion(project.getName());
        deploymentInfo.setDeployVersion(deployVersion);

        return deploymentInfo;
    }

    private String getDeployVersion(final String projectName) {

        // load from deploy archive
        File deployArchive = new File(System.getProperty("user.home") + "/.jcatapult/deploy-archive");
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

        // set version strings into completor
        TreeSet<String> versionStrings = new TreeSet<String>();
        for (File releaseFile : releaseFiles) {
            versionStrings.add(releaseFile.getName());
        }
        completor.setCandidates(versionStrings);

        return cliService.ask("Please select the version to deploy:", "deploy version set to: ",
            "Invalid deploy version.  Use tab to view available versions",
            versionStrings.first(), completor);
    }

    /**
     * Gets deploy domain input from the CLI if there's more than one deploy domain descriptor defined in the deploy.xml
     *
     * @param deploys the list of {@link org.jcatapult.deployment.domain.Deploy} objects
     * @return the {@link org.jcatapult.deployment.domain.Deploy} object
     */
    private Deploy getDeploy(List<Deploy> deploys) {

        String domain = deploys.get(0).getDomain();

        // this block populates a tree set to be used for tab completion in the cli service.
        // this only gets interpreted if there are more than one deploy descriptor defined
        String msgFrag = "Remote deploying to ";
        if (deploys.size() > 1) {
            TreeSet<String> domains = new TreeSet<String>();
            for (Deploy deploy : deploys) {
                domains.add(deploy.getDomain());
            }

            completor.setCandidates(domains);

            domain = cliService.ask("Please select the domain you are deploying to:",
                msgFrag, "Invalid domain.  Use tab to view available domains",
                domains.first(), completor);
        } else {
            System.out.println(msgFrag + domain);
        }

        Deploy deploy = null;
        for (Deploy d : deploys) {
            if (d.getDomain().equals(domain)) {
                deploy = d;
            }
        }

        return deploy;
    }

    /**
     * Gets environment input from the CLI if there's more than one environment descriptor defined for the selected deploy domain
     *
     * @param envs list of {@link org.jcatapult.deployment.domain.Environment} objects associated to the selected deploy domain
     * @return {@link org.jcatapult.deployment.domain.Environment} object
     */
    private Environment getDeployEnv(List<Environment> envs) {

        String envType = envs.get(0).getType();

        // this block populates a tree set to be used for tab completion in the cli service.
        // this only gets interpreted if there are more than one environment descriptor defined
        String msgFrag = "Remote deploying to environment ";
        if (envs.size() > 1) {
            TreeSet<String> envTypes = new TreeSet<String>();
            for (Environment env : envs) {
                envTypes.add(env.getType());
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
            if (e.getType().equals(envType)) {
                env = e;
            }
        }

        return env;
    }
}
