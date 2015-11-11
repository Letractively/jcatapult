# Introduction #

This plugin provides applications with the ability to integrate modules into an existing web application. In addition, this plugin provides scripts and targets for developing new modules.

# Files #

Web applications should import this file if they ever need to integrate modules (this import statement is included with web applications that are built using the JCatapult _create-webapp_ tool).

```
<import file="${ant.home}/plugins-jcatapult/module/1.0/project-support.xml"/>
```

Module projects must import these files (these import statements are included with modules that are built using the JCatapult _create-module_ tool).

```
<import file="${ant.home}/plugins-jcatapult/module/1.0/clean.xml"/>
<import file="${ant.home}/plugins-jcatapult/module/1.0/module.xml"/>
```

# Web application targets #

The plugin provides these targets for web applications:

## add-module ##

This target adds a module to a project by asking for the URL within a Savant repository. This URL is the directory that contains the module artifacts for all versions of the module. This URL is used, instead of a direct URL to the artifact, so that the latest version of the module can be added to the project.

# Module project targets #

The plugin provides these targets for module projects:

## clean ##

This target cleans up the modules target directory. Since modules store their web resources in src/web/main rather than /web, JCatapult creates symbolic links during the build so that modifications to web resources can be modified and reloaded without a copying step.

## jar ##

This process creates the JAR file for the module. Modules have special requirements in order to function properly with the JCatapult Migrator tool and for the JUnit tests to work properly when modules define JPA entity classes. This target produces 3 JAR files:

| **module.jar** | Contains the compiled classes for the module and any configuration. |
|:---------------|:--------------------------------------------------------------------|
| **module-src.jar** | Contains the modules source files for IDE to use.                   |
| **module-test.jar** | Contains a generated persistence.xml JPA configuration file used for unit testing and running the module in a development application. |

## app ##

This target creates a local web application that contains the module so that it can be tested. This process is somewhat complex, but here is an overview of the steps that are taken:

  1. Call the module _jar_ target to create all the JAR files for the module.
  1. Constructs a web application in the _target/web_ directory. This web application is based on the _webapp-template_ directory that is part of the JCatapult tools directory that was step as part of JCatapult Ant.
  1. Copies the module JAR files to the _target/web/WEB-INF/lib_ directory.
  1. Copies the test configuration to the _target/web/WEB-INF/classes_ directory.
  1. Creates a symlink from the _src/web/main_ directory to _target/web/WEB-INF/module/${module.name}/content_
  1. Creates a symlink from the _src/static/main_ directory to _target/web/module_ directory.
  1. Copies all the files from the _src/web/test_ directory into the _target/web_ directory.