/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package com.texturemedia.catapult.scaffolding;

import java.io.File;
import java.io.IOException;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * <p>
 * This class is a main entry point for scaffolders. It asks the user
 * what type of scaffolder they want to use and then invokes the Groovy
 * script in the scaffolding directory named <strong>scaffolder.groovy</strong>.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Main {
    public static void main(String[] args) throws IOException {
        boolean overwrite = false;
        if (args.length == 1) {
            if (!args[0].equals("--overwrite")) {
                System.err.println("Usage: scaffold [--overwrite]");
                System.err.println("");
                System.err.println("    --overwrite: Override existing files");
                System.exit(1);
            }

            overwrite = true;
        }

        String scaffolderHome = System.getProperty("scaffolder.home");
        if (scaffolderHome == null) {
            System.err.println("You must provide the scaffolder.home Java property");
            System.exit(1);
        } else {
            System.out.println("Using Scaffolder home [" + scaffolderHome + "]");
        }

        File scaffoldersDir = new File(scaffolderHome, "scaffolders");
        String type = ScaffoldingHelper.ask("Enter scaffolder to use", "Using ", "Invalid scaffolder", null,
            new ScaffoldingCompletor(scaffoldersDir));
        File scaffolderDir = new File(scaffoldersDir, type);
        if (!scaffolderDir.exists() || scaffolderDir.isFile()) {
            System.err.println("Invalid scaffolder [" + type + "]. A directory inside [" + scaffoldersDir +
                "] with the name of the scaffolder doesn't exist.");
            System.exit(1);
        }

        File scaffolder = new File(scaffolderDir, "scaffolder.groovy");

        Binding binding = new Binding();
        binding.setVariable("scriptDir", scaffolderDir);
        binding.setVariable("overwrite", overwrite);
        GroovyShell groovyShell = new GroovyShell(binding);
        groovyShell.evaluate(scaffolder);
    }
}