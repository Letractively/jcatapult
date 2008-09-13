import org.jcatapult.scaffolder.AbstractScaffolder
import org.jcatapult.scaffolder.URIPrefixCompletor
import org.jcatapult.scaffolder.annotation.LongDescription
import org.jcatapult.scaffolder.annotation.ShortDescription
import org.jcatapult.scaffolder.lang.Type
import org.jcatapult.scaffolder.URIPrefixCompletor

/**
 * This class is a simple CRUD scaffolder for JCatapult.
 */
@ShortDescription("Creates a CRUD operation for a single domain object")
@LongDescription(
"""Creates a CRUD operation for a single domain object. If the project that the scaffolder
is being run on is a webapp, it creates JSPs for the view. If the project is a module,
FreeMarker templates are created.

  This scaffolder asks the following questions:
    1. The domain class to create the CRUD for (tab completion available)
    2. The URI for the CRUD (tab completion available)
    3. The action package (tab completion available)
    4. The service package (tab completion available)

  This scaffolder creates all of a service, the actions, JSPs/FTLs, and action tests for the
  CRUD operation. It handles basic JPA domain modelling, but doesn't handle complex
  relationships such as enumeration tables and such.
""")
public class CrudScaffolder extends AbstractScaffolder {
  public void execute() {
    // Ask the user what the bean they want to scaffold is
    String className = ask("Enter the domain class to CRUD scaffold", "Scaffolding domain object ",
        "Invalid domain class", null);

    // Setup some useful names
    String simpleClassName = makeSimpleClassName(className);

    // Try to get the AST
    Type type = getType(className);
    if (type == null) {
      println "Invalid domain class [" + className + "]";
      System.exit 1;
    }

    // Get the URI
    String defaultURI = "/admin/" + makeURI(simpleClassName);
    String uri = ask("Enter the URI of the CRUD", "Scaffolding URI prefix ",
        "Invalid URI prefix", defaultURI, new URIPrefixCompletor());

    // Get the action package
    String defaultActionPackage = getFirstPackage("action");
    if (defaultActionPackage != null) {
      defaultActionPackage = defaultActionPackage + makePackageName(uri.replace("/", "."));
    }
    String actionPackage = ask("Enter the action package",
        "Scaffolding action package ", "Invalid action package", defaultActionPackage);

    // Get the service package
    String defaultServicePackage = getFirstPackage("service");
    String servicePackage = ask("Enter the service package",
        "Scaffolding service package ", "Invalid service package", defaultServicePackage);

    // Verify
    String verify = null;
    while (verify != "yes") {
      verify = ask(
      """Please review the scaffolding configuration

    Type: crud
    Domain class: ${className}
    URI: ${uri}
    Action package: ${actionPackage}
    Service package: ${servicePackage}

    Is this is correct, type yes, if not type no""", "", "Invalid input", "yes");

      if (verify == "no") {
        println("Scaffolding cancelled");
        System.exit(1);
      } else if (verify != "yes") {
        println("Invalid response");
      }
    }

    executeTemplates(simpleClassName, servicePackage, actionPackage, uri, type);
  }

