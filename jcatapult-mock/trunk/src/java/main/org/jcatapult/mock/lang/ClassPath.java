/*
 * Copyright (c) 2001-2011, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jcatapult.mock.lang;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Collections.list;

/**
 * This class models a ClassPath that contains a list of files that are directories or JAR/ZIP files in the path.
 *
 * @author Brian Pontarelli
 */
public class ClassPath {
  private List<String> names = new ArrayList<String>();

  public ClassPath() {
  }

  /**
   * Copies the given classpath's entries into this new classpath.
   *
   * @param classPath The classpath to copy.
   */
  public ClassPath(ClassPath classPath) {
    this.names.addAll(classPath.names);
  }

  /**
   * Makes a new classpath with the given names.
   *
   * @param names The name for the classpath.
   */
  public ClassPath(List<String> names) {
    this.names.addAll(names);
  }

  /**
   * Constructs a new classpath builder using the given classloader.
   *
   * @param loader The class loader to use.
   * @return The ClassPathBuilder.
   */
  public static ClassPathBuilder build(ClassLoader loader) {
    return new ClassPathBuilder(loader);
  }

  /**
   * Constructs a new classpath builder using the current Thread's context classloader.
   *
   * @return The ClassPathBuilder.
   */
  public static ClassPathBuilder build() {
    return new ClassPathBuilder(Thread.currentThread().getContextClassLoader());
  }

  /**
   * @return The current classpath.
   * @throws java.io.IOException If there was any problems retrieving the classpath from the current thread's context
   *                             classloader.
   */
  public static ClassPath getCurrentClassPath() throws IOException {
    ClassPathBuilder builder = new ClassPathBuilder(Thread.currentThread().getContextClassLoader());
    return builder.build();
  }

  /**
   * Adds the file to the classpath by getting the file's absolute path and appending this string to the classpath.
   *
   * @param file The file to add to the classpath.
   */
  public void addFile(File file) {
    names.add(file.getAbsolutePath());
  }

  /**
   * Removes the existing file entry from the classpath.
   *
   * @param file The file whose absolute path to remove from the classpath.
   */
  public void removeFile(File file) {
    names.remove(file.getAbsolutePath());
  }

