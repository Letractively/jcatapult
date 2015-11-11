# Introduction #

This document covers how to write a custom scaffolder by covering the scaffold API and also a step-by-step example of creating a simple scaffolder.

# API #

The JCatapult scaffolder API provides developers with the ability to create custom scaffolders quickly. Scaffolders are written in the Groovy programming language and you should have a good understanding of Groovy before trying to write a scaffolder.

There are two ways to write a custom scaffolder. The first method is to implement the org.jcatapult.scaffold.Scaffolder interface in a Groovy file named `scaffolder.groovy`. This interface defines a single method named `execute` and has this signature:

```
public interface Scaffolder {
  void execute();
}
```

The second method is to extend the class `org.jcatapult.scaffold.AbstractScaffolder`. This class provides a number of helper methods that make scaffolding simpler. This is the preferred method of creating scaffolders for JCatapult.

## Description ##

There are two types of descriptions that a JCatapult scaffolder can provide to users, short and long. The short description should be a short one-line description that provides information about the overall function of the scaffolder. This description is placed as a class level annotation on the scaffolder class. The annotation used to provide the short description is `org.jcatapult.scaffold.annotation.ShortDescription`. Here is an example of using the annotation:

```
@ShortDescription("This scaffolder rocks the house!")
public class MyScaffolder implements Scaffolder {
  ...
}
```

The long description should provide detailed information about how the scaffolder works and should tell the user what questions the scaffolder will ask and what it produces as a result. This description is also placed as class level annotation using the `org.jcatapult.scaffold.annotation.LongDescription` annotation. Here is an example of using this long description:

```
@LongDescription(
  "This scaffolder rocks the house!\n" +
  "Here are the questions that this scaffolder will ask:\n\n" +
  "  1. What's your name?\n" +
  "  2. What's your age?\n\n" +
  "After you have successfully answered these questions, this scaffolder will" +
  "rock your house with mad force."
)
public class MyScaffolder implements Scaffolder {
  ...
}
```

# Creating a scaffolder #

Scaffolders are simply directories that are placed into a directory called `scaffolders` within the installation directory of the JCatapult Scaffolding tool. Each directory inside the `scaffolders` directory is an individual scaffolder and the name of the scaffolder is based on the name of the directory. Here is an example of the file structure:

```
<scaffolder-tool-home>
      | - scaffolders
      |       |- crud
      |       |    |- ...
      |       |- module-crud
      |       |    |- ...
```

This layout means that there are two scaffolders available for use, a scaffolder named **crud** and a scaffolder named **module-crud**.

## Scaffolder layout ##

Each scaffolder is defined in a separate directory and that directory has a very simple structure. This structure looks like this:

```
<scaffolder-dir>
      |- lib
      |   |- ...
      |
      |- scaffolder.groovy
```

### scaffolder.groovy ###

Inside the scaffolder directory must be a single file named `scaffolder.groovy`. This file is a Groovy source file that contains the scaffolder implementation, which must implement the `org.jcatapult.scaffold.Scaffolder` interface or extend the abstract class. The JCatapult Scaffolding system will load this file and execute it.

### lib ###

If a scaffolder requires additional libraries in order to function properly it can define an additional directory named **lib**. This directory can contain any number of JAR files that will be loaded for the scaffolder to use.

**ClassLoading note**: For those who are interested, JCatapult creates a new classloader for the scaffolder and loads the classloader with each of the JAR files from the lib directory as well as the `scaffolder.groovy` class after it has been compiled by Groovy into Java byte-code.