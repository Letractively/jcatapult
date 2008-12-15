/**
 * Unfortunately, I want the transitive dependencies and there isn't an easy way yet from ant
 * to get them without writing some code. So, I create a path structure and then parse it to
 * create some XML that I can then transform.
 *
 * Parses the 'path' variable and creates an xml file from all the dependency paths in the following format:
 *
 * <files>
 *   <file path="PATH TO DEPENDENCY IN THE SAVANT REPOS" src="PATH TO SOURCE IN THE SAVANT REPOS"/>
 * </files>
 *
 * User: jhumphrey
 * Date: Oct 22, 2007
 */

import net.java.io.FileTools;

String[] libs = properties["path"].split(/,/);
StringBuffer buf = new StringBuffer();
buf.append("<files>");
for (String lib in libs) {
  lib = lib.replace("\\", "/");
  String home = System.getProperty("user.home").replace("\\", "/");
  String path = lib.replace(home + "/.savant/repository/", "");
  String src = path.replace(".jar", "-src.jar");
  int index = path.lastIndexOf("/");
  String name = path.substring(index + 1);

  buf.append("<file path=\"").append(path).append("\" src=\"").append(src).append("\"");
  buf.append(" name=\"").append(name).append("\"/>");
}
buf.append("</files>");

File file = new File(System.getProperty("java.io.tmpdir") + "/files.xml");
file.delete();
FileTools.write(file, buf.toString());
