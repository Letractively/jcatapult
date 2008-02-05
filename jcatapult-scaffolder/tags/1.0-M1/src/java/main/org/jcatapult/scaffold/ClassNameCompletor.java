/*
 * Copyright (c) 2002-2007, Marc Prud'hommeaux. All rights reserved.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 */
package org.jcatapult.scaffold;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *  A Completor implementation that completes java class names. By default,
 *  it scans the java class path to locate all the classes.
 *
 *  @author  Marc Prud'hommeaux and Brian Pontarelli
 */
public class ClassNameCompletor extends BetterSimpleCompletor {

    /**
     * Complete candidates using all the classes available in the
     * java <em>CLASSPATH</em>.
     *
     * @throws  IOException If there was an error loading the class path classes.
     */
    public ClassNameCompletor() throws IOException {
        this(null);
    }

    public ClassNameCompletor(final SimpleCompletorFilter filter) throws IOException {
        super(getClassNames(), filter);
        setDelimiter(".");
    }

    public static String[] getClassNames() throws IOException {
        Set<URL> urls = new HashSet<URL>();

        ClassLoader loader = ClassNameCompletor.class.getClassLoader();
        while (loader != null) {
            if (loader instanceof URLClassLoader) {
                urls.addAll(Arrays.asList(((URLClassLoader) loader).getURLs()));
            }

            loader = loader.getParent();
        }

        // Now add the URL that holds java.lang.String. This is because
        // some JVMs do not report the core classes jar in the list of
        // class loaders.
        Class[] systemClasses = new Class[] {String.class, javax.swing.JFrame.class};
        for (int i = 0; i < systemClasses.length; i++) {
            URL classURL = systemClasses[i].getResource("/" + systemClasses[i].getName().replace('.', '/') + ".class");
            if (classURL != null) {
                URLConnection uc = classURL.openConnection();

                if (uc instanceof JarURLConnection) {
                    urls.add(((JarURLConnection) uc).getJarFileURL());
                }
            }
        }

        Set<String> classes = new HashSet<String>();
        for (URL url : urls) {
            File file = new File(url.getFile());

            if (file.isDirectory()) {
                Set<String> files = getClassFiles(file.getAbsolutePath(), new HashSet<String>(), file, 200);
                classes.addAll(files);
                continue;
            }

            if ((file == null) || !file.isFile()) {
                continue;
            }

            try {
                JarFile jf = new JarFile(file);
                for (Enumeration e = jf.entries(); e.hasMoreElements();) {
                    JarEntry entry = (JarEntry) e.nextElement();
                    if (entry == null) {
                        continue;
                    }

                    String name = entry.getName();
                    if (!name.endsWith(".class")) {
                        continue;
                    }

                    classes.add(name);
                }
            } catch (IOException e) {
                // This isn't a JAR file, skip it.
            }
        }

        // now filter classes by changing "/" to "." and trimming the
        // trailing ".class"
        Set<String> classNames = new TreeSet<String>();
        for (String name : classes) {
            classNames.add(name.replace('/', '.').substring(0, name.length() - 6));
        }

        return classNames.toArray(new String[classNames.size()]);
    }

    private static Set<String> getClassFiles(String root, Set<String> holder, File directory,
            int maxDirectories) {
        // we have passed the maximum number of directories to scan
        if (maxDirectories < 0) {
            return holder;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            String name = file.getAbsolutePath();
            if (!(name.startsWith(root))) {
                continue;
            }

            if (file.isDirectory()) {
                getClassFiles(root, holder, file, maxDirectories - 1);
            } else if (file.getName().endsWith(".class")) {
                holder.add(file.getAbsolutePath().substring(root.length() + 1));
            }
        }

        return holder;
    }
}