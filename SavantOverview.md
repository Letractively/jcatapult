# Introduction #

This document covers the architecture of the JCatapult Savant integration. JCatapult uses Savant for building, however you are feel to use any build system you want. Savant is simply a wrapper around Apache Ant provides dependency management and a plugin system.

# Why Savant? #

JCatapult uses Savant for these reasons:

  * It is based on Ant, which is a de-facto standard for builds and install on nearly every Java developers machine
  * Savant provides the ability to download plugins and makes build files simple and concise
  * Savant provides an excellent dependency management system that is simple, concise and powerful

We considered using Maven and Ivy but decided that Savant was a better solution as it provides better dependency management than Maven and is simpler than Ivy. Savant also provides support for a downloadable plugin system for Ant, something that Ivy doesn't currently provide.

# How to use it #

Using Savant is as simple as using Ant. In fact, the syntax is identical except for the executable you invoke. Here is an example of compiling a project using Savant:

**Windows**
```
c:\dev> svnt compile
```

**Unix**
```
$ svnt compile
```

If you want a list of the targets you can use, simply execute the project help command like this:

**Windows**
```
c:\dev> svnt -p
```

**Unix**
```
$ svnt -p
```

**NOTE:** Due to a bug in Ant, some targets are listed twice. One time with a prefix and one time without. It looks like this:

```
compile
...
java-compile.compile
```

You can always safely ignore the version with the prefix.

# How it works #

To understand how JCatapult leverages Savant's plugin system for Ant you should first consult the Savant documentation at http://code.google.com/p/savant-build.

# Plugins #

The main plugins available for Savant are listed at the Savant website:

http://code.google.com/p/savant-build/wiki/Plugins

JCatapult provides a few additional plugins for Savant that make working with JCatapult easier. These plugins are:

  * DB Manager (dbmgr) - This provides support for the JCatapult [database management](GuideDatabaseManagement.md) tool
  * Module (module) - This provides support for developing [JCatapult modules](GuideModule.md)

If you are simply developing a web application and won't be using the database management tool or modules, you don't need these plugins at all. In fact, you don't need to use Savant either, but we still encourage you to use Savant.

If you create your web application or module using the **make-project** script, your project will automatically be setup to use Savant without any changes.