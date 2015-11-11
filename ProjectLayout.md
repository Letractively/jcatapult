# Project types #

JCatapult projects fall into three types:

  * Web applications
  * Modules
  * Libraries

Each project type has different project layouts and those layouts are as follows:

# Web applications #

Here is the web application project layout

```
deploy                           <- J2EE container deployment files
 |- tomcat
 |   |- main
 |   |   |- conf
 |   |   |   |- context.xml      <- The Tomcat context configuration file for development
 |
src
 |- java
 |   |- main                     <- Main Java source files
 |   |   |- org
 |   |   |   |- ...
 |   |
 |   |- test
 |   |   |- unit                 <- Unit tests
 |   |   |   |- org
 |   |   |   |   |- ...
 |   |   |
 |   |   |- integration          <- Integration tests
 |   |   |   |- org
 |   |   |   |   |- ...
 |
 |- db                           <- Database migrator files
 |   |- main
 |   |   |- base
 |   |   |   |- ...
 |   |   |
 |   |   |- alter
 |   |   |   |- ...
 |   |   |
 |   |   |- seed
 |   |   |   |- ...
 |
 |- conf
 |   |- main                     <- Main configuration
 |   |   |- META-INF
 |   |   |   |- persistence.xml  <- JPA configuration file
 |   |
 |   |- test
 |   |   |- unit                 <- Unit test configuration
 |   |   |   |- ...
 |   |   |
 |   |   |- integration          <- Integration test configuration
 |   |   |   |- ...
 |
web                              <- Web resources
 |- WEB-INF
 |   |- config                   <- Environment aware configuration files
 |   |   |- ...
 |   |
 |   |- content                  <- Website content JSPs/FreeMarker
 |   |   |- ...
 |   |
 |   |- decorators               <- SiteMesh decorators
 |   |   |- ...
 |   |
 |   |- message                  <- Localization and internationalization messages
 |   |   |- ...
 |   |
```

**NOTE:** Since JCatapult supports inline web applications so that JSPs and other web resources can be modified without requiring a re-compile or copy step to reload, the web directory is the location that the web application is run from during development. The standard web directories of _WEB-INF/classes_ and _WEB-INF/lib_ are therefore cleaned out by the JCatapult Ant J2EE clean target. In addition, these directories should not be part of source control.

# Libraries #

Here is the library project layout

```
src
 |- java
 |   |- main                     <- Main Java source files
 |   |   |- org
 |   |   |   |- ...
 |   |
 |   |- test
 |   |   |- unit                 <- Unit tests
 |   |   |   |- org
 |   |   |   |   |- ...
 |   |   |
 |   |   |- integration          <- Integration tests
 |   |   |   |- org
 |   |   |   |   |- ...
 |
 |- conf
 |   |- main                     <- Main configuration
 |   |   |- META-INF
 |   |   |   |- persistence.xml  <- JPA configuration file
 |   |
 |   |- test
 |   |   |- unit                 <- Unit test configuration
 |   |   |   |- ...
 |   |   |
 |   |   |- integration          <- Integration test configuration
 |   |   |   |- ...
 |
```

# Modules #

Here is the module project layout

```
src
 |- java
 |   |- main                     <- Main Java source files
 |   |   |- org
 |   |   |   |- ...
 |   |
 |   |- test
 |   |   |- unit                 <- Unit tests
 |   |   |   |- org
 |   |   |   |   |- ...
 |   |   |
 |   |   |- integration          <- Integration tests
 |   |   |   |- org
 |   |   |   |   |- ...
 |
 |- db                           <- Database migrator files
 |   |- main
 |   |   |- base
 |   |   |   |- ...
 |   |   |
 |   |   |- alter
 |   |   |   |- ...
 |   |   |
 |   |   |- seed
 |   |   |   |- ...
 |   |
 |   |- test
 |   |   |- ...
 |
 |- conf
 |   |- main                     <- Main configuration
 |   |   |- META-INF
 |   |   |   |- module.xml       <- Module configuration file
 |   |
 |   |- test
 |   |   |- unit                 <- Unit test configuration
 |   |   |   |- ...
 |   |   |
 |   |   |- integration          <- Integration test configuration
 |   |   |   |- ...
 |
web                              <- Web resources
 |
 |- module                       <- Static web resources like CSS, images, JavaScript
 | 
 |- WEB-INF
 |   |- config                   <- Testing configuration files (FOR TESTING ONLY)
 |   |   |- ...
 |   |
 |   |- content                  <- Module content JSPs/FreeMarker
 |   |   |- ...
 |   |
 |   |- decorators               <- SiteMesh decorators (FOR TESTING ONLY)
 |   |   |- ...
 |   |
 |   |- message                  <- Localization and internationalization messages
 |   |   |- ...
 |   |
```