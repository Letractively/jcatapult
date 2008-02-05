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
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jcatapult.scaffolder.annotation.LongDescription;
import org.jcatapult.scaffolder.annotation.ShortDescription;

import groovy.lang.GroovyClassLoader;

/**
 * <p>
 * This class is a main entry point for scaffolders.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String scaffolderHome = System.getProperty("scaffolder.home");
        if (scaffolderHome == null) {
            System.err.println("You must provide the scaffolder.home Java property");
            System.exit(1);
        } else {
            System.out.println("Using Scaffolder home [" + scaffolderHome + "]");
        }

        File scaffoldersDir = new File(scaffolderHome, "scaffolders");

        Options options = new Options();
        options.addOption(new Option("o", "overwrite", false, "Overwrite existing files created by the scaffolder"));

        CommandLineParser parser = new PosixParser();
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
                printAllHelp(scaffoldersDir);
            } else {
                File scaffolderDir = new File(scaffoldersDir, commands[0]);
                Scaffolder scaffolder = makeScaffolder(commands[0], scaffolderDir);
                scaffolder.setDir(scaffolderDir);
                scaffolder.setOverwrite(line.hasOption("overwrite"));
                scaffolder.execute();
            }
        } else if (commands.length == 2) {
            File scaffolderDir = new File(scaffoldersDir, commands[1]);
            Scaffolder scaffolder = makeScaffolder(commands[1], scaffolderDir);
            LongDescription description = scaffolder.getClass().getAnnotation(LongDescription.class);
            if (description != null) {
                System.out.println(description.value());
            } else {
                System.out.println("The scaffolder [" + commands[1] + "] has no help.");
            }
        }
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

    private static void printAllHelp(File scaffoldersDir) {
        File[] list = scaffoldersDir.listFiles();
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