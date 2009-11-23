import org.inversoft.savant.dep.Artifact

// Check for the type
def type = arguments['type']
if (type == null || (type != 'webapp' && type != 'module' && type != 'library')) {
  println 'Please specify the type of project to create like this:'
  println ''
  println '  svnt jcatapult:make-project --type=library'
  println '  svnt jcatapult:make-project --type=webapp'
  println '  svnt jcatapult:make-project --type=module'
  System.exit(1)
}

Reader reader = new BufferedReader(new InputStreamReader(System.in))


// Read the input
def projectName = '_'
while (projectName.contains('_')) {
  println 'Enter the project name'
  projectName = reader.readLine()
  if (projectName.contains('_')) {
    println 'Project names can\'t contain underscores'
  }
}

println 'Enter the directory to create the project in (press enter to use the current directory)'
def projectDir = reader.readLine()
if (projectDir == '') {
  projectDir = new File('.').getAbsolutePath()
}

println 'Enter the package the project will use (i.e. com.example.foo)'
def packageName = reader.readLine()

println 'Enter the group name the project will use (i.e. example.com)'
def groupName = reader.readLine()


def confirm = ''
while (confirm != 'n' && confirm != 'y') {
  println "Really create the project ${groupName}:${projectName}? (y or n)"
  confirm = reader.readLine()
  if (confirm == 'n') {
    System.exit(0)
  }
}


// Determine the JCatapult version
Artifact jcat = pluginContext.getProjectConfig().getDefaultDependencies().getArtifactGroup("jcatapult").getArtifacts().iterator().next()
String version = jcat.getVersion()
int index = version.lastIndexOf("-IB")
if (index > 0) {
  version = version.substring(0, index) + "-{integration}"
}
println "Determined JCatapult version to be ${version}"


// Create the project
new File("${projectDir}/${projectName}").mkdirs()
new File("${projectDir}/${projectName}/src/java/main/${packageName.replace('.', '/')}/action").mkdirs()
new File("${projectDir}/${projectName}/src/java/main/${packageName.replace('.', '/')}/domain").mkdirs()
new File("${projectDir}/${projectName}/src/java/main/${packageName.replace('.', '/')}/service").mkdirs()
new File("${projectDir}/${projectName}/src/java/test/integration/${packageName.replace('.', '/')}").mkdirs()
new File("${projectDir}/${projectName}/src/java/test/unit/${packageName.replace('.', '/')}/action").mkdirs()
new File("${projectDir}/${projectName}/src/java/test/unit/${packageName.replace('.', '/')}/domain").mkdirs()
new File("${projectDir}/${projectName}/src/java/test/unit/${packageName.replace('.', '/')}/service").mkdirs()

def ant = new AntBuilder()
println "Copying project files"
ant.copy(todir: "${projectDir}/${projectName}") {
  fileset(dir: "${pluginContext.projectHome}/groovy/${type}-template", includes: "**/*", excludes: "${type}*")
  filterset() {
    filter(token: "GROUP_NAME", value: groupName)
    filter(token: "PACKAGE_NAME", value: packageName)
    filter(token: "PROJECT_NAME", value: projectName)
    filter(token: "UNDERSCORED_PROJECT_NAME", value: projectName.replace('-', '_').replace('.', '_'))
    filter(token: "JCATAPULT_VERSION", value: version)
  }
}
println "Copying IDE files"
ant.copy(tofile: "${projectDir}/${projectName}/${projectName}.iml", file: "${pluginContext.projectHome}/groovy/${type}-template/${type}-template.iml")
ant.copy(tofile: "${projectDir}/${projectName}/${projectName}.ipr", file: "${pluginContext.projectHome}/groovy/${type}-template/${type}-template.ipr")
ant.copy(tofile: "${projectDir}/${projectName}/${projectName}.eml", file: "${pluginContext.projectHome}/groovy/${type}-template/${type}-template.eml")

println "Fixing IntelliJ file names"
ant.replace(file: "${projectDir}/${projectName}/${projectName}.ipr", token: "${type}-template", value: projectName)
ant.replace(file: "${projectDir}/${projectName}/${projectName}.iml", token: "${type}-template", value: projectName)

String cmd
if (System.getProperty("os.name").contains("Windows")) {
  String savantHome = System.getProperty("savant.home")
  cmd = "${savantHome}\\bin\\svnt.bat"
} else {
  cmd = "svnt"
}

ant.exec(executable: cmd, dir: "${projectDir}/${projectName}") {
  arg(line: "ide")
}