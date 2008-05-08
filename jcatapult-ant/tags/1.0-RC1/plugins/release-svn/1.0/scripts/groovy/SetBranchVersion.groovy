/**
 * This groovy script extracts the major and minor version from the project.xml version
 * so that it can check out the correct branch from the project's branches directory.
 *
 * User: jhumphrey
 * Date: Oct 18, 2007
 */

import net.java.util.Version;
String versionString = properties["project.version"];
try {
  Version version = new Version(versionString);
  properties["branch.version"] = version.getMajor() + "." + version.getMinor();
} catch (e) {
  ant.fail("Project version [" + properties["project.version"] + "] is an invalid branch version. Please make " +
          "sure that it meets the criteria for a major (X.0 where X > 0), minor (X.Y where X, Y > 0), or snapshot (X.Y-RC1, X.Y-B1, X.Y-A1 where X, A > 0 and Y >= 0) version number");
}