  private void executeTemplates(String simpleClassName, String servicePackage, String actionPackage,
                                String uri, Type type) {
    boolean module = !new File("web").exists();

    // Create the index action
    def params = [actionPackage: actionPackage, servicePackage: servicePackage, uri: uri, type: type,
            module: module];

    // Make the directory for all the actions
    String actionDirName = "src/java/main/" + actionPackage.replace(".", "/") + "/";
    File actionsDir = new File(actionDirName);
    actionsDir.mkdirs();

    // Create the actions
    executeFreemarkerTemplate("/actions/add.ftl", actionDirName + "Add.java", params);
    executeFreemarkerTemplate("/actions/delete.ftl", actionDirName + "Delete.java", params);
    executeFreemarkerTemplate("/actions/edit.ftl", actionDirName + "Edit.java", params);
    executeFreemarkerTemplate("/actions/index.ftl", actionDirName + "Index.java", params);
    executeFreemarkerTemplate("/actions/package-info.ftl", actionDirName + "package-info.java", params);
    executeFreemarkerTemplate("/actions/prepare.ftl", actionDirName + "Prepare.java", params);

    // Make the directory for all the actions
    String messageDirName = "web/WEB-INF/message" + uri + "/";
    File messageDir = new File(messageDirName);
    messageDir.mkdirs();

    // Create the error and message bundles
    executeFreemarkerTemplate("/messages/package.ftl", messageDirName + "package.properties", params);

    // Make the directory for the service
    String serviceDirName = "src/java/main/" + servicePackage.replace(".", "/") + "/";
    File serviceDir = new File(serviceDirName);
    serviceDir.mkdirs();

    // Create the service interface and implementation
    executeFreemarkerTemplate("/service/service.ftl", serviceDirName + simpleClassName + "Service.java", params);
    executeFreemarkerTemplate("/service/service-impl.ftl", serviceDirName + simpleClassName + "ServiceImpl.java", params);

    // Create the JSPs or FreeMarker templates
    // Make the directory for the FTLs
    String webDirName = "web/WEB-INF/content" + uri + "/";
    File webDir = new File(webDirName);
    webDir.mkdirs();

    executeFreemarkerTemplate("/ftls/add.ftl", webDirName + "add.ftl", params);
    executeFreemarkerTemplate("/ftls/edit.ftl", webDirName + "edit.ftl", params);
    executeFreemarkerTemplate("/ftls/index.ftl", webDirName + "index.ftl", params);
    executeFreemarkerTemplate("/ftls/form.ftl", webDirName + "form.ftl", params);

    // Make the directory for the action unit tests
    String actionTestDirName = "src/java/test/unit/" + actionPackage.replace(".", "/") + "/";
    File actionTestsDir = new File(actionTestDirName);
    actionTestsDir.mkdirs();

    // Create the action unit tests
    executeFreemarkerTemplate("/unit-tests/add.ftl", actionTestDirName + "AddTest.java", params);
    executeFreemarkerTemplate("/unit-tests/delete.ftl", actionTestDirName + "DeleteTest.java", params);
    executeFreemarkerTemplate("/unit-tests/edit.ftl", actionTestDirName + "EditTest.java", params);
    executeFreemarkerTemplate("/unit-tests/index.ftl", actionTestDirName + "IndexTest.java", params);

    // Make the directory for the service unit tests
    String serviceTestDirName = "src/java/test/unit/" + servicePackage.replace(".", "/") + "/";
    File serviceTestDir = new File(serviceTestDirName);
    serviceTestDir.mkdirs();

    // Create the service unit tests
    executeFreemarkerTemplate("/unit-tests/service.ftl", serviceTestDirName + simpleClassName + "ServiceImplTest.java", params);

    // Make the directory for the action integration tests
    String actionIntegrationTestDirName = "src/java/test/integration/" + actionPackage.replace(".", "/") + "/";
    File actionIntegrationTestsDir = new File(actionIntegrationTestDirName);
    actionIntegrationTestsDir.mkdirs();

    // Create the action integration tests
    executeFreemarkerTemplate("/integration-tests/add.ftl", actionIntegrationTestDirName + "AddIntegrationTest.java", params);
    executeFreemarkerTemplate("/integration-tests/base.ftl", actionIntegrationTestDirName + "BaseIntegrationTest.java", params);
    executeFreemarkerTemplate("/integration-tests/delete.ftl", actionIntegrationTestDirName + "DeleteIntegrationTest.java", params);
    executeFreemarkerTemplate("/integration-tests/edit.ftl", actionIntegrationTestDirName + "EditIntegrationTest.java", params);
    executeFreemarkerTemplate("/integration-tests/index.ftl", actionIntegrationTestDirName + "IndexIntegrationTest.java", params);
  }
}