  /**
   * Adds all the files to the classpath.
   *
   * @param files The files.
   */
  public void addAllFiles(File[] files) {
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      addFile(file);
    }
  }

  /**
   * Adds the entry to the classpath.
   *
   * @param entry The entry to add to the classpath.
   */
  public void addEntry(String entry) {
    names.add(entry);
  }

  /**
   * Removes the entry from the classpath.
   *
   * @param entry The entry to remove.
   */
  public void removeEntry(String entry) {
    names.remove(entry);
  }

  /**
   * @return The list of names.
   */
  public List<String> getNames() {
    return Collections.unmodifiableList(names);
  }

  /**
   * Builds a URLClassLoader from the classpath. Each entry is first made into a URL. If this is successful, that URL is
   * added to the URLClassLoader's URL list. If not, a File is created and if that File exists, it is converted to a URL
   * and then added to the URLClassLoader.
   *
   * @param parent The parent classloader of the URLClassLoader being created.
   * @return The URLClassLoader and never null.
   * @throws IllegalStateException If the creation of the URLClassLoader failed because a URL could not be created for
   *                               each entry in the classpath.
   */
  public URLClassLoader toURLClassLoader(ClassLoader parent) throws IllegalStateException {
    List<URL> urls = new ArrayList<URL>();
    for (String s : names) {
      URL url;
      try {
        url = new URL(s);
      } catch (MalformedURLException e) {
        File f = new File(s);
        if (f.exists()) {
          try {
            url = f.toURI().toURL();
          } catch (MalformedURLException e1) {
            throw new IllegalStateException("Cannot create URLClassLoader because classpath entry [" +
              s + "] could not be converted to a URL from a File.");
          }
        } else {
          throw new IllegalStateException("Cannot create URLClassLoader because classpath entry [" +
            s + "] is not a URL or a File.");
        }
      }

      urls.add(url);
    }

    URLClassLoader cl;
    if (parent == null) {
      cl = new URLClassLoader(urls.toArray(new URL[urls.size()]));
    } else {
      cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
    }

    return cl;
  }

  /**
   * Converts the classpath to a platform compatible classpath String using the path separator character from the File
   * class.
   *
   * @return The classpath as a String or an empty String if the classpath is empty.
   */
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < names.size(); i++) {
      String entry = names.get(i);
      if (i != 0) {
        buf.append(File.pathSeparator);
      }

      buf.append(entry);
    }

    return buf.toString();
  }

  /**
   * Simple class to assist in build ClassPath objects using the classpath of a ClassLoader.
   */
  public static class ClassPathBuilder {
    private final ClassLoader classLoader;
    private final Set<String> excludes = new HashSet<String>();
    private final Set<Pattern> excludePatterns = new HashSet<Pattern>();

    public ClassPathBuilder(ClassLoader classLoader) {
      this.classLoader = classLoader;
    }

    /**
     * Adds a fully qualified URL to be excluded. This must be the fully qualified URL to a JAR or a classpath entry
     * such as:
     * <p/>
     * <pre>
     * file:///tmp/foo.jar
     * </pre>
     *
     * @param exclude The URL to exclude.
     */
    public void addExclude(String exclude) {
      excludes.add(exclude);
    }

    /**
     * Adds a pattern that will exclude all URLs that match.
     *
     * @param pattern The pattern.
     */
    public void addExcludePattern(Pattern pattern) {
      excludePatterns.add(pattern);
    }

    /**
     * Builds the ClassPath.
     *
     * @return The ClassPath.
     * @throws IOException If the classloader throws an exception.
     */
    public ClassPath build() throws IOException {
      List<String> list = new ArrayList<String>();
      List<URL> urls = list(classLoader.getResources("META-INF"));

      for (URL url : urls) {
        String externalForm = clean(url.toExternalForm());
        if (externalForm != null && !exclude(externalForm)) {
          list.add(externalForm);
        }
      }

      urls = list(classLoader.getResources(""));

      for (URL url : urls) {
        String externalForm = clean(url.toExternalForm());
        if (externalForm != null && !exclude(externalForm)) {
          list.add(externalForm);
        }
      }

      return new ClassPath(list);
    }

    private String clean(String externalForm) throws UnsupportedEncodingException {
      // JBoss scheme that is not supported
      if (externalForm.contains("vfsmemory:")) {
        return null;
      }

      externalForm = URLDecoder.decode(externalForm, "UTF-8");
      if (externalForm.endsWith("META-INF")) {
        externalForm = externalForm.substring(0, externalForm.length() - 8);
      } else if (externalForm.endsWith("META-INF/")) { /* JBoss work-around */
        externalForm = externalForm.substring(0, externalForm.length() - 9);
      }

      if (externalForm.endsWith("!/")) {
        externalForm = externalForm.substring(0, externalForm.length() - 2);
      }

      if (externalForm.startsWith("file:")) {
        externalForm = externalForm.substring(5);
      }

      if (externalForm.startsWith("jar:file:")) {
        externalForm = externalForm.substring(9);
      }

      // JBoss schemes
      int index = externalForm.indexOf("vfszip:");
      if (index >= 0) {
        externalForm = externalForm.substring(index + 7);
      }

      index = externalForm.indexOf("vfsfile:");
      if (index >= 0) {
        externalForm = externalForm.substring(index + 8);
      }

      return externalForm;
    }

    private boolean exclude(String externalForm) {
      for (String exclude : excludes) {
        if (externalForm.equals(exclude)) {
          return true;
        }
      }

      for (Pattern pattern : excludePatterns) {
        if (pattern.matcher(externalForm).matches()) {
          return true;
        }
      }

      return false;
    }
  }
}