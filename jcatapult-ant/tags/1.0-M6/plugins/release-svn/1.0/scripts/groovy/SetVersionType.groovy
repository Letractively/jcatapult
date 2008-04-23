/**
 * User: jhumphrey
 * Date: Oct 18, 2007
 */

import net.java.util.Version;

String versionString = properties["project.version"];
String type = "";
boolean valid = false;
Version version = new Version(versionString);
if (version.isMajorVersion()) {
  type = "major";
} else if (version.isMinorVersion()) {
  type = "minor";
} else if (version.isSnapshot()) {
  type = "snapshot";
} else if (version.isPatchVersion()) {
  type = "patch";
}

// if the type is empty string then the version is invalid
if (type.equals("")) {
  ant.fail("Project version [" + properties["project.version"] + "] is invalid or not a valid " +
          "${type} version number.\nPlease make sure that your version number meets the following criteria:\nMajor: X.0 where X > 0\nMinor: X.Y where X and Y > 0\nPatch: X.Y.Z where X, Z > 0 and Y >= 0\nSnapshot: X.Y-RCn, X.Y-Bn, X.Y-An where X, n > 0 and Y >= 0 (RC = Release Candidate, B = Beta, A = Alpha)");
} else {
  properties["version.type"] = type;
}