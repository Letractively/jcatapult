/**
 * This Groovy scripts validates that the project version meets the following criteria:
 *
 * Major: X.0 where X > 0
 * Minor: X.Y where X and Y > 0
 * Patch: X.Y.Z where X, Z > 0 and Y >= 0
 * Snapshot: X.Y-Z where X > 0, Y >= 0, and Z is any character (usually Z = [A|B|RC|alpha|m|milestone][0-9]+)
 *
 * User: jhumphrey
 * Date: Oct 18, 2007
 */

import net.java.util.Version;

String versionString = properties["project.version"];
String type = properties["tmp_type"];
boolean valid = false;
try {
  Version version = new Version(versionString);
  if (type == "major") {
    valid = version.isMajorVersion();
  } else if (type == "minor") {
    valid = version.isMinorVersion();
  } else if (type == "patch") {
    valid = version.isPatchVersion();
  } else if (type == "snapshot") {
    valid = version.isSnapshot();
  } else {
    add.fail("Invalid type [${type}] passed to the validate_version macro");
  }
} catch (e) {
  System.out.println(e.getMessage());
}

if (!valid) {
  ant.fail("Project version [" + properties["project.version"] + "] is invalid or not a valid " +
          "${type} version number.\nPlease make sure that your version number meets the following criteria:\nMajor: X.0 where X > 0\nMinor: X.Y where X and Y > 0\nPatch: X.Y.Z where X, Z > 0 and Y >= 0\nSnapshot: X.Y-RCn, X.Y-Bn, X.Y-An where X, n > 0 and Y >= 0 (RC = Release Candidate, B = Beta, A = Alpha)");
}