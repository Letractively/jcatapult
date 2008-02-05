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
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * A Completor implementation that completes URLs using the
 * web directory of the project.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class URLPrefixCompletor extends BetterSimpleCompletor {
    public URLPrefixCompletor() throws IOException {
        this("WEB-INF/content");
    }

    public URLPrefixCompletor(String webDir) throws IOException {
        super(getDirNames(webDir));
        setDelimiter("/");
    }

    private static String[] getDirNames(String webDir) throws IOException {
        Set<String> urls = new HashSet<String>();
        getDirNames(webDir, "", urls);
        return urls.toArray(new String[urls.size()]);
    }

    public static void getDirNames(String webDir, String prefix, Set<String> urls) throws IOException {
        File file = new File(webDir);
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            if (f.isDirectory()) {
                urls.add(prefix + f.getName());
                getDirNames(webDir + "/" + f.getName(), "/" + f.getName(), urls);
            }
        }
    }
}