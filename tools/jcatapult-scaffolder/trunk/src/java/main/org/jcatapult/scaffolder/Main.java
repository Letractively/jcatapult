/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.scaffolder;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcatapult.scaffolder.annotation.LongDescription;
import org.jcatapult.scaffolder.annotation.ShortDescription;

import groovy.lang.GroovyClassLoader;
import net.java.lang.StringTools;

/**
 * <p>
 * This class is a main entry point for scaffolders.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Main {
    public static void main(String[] args) throws IOException {

        Map<String, File> scaffolderDirs = getScaffolderDirs();

        Options options = new Options();
        options.addOption(new Option("o", "overwrite", false, "Overwrite existing files created by the scaffolder"));

        CommandLineParser parser = new BasicParser();
        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("Unable to run scaffolder");
            return;
        }

        String[] commands = line.getArgs();
        if (commands.length == 0 || commands.length > 2 ||
                (commands.length == 2 && !commands[0].equals("help"))) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("Usage: scaffold [--overwrite] <command>", options);
            System.exit(1);
        } else if (commands.length == 1) {
            if (commands[0].equals("help")) {
                printAllHelp(scaffolderDirs);
            } else {
                File scaffolderDir = new File(scaffolderDirs.get(commands[0]).getParentFile(), commands[0]);
                Scaffolder scaffolder = makeScaffolder(commands[0], scaffolderDir);
                scaffolder.setDir(scaffolderDir);
                scaffolder.setOverwrite(line.hasOption("overwrite"));
                scaffolder.execute();
            }
        } else if (commands.length == 2) {
            File scaffolderDir = new File(scaffolderDirs.get(commands[1]).getParentFile(), commands[1]);
            Scaffolder scaffolder = makeScaffolder(commands[1], scaffolderDir);
            LongDescription description = scaffolder.getClass().getAnnotation(LongDescription.class);
            if (description != null) {
                System.out.println(description.value());
            } else {
                System.out.println("The scaffolder [" + commands[1] + "] has no help.");
            }
        }
    }

    /**
     * <p>Helper method that returns a map containing the default and custom scaffolders.</p>
     *
     * Map:
     * <ul>
     *   <li>key: scaffolder (directory) name</li>
     *   <li>value: file object representing the directory</li>
     * </ul>
     *
     * Scaffolder Dirs:
     * <ul>
     *   <li>default: JCATAPULT_HOME/scaffolder/scaffolders</li>
     *   <li>custom: USER.HOME/.jcatapult/scaffolders</li>
     * </ul>
     *
     * @return map contaning the default and custom scaffolders dir
     */
    private static Map<String, File> getScaffolderDirs() {

        Map<String, File> scaffolderDirs = new HashMap<String, File>();

        // get the default scaffolder dir
        String jcatapultHome = System.getenv("JCATAPULT_HOME");
        if (StringTools.isEmpty(jcatapultHome)) {
            System.err.println("JCATAPULT_HOME must be set into your environment");
            System.exit(1);
        }
        File defaultScaffolderDir = new File(jcatapultHome, "tools/scaffolder/scaffolders");
        File[] defaultScaffolders = defaultScaffolderDir.listFiles();
        for (File defaultScaffolder : defaultScaffolders) {
            scaffolderDirs.put(defaultScaffolder.getName(), defaultScaffolder);
        }

        // get the custom scaffolder dir
        String dotJcat = System.getProperty("user.home") + "/.jcatapult";
        File customScaffolderDir = new File(dotJcat, "scaffolders");
        if (customScaffolderDir.exists() && customScaffolderDir.isDirectory()) {
            File[] customScaffolders = customScaffolderDir.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    File file = new File(dir, name);
                    boolean accept = true;
                    if (name.equals(".svn") || name.equals(".cvs") || !file.isDirectory()) {
                        accept = false;
                    }
                    return accept;
                }
            });
            for (File customScaffolder : customScaffolders) {
              scaffolderDirs.put(customScaffolder.getName(),  customScaffolder);
            }
        }

        return scaffolderDirs;
    }

    private static Scaffolder makeScaffolder(String name, File scaffolderDir) {
        File scaffolderFile = new File(scaffolderDir, "scaffolder.groovy");
        if (!scaffolderFile.exists()) {
            System.err.println("Invalid scaffolder [" + name + "]");
        }

        GroovyClassLoader gcl = new GroovyClassLoader();
        File libDir = new File(scaffolderDir, "lib");
        if (libDir.exists()) {
            File[] libs = libDir.listFiles();
            for (File lib : libs) {
                try {
                    gcl.addURL(lib.toURI().toURL());
                } catch (MalformedURLException e) {
                    System.err.println("Unable to load scaffolder library [" + lib.getAbsolutePath() + "]");
                    System.exit(1);
                }
            }
        }

        try {
            Class clazz = gcl.parseClass(scaffolderFile);
            return (Scaffolder) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unable to run scaffolder");
            System.exit(1);
        }

        return null;
    }

    private static void printAllHelp(Map<String, File> scaffolders) {
        Collection<File> list = scaffolders.values();
        for (File file : list) {
            if (file.isDirectory()) {
                Scaffolder scaffolder = makeScaffolder(file.getName(), file);
                System.out.println(file.getName() + ": ");

                ShortDescription description = scaffolder.getClass().getAnnotation(ShortDescription.class);
                if (description != null) {
                    System.out.println("\t" + description.value());
                } else {
                    System.out.println("\tThe scaffolder [" + file.getName() + "] has no help.");
                }
            }
        }
    }
}