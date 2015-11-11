# Libraries #

JCatapult supports the concept of libraries. Libraries are the best location to put common code that will be used across applications. Libraries might contain these types of classes:

  * Common toolkits
  * Common services and implementations
  * Common entity classes including Embeddable and base-classes (avoid putting Entity classes in libraries)
  * Common actions without views (for example the JCatapult Country action is included in the JCatapult-core library)


# Create a library #
To create a library, run the _make-project_ plugin like this:

```
$ svnt jcatapult:make-project --type=library
```

After you answer the questions it asks, you should have a newly created library. This library can be built using the following command:

```
svnt clean jar
```

Once the library has been compiled, you can use the library from other applications by adding it as a dependency in the _project.xml_ file. If you haven't released the library as a final version you will need to include the library as an integration build in the application's _project.xml_ file like this:

```
<artifact group="example.com" name="my-library" version="1.0-A1-{integration}"/>
```

Once the library has been released you can remove the integration build from the _project.xml_ file like this:

```
<artifact group="example.com" name="my-library" version="1.0-A1"/>
```

For more information on Integration builds, please consult the Savant documentation at http://code.google.com/p/savant-build