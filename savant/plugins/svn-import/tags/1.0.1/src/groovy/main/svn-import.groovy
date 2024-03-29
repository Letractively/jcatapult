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
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNRevision
import org.tmatesoft.svn.core.SVNPropertyValue
import org.tmatesoft.svn.core.wc.ISVNPropertyHandler

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
def url = reader.readLine() + "/" + projectName

println 'Enter the SubVersion username'
def username = reader.readLine()

println 'Enter the SubVersion password'
def password = reader.readLine()

def projectPath = projectDir.getAbsolutePath()
def confirm = ''
while (confirm != 'y' && confirm != 'n') {
  println "Really import [${projectPath}] to [${url}/trunk]? (y or n)"
  confirm = reader.readLine()
  if (confirm == 'n') {
    System.exit(0)
  }
}

println "Cleaning up the project (deleting target, web/WEB-INF/classes, web/WEB-INF/lib)"
def jcatDir = System.getProperty("user.home") + "/.jcatapult/project-svn-import"
FileTools.prune(jcatDir)
FileTools.prune("${projectPath}/target")
FileTools.prune("${projectPath}/web/WEB-INF/lib")
FileTools.prune("${projectPath}/web/WEB-INF/classes")
projectDir.eachFileMatch(~/.+\.iws/) {
  it.delete()
}

println "Importing [${projectPath}] to [${url}/trunk]..."

try {
  // Setup the SubVersion handlers
  DAVRepositoryFactory.setup()
  SVNRepositoryFactoryImpl.setup()
  FSRepositoryFactory.setup()

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

  // Move the project to the backup and check out the project
  if (projectDir.renameTo(new File(jcatDir))) {
    clientManager.getUpdateClient().doCheckout(trunk, projectDir, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false)
    clientManager.getWCClient().doSetProperty(projectDir, "svn:ignore", SVNPropertyValue.create("target\n*.iws"), true, SVNDepth.EMPTY, ISVNPropertyHandler.NULL, null)
  } else {
    println "Unable to move the existing project directory out of the way and checkout from SubVersion. You'll have to do that manually."
  }
} catch (SVNException e) {
  e.printStackTrace()
  throw new SavantBuildException("Failure while importing project", e)
}
