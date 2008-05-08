/**
 * User: jhumphrey
 * Date: Nov 2, 2007
 */
String project = properties["project.name"];
if (project.endsWith("-module")) {
  properties["module.name"] = project.substring(0, project.length() - 10);
} else {
  properties["module.name"] = project;
}