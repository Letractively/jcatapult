import org.jcatapult.scaffolder.AbstractScaffolder
import org.jcatapult.scaffolder.annotation.LongDescription
import org.jcatapult.scaffolder.annotation.ShortDescription
import net.java.lang.StringTools

/**
* This class is a simple CRUD scaffolder for JCatapult.
*/
@ShortDescription("Creates an action class.")
@LongDescription(
"""Creates an action. This can be used in Modules and WebApps.

  This scaffolder asks the following questions:
    1. The URI of the action

  This scaffolder creates the action class, unti test and FTL result. This handles
  putting the FTL file in the correct location for Modules.
""")
public class ActionScaffolder extends AbstractScaffolder {
  public void execute() {
    String uri = ask("Enter the URI of the action", "", "Invalid URI", "");
    if (!uri.startsWith("/")) {
      println("URI must fully qualified rather than relative.");
      System.exit(1);
    }

    String pkgName = actionPackage(uri);
    String action = actionClass(uri);
    String actionTest = action + "Test";

    // Verify
    String actionClass = pkgName + "." + action;
    String actionTestClass = pkgName + "." + actionTest;
    String result = uri + ".ftl";

    String verify = null;
    while (verify != "yes") {
      verify = ask(
      """Please review the scaffolding configuration

    Action URI: ${uri}
    Action class: ${actionClass}
    Action test class: ${actionTestClass}
    Result: ${result}

    Is this is correct, type yes, if not type no""", "", "Invalid input", "yes");

      if (verify == "no") {
        println("Scaffolding cancelled");
        System.exit(1);
      } else if (verify != "yes") {
        println("Invalid response");
      }
    }

    executeTemplates(uri, pkgName, action, result);
  }

  private String actionPackage(String uri) {
    String base = getFirstPackage("action");
    int lastIndex = uri.lastIndexOf("/");
    return base + toJava(uri.substring(0, lastIndex));
  }

  private String actionClass(String uri) {
    int lastIndex = uri.lastIndexOf("/");
    String className = toJava(uri.substring(lastIndex + 1));
    return StringTools.capitalize(className);
  }

  private String toJava(String str) {
    StringBuilder build = new StringBuilder();
    char[] ca = str.toCharArray();
    boolean dash = false;
    ca.each { c ->
      if (dash) {
        build.append(Character.toUpperCase(c));
      } else {
        build.append(c);
      }

      dash = (c == '-');
    }

    return build.toString().replace("/", ".");
  }

  private void executeTemplates(String uri, String pkgName, String className, String result) {

    // Create the index action
    def params = [uri: uri, pkgName: pkgName, className: className];

    // Make the directory for all the actions
    String mainDirName = "src/java/main/" + pkgName.replace(".", "/") + "/";
    new File(mainDirName).mkdirs();
    String unitTestDirName = "src/java/test/unit/" + pkgName.replace(".", "/") + "/";
    new File(unitTestDirName).mkdirs();
    String integrationTestDirName = "src/java/test/integration/" + pkgName.replace(".", "/") + "/";
    new File(integrationTestDirName).mkdirs();

    // Create the actions
    executeFreemarkerTemplate("/action.ftl", mainDirName + className + ".java", params);
    executeFreemarkerTemplate("/unit-test.ftl", unitTestDirName + className + "Test.java", params);
    executeFreemarkerTemplate("/integration-test.ftl", integrationTestDirName + className + "IntegrationTest.java", params);

    // If this is a module, add the entity entry into module.xml
    String webDirName = "src/web/main";
    if (!new File(webDirName).isDirectory()) {
      webDirName = "web/WEB-INF/content";
    }

    new File(webDirName + result).getParentFile().mkdirs();
    executeFreemarkerTemplate("/ftl.ftl", webDirName + result, params);
  }
}
