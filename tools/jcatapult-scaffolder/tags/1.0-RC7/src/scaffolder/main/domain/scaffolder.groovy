import org.jcatapult.scaffolder.AbstractScaffolder
import org.jcatapult.scaffolder.annotation.LongDescription
import org.jcatapult.scaffolder.annotation.ShortDescription
import org.jdom.Document
import org.jdom.Element
import org.jdom.input.SAXBuilder
import org.jdom.output.Format
import org.jdom.output.XMLOutputter

/**
* This class is a simple CRUD scaffolder for JCatapult.
*/
@ShortDescription("Creates a single domain object.")
@LongDescription(
"""Creates a single domain object. This also handles a number of additional operations
that are critical for modules, but it can also be used for webapps.

  This scaffolder asks the following questions:
    1. The package to put the domain class into (tab completion available)
    2. The name of the domain class

  This scaffolder creates the domain class and for modules adds the domain class
  to the module.xml file in src/conf/main/META-INF.
""")
public class DomainScaffolder extends AbstractScaffolder {
  public void execute() {
    // Ask the user what the package and domain name are
    String domainGuess = getFirstPackage("domain");
    String pkgName = ask("Enter the package to place the domain class", "Java package ",
        "Invalid package", domainGuess);
    String className = ask("Enter the name of the domain class", "Domain class ",
        "Invalid class ", null);

    // Verify
    String domainClass = pkgName + "." + className;
    String verify = null;
    while (verify != "yes") {
      verify = ask(
      """Please review the scaffolding configuration

    Domain class: ${domainClass}

    Is this is correct, type yes, if not type no""", "", "Invalid input", "yes");

      if (verify == "no") {
        println("Scaffolding cancelled");
        System.exit(1);
      } else if (verify != "yes") {
        println("Invalid response");
      }
    }

    executeTemplates(pkgName, className);
  }

  private void executeTemplates(String pkgName, String className) {

    // Create the index action
    def params = [pkgName: pkgName, className: className];

    // Make the directory for all the actions
    String mainDirName = "src/java/main/" + pkgName.replace(".", "/") + "/";
    new File(mainDirName).mkdirs();
    String testDirName = "src/java/test/unit/" + pkgName.replace(".", "/") + "/";
    new File(testDirName).mkdirs();

    // Create the actions
    executeFreemarkerTemplate("/domain.ftl", mainDirName + className + ".java", params);
    executeFreemarkerTemplate("/test.ftl", testDirName + className + "Test.java", params);

    // If this is a module, add the entity entry into module.xml
    File moduleFile = new File("src/conf/main/META-INF/module.xml");
    if (moduleFile.exists()) {
      SortedSet set = new TreeSet();
      set.add(pkgName + "." + className);

      // Add existing from module.xml
      Node persistence = new XmlParser().parse(moduleFile).persistence[0];
      persistence.children().each { set.add(it.text()) }

      // Use JDOM to add the new ones and save it out
      Document moduleDoc = new SAXBuilder().build(moduleFile);
      Element persistenceElem = moduleDoc.getRootElement().getChild("persistence");
      if (persistenceElem == null) {
        println("Project doesn't define a <persistence> element in the module.xml file. Skipping entity management");
        return;
      }

      persistenceElem.removeChildren("clas");
      set.each { c ->
        Element newClassElem = new Element("class");
        newClassElem.setText(c);
        persistenceElem.addContent(newClassElem);
      }

      // Write it out
      Format format = Format.getPrettyFormat();
      format.setLineSeparator("\n");
      XMLOutputter output = new XMLOutputter(format);
      FileOutputStream fos = new FileOutputStream(moduleFile);
      output.output(moduleDoc, fos);
      fos.close();
    }
  }
}
