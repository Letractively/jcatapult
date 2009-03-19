import net.java.io.FileTools
import org.inversoft.savant.SavantBuildException
import org.tmatesoft.svn.core.SVNDepth
import org.tmatesoft.svn.core.SVNException
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions
import org.tmatesoft.svn.core.io.SVNRepository
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNCommitClient
import org.tmatesoft.svn.core.wc.SVNWCUtil

println """svn-import is an interactive tool to help you import a local project directory into a remote subversion
repository. Please ensure that you are NOT in the project directory currently. If you are in the
project directory this command might fail (on Windows) or you might think you are in the checked
directory once the command exits, but you won't be.


You will be prompted for the following information:

    1. The project name as it exists on disk and will exist in the SVN repository.
    2. The URL to where your project directory will be imported to without the project name.
           ex: https://svn.someuser.com/myprojects
    3. The subversion repository username
    4. The subversion repository password"""

Reader reader = new InputStreamReader(System.in)

println 'Enter the project name'
def projectName = reader.readLine()

def projectDir = new File(projectName)
if (!projectDir.isDirectory()) {
  println "${projectDir.getAbsolutePath()} doesn't exist!"
  println "You must be in the directory just above the project directory."
  System.exit(0)
}

println 'Enter the SubVersion URL (excluding the project name)'
def url = reader.readLine()

println 'Enter the SubVersion username'
def username = reader.readLine()

println 'Enter the SubVersion password'
def password = reader.readLine()

def projectPath = projectDir.getAbsolutePath()
def confirm = ''
while (confirm != 'y' && confirm != 'n') {
  println "Really import [${projectPath}] to [${url}/${projectName}]? (y or n)"
  confirm = reader.readLine()
  if (confirm == 'n') {
    System.exit(0)
  }
}

def jcatDir = System.getProperty("user.home") + "/.jcatapult"
FileTools.prune("${jcatDir}/project-svn-import")
FileTools.prune("${projectPath}/target")
FileTools.prune("${projectPath}/web/WEB-INF/lib")
FileTools.prune("${projectPath}/web/WEB-INF/classes")
projectDir.eachFileMatch(/.+\.iws/) {
  this.delete()
}

println "Importing [${projectPath}] to [${url}/${projectName}/trunk]..."

try {
  SVNURL svnURL = SVNURL.parseURIEncoded(url)
  DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true)
  SVNClientManager clientManager = SVNClientManager.newInstance(options, username, password)
  SVNRepository repository = clientManager.createRepository(svnURL, false)
  repository.checkPath("", -1)

  SVNURL trunk = svnURL.appendPath("trunk", false)
  SVNCommitClient client = clientManager.getCommitClient()
  client.doImport(projectDir, trunk, "Project import", null, true, true, SVNDepth.INFINITY)

  SVNURL branches = svnURL.appendPath("branches", false)
  SVNURL tags = svnURL.appendPath("tags", false)
  client.doMkDir([branches, tags] as SVNURL[], "Branches and tags")
} catch (SVNException e) {
  throw new SavantBuildException("Failure while importing project", e);
}
