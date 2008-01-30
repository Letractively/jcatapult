import org.jcatapult.scaffold.ScaffoldingHelper
import org.jcatapult.scaffold.URLPrefixCompletor
import org.jcatapult.scaffold.lang.Type
import net.java.text.SimplePluralizer;

// Ask the user what the bean they want to scaffold is
String className = ScaffoldingHelper.ask("Enter the domain class to CRUD scaffold", "Scaffolding domain object ",
    "Invalid domain class", null);

// Setup some useful names
String simpleClassName = ScaffoldingHelper.makeSimpleClassName(className);
String simplePropertyName = ScaffoldingHelper.makePropertyName(simpleClassName);
SimplePluralizer pluralizer = new SimplePluralizer();
String simplePluralPropertyName = pluralizer.pluralize(simplePropertyName);

// Try to get the AST
Type type = ScaffoldingHelper.getType(className);
if (type == null) {
  println "Invalid domain class [" + className + "]";
  System.exit 1;
}

// Get the URL
String defaultURL = "/admin/" + simplePluralPropertyName;
String url = ScaffoldingHelper.ask("Enter the URL prefix of the CRUD", "Scaffolding URL prefix ",
    "Invalid URL prefix", defaultURL, new URLPrefixCompletor());

// Get the action package
String defaultActionPackage = ScaffoldingHelper.getDefaultActionPackage();
if (defaultActionPackage != null) {
  defaultActionPackage = defaultActionPackage + url.replace("/", ".");
}
String actionPackage = ScaffoldingHelper.ask("Enter the action package",
    "Scaffolding action package ", "Invalid action package", defaultActionPackage);

// Get the service package
String baseActionPackage = null;
if (actionPackage.endsWith(url.replace("/", "."))) {
  baseActionPackage = actionPackage.substring(0, actionPackage.length() - url.length());
} else {
  baseActionPackage = actionPackage;
}

String defaultServicePackage = ScaffoldingHelper.getDefaultServicePackage(baseActionPackage);
String servicePackage = ScaffoldingHelper.ask("Enter the service package",
    "Scaffolding service package ", "Invalid service package", defaultServicePackage);

// Verify
String verify = null;
while (verify != "yes") {
  verify = ScaffoldingHelper.ask(
  """Please review the scaffolding configuration

Type: crud
Domain class: ${className}
URL: ${url}
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

// Create the index action
def params = [actionPackage: actionPackage, servicePackage: servicePackage, url: url, type: type];

// Make the directory for all the actions
File actionsDir = new File("src/java/main/" + actionPackage.replace(".", "/"));
actionsDir.mkdirs();

// Create the actions
String scriptPath = scriptDir.getAbsolutePath();
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/add.ftl", new File(actionsDir, "Add.java"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/index.ftl", new File(actionsDir, "Index.java"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/edit.ftl", new File(actionsDir, "Edit.java"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/save.ftl", new File(actionsDir, "Save.java"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/delete.ftl", new File(actionsDir, "Delete.java"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/prepare.ftl", new File(actionsDir, "Prepare.java"),
    overwrite, params);

// Create the form validation
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/validation.ftl", new File(actionsDir, "Save-validation.xml"),
    overwrite, params);

// Create the error and message bundles
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/messages.ftl", new File(actionsDir, "Save.properties"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/package.ftl", new File(actionsDir, "package.properties"),
    overwrite, params);

// Make the directory for the service
File serviceDir = new File("src/java/main/" + servicePackage.replace(".", "/"));
serviceDir.mkdirs();

// Create the service interface and implementation
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/service/service.ftl",
    new File(serviceDir, simpleClassName + "Service.java"), overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/service/service-impl.ftl",
    new File(serviceDir, simpleClassName + "ServiceImpl.java"), overwrite, params);

// Make the directory for the JSPs
File webDir = new File("web/WEB-INF/content" + url);
webDir.mkdirs();

// Create the JSPs
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/jsps/add.ftl",
    new File(webDir, "add.jsp"), overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/jsps/edit.ftl",
    new File(webDir, "edit.jsp"), overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/jsps/index.ftl",
  new File(webDir, "index.jsp"), overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/jsps/form.ftl",
  new File(webDir, "form.jsp"), overwrite, params);

// Make the directory for the action unit tests
File actionTestsDir = new File("src/java/test/" + actionPackage.replace(".", "/"));
actionTestsDir.mkdirs();

// Create the action unit tests
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/index.ftl", new File(actionTestsDir, "IndexTest.java"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/edit.ftl", new File(actionTestsDir, "EditTest.java"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/save.ftl", new File(actionTestsDir, "SaveTest.java"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/delete.ftl", new File(actionTestsDir, "DeleteTest.java"),
    overwrite, params);
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/prepare.ftl", new File(actionTestsDir, "PrepareTest.java"),
    overwrite, params);

// Make the directory for the service unit tests
File serviceTestsDir = new File("src/java/test/" + servicePackage.replace(".", "/"));
serviceTestsDir.mkdirs();

// Create the service unit tests
ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/service.ftl",
    new File(serviceTestsDir, simpleClassName + "ServiceImplTest.java"), overwrite, params);
