import org.inversoft.savant.dep.*
import org.inversoft.savant.context.*
import org.inversoft.savant.context.xml.DefaultProjectConfigBuilder

DefaultProjectConfigBuilder builder = new DefaultProjectConfigBuilder()
ProjectConfig projectConfig = builder.build(new File("project.xml"))

// Create a new deps set
ArtifactGroup artifactGroup = new ArtifactGroup("compile", Integer.MAX_VALUE)
artifactGroup.addArtifact(new Artifact("@{group}", "@{project}", "@{project}", "{latest}", "jar"))
Dependencies deps = new Dependencies("add-module-deps")
deps.addArtifactGroup(artifactGroup)

// Add it to the project
projectConfig.addDependencies(deps)

// Resolve the module
SavantContext context = new DefaultSavantContext(projectConfig, new File("."), new File("."))
DependencyResolveMediator mediator = new DependencyResolveMediator()
mediator.mediate(context, "add-module-deps", null, ["compile"] as Set, false)

// Get the artifact back out and grab the version and the file
Artifact artifact = artifactGroup.getArtifacts()[0]
properties["module.version"] = artifact.getVersion()
properties["module.file"] = context.getArtifactFile(artifact).getAbsolutePath()
