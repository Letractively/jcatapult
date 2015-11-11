# Introduction #

The jCatapult Database Manager (**dbmgr** for short) was built to help make managing your databases easier.  It accomplishes this by managing creation, seeding, patching, and versioning of your project databases for you.

# Project Integration #
The dbmgr is integrated into projects via the JCatapult Ant Mysql plugin, which is imported in the project's build.xml file.  JCatapult currently only supports MySQL, however, work is currently being done to provide support for PostgreSQL and others.

## Connectivity ##
The JCatapult dbmgr uses 2 databases for development:
  * Main Database
  * Test Database

### The Main Database ###
The Main database is used by the webapp during runtime and is connected to through JNDI via an entry in the `deploy/tomcat/main/conf/context.xml` file.  An example entry has been provided below:

```
<Resource name="jdbc/test_webapp" auth="Container" type="javax.sql.DataSource"
          initialSize="2" maxActive="15" maxIdle="5" maxWait="10000"
          removeAbandoned="true" removeAbandonedTimeout="180"
          validationQuery="select 1" testOnBorrow="true" testWhileIdle="true"
          timeBetweenEvictionRunsMillis="30000" minEvictableIdleTimeMillis="60000"
          poolPreparedStatements="true" maxOpenPreparedStatements="30"
          username="dev" password="dev" driverClassName="com.mysql.jdbc.Driver"
          url="jdbc:mysql://localhost:3306/test_webapp?relaxAutoCommit=true&amp;characterEncoding=utf8"/>
```

### Test Database ###
The Test Database is used only during unit testing and, like the main database, is connected to through JNDI.  Instead of connecting via an entry in the context.xml, however, the test database is connected to programatically within the `org.jcatapult.database.JPABaseTest` class contained within the jcatapult-core-test jar (which is a dependency defined within every project's project.xml by default).  Consequently, in order for your unit test to correctly connect to the database, it must be a sub-class of JPABaseTest.

## Database Creation ##
In development, both the main and test databases can be created by executing the following ant targets respectively:

```
ant create-main-database
```

```
ant create-test-database
```

**TIP**: Because the test database is only used during unit testing, it's only required to create the test database if you're running unit tests.  To make it as simple as possible, the JCatapult testing framework creates your test database for you when you run your unit tests via the `ant test` target.

## SQL Scripts ##
The dbmgr is designed to execute SQL scripts located within files in your project and it does this by directory structure convention and a filename versioning format.

### Directory Structure ###
By convention, the dbmgr expects SQL script files to reside in the following directories:

```
src
 |
 |- db
 |   |- main
 |   |   |- base
 |   |   |   |- ...
 |   |   |
 |   |   |- alter
 |   |   |   |- ...
 |   |   |
 |   |   |- seed
 |   |   |   |- ...
```

#### Base Directory ####
The concept behind the base directory is that it contains one and only one SQL file, `tables.sql`, that represents the 'base' database schema your project shipped with release version 1.0. Moreover, the tables.sql file only contains the SQL scripts necessary to create tables that the project alone defines via local hibernate entity objects.

Currently, the tables.sql file must be created manually when the project goes 1.0.  Developers can create this file using the JCatapult Ant Hibernate plugin (imported by all new projects by default) by executing the ant target below:

```
ant table-sql
```

**NOTE:** The current version of the Hibernate plugin's table-sql ant target generates table creation scripts for all hibernate entity objects that are localized to your project and also those that are defined in the project's `src/conf/main/META-INF/persistence.xml` file.  Consequently, after running `ant table-sql` you must manually delete all table creation scripts that aren't localized to your project.  To reiterate what was stated above, the purpose of the tables.sql file is to represent the 'base' database schema that your project alone defines via local hibernate objects.

#### Alter Directory ####
The concept behind the alter directory is that it should only contain scripts that alter your database schema.  Alterations includes operations such as:  creating tables, adding table fields, changing field data types, changing field names, adding indexes, etc.

#### Seed Directory ####
The concept behind the seed directory is that it should only contain scripts that add, edit, and delete data within your database.  In other other words, seed scripts should only perform inserts, updates, and deletes on your data.

### Filename Format ###
SQL script filenames must follow the BNF format below in order for the dbmgr to execute them successfully:

```
<sql-file> ::= <version-part>"-"<label-part>.sql
<version-part> ::= <digit>"."<digit>["."<digit>]
<label-part> ::= {<letter>|<digit>}
```

### Database Versioning ###
The ultimate purpose of the base, alter, and seed concepts is to create a version mechanism for a project database.  Obviously, during the lifetime of a project, a database might take on many forms changing from one version to the next.  Consequently, in order for the dbmgr to successfully migrate a database from version to version, it must be able to determine previously migrated database versions and the order in which SQL script files are to be executed based on those versions. It makes this determination by using two mechanisms:

  1. The JCATAPULT\_ARTIFACT\_VERSIONS table
  1. The version of the SQL script files in the project

The pseudo-code below attempts to illustrate this determination process:

  1. If a database currently exist for the project then read the last migrated version from the JCATAPULT\_ARTIFACT\_VERSIONS table and goto 4.  If the database doesn't exist then set the last migrated database version to 0.0 and goto 2.
  1. If the `src/db/main/base/tables.sql` exists then execute it and goto 4.  If not, goto 3.
  1. Defer to hibernate to create database.  Hibernate proceeds to create the database using local project entities and those defined in `src/conf/main/META-INF/persistence.xml`.  Goto 7.
  1. If properly formatted SQL script files exist in `src/db/main/alter` order them based on file version with the smallest version being greater than the last migrated version.  Goto 5.
  1. If properly formatted SQL script files exist in `src/db/main/seed` order them based on file version with the smallest version being greater than the last migrated version.  Goto 6.
  1. Alternate execution of alter and seed scripts based on version order.  Goto 7.
  1. If the JCATAPULT\_ARTIFACT\_VERSIONS table doesn't exist then create it. Set previously migrated version in the JCATAPULT\_ARTIFACT\_VERSIONS to the current project version.  Goto 8.
  1. Exit.

# Warnings #

**NOTE:**  Be aware that sql seed files are case-insensitive on windows and mac and case-sensitive on unix.  This means that if you refer to a table name within sql, you must specify the exact table name that was created in the database in order for proper execution across multiple platforms.