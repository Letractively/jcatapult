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
import java.util.ArrayList;
import java.util.List;

import jline.SimpleCompletor;

/**
 * <p>
 * This class is a completor for the JLine command-line interaction library
 * that finds the types of scaffolders and provides options.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class ScaffoldingCompletor extends SimpleCompletor {
    public ScaffoldingCompletor(File scaffoldersDir) {
        super(findScaffolders(scaffoldersDir));
    }

    /**
     * Using the Scaffolding home directory, this returns an array of all the scaffolders inside
     * the directory. Scaffolders are denoted because each scaffolder is itself a directory.
     *
     * @param   scaffoldersDir The location of the scaffolders.
     * @return  The list of scaffolders.
     */
    private static String[] findScaffolders(File scaffoldersDir) {
        List<String> dirs = new ArrayList<String>();
        File[] files = scaffoldersDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                dirs.add(file.getName());
            }
        }
        return dirs.toArray(new String[dirs.size()]);
    }
}