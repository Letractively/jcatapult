/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.ServletContext;

import freemarker.cache.TemplateLoader;

/**
 * <p>
 * This class allows templates to be loaded from multiple locations
 * in a search order. This allows overridding of templates.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class OverridingTemplateLoader implements TemplateLoader {
    private static final Logger logger = Logger.getLogger(OverridingTemplateLoader.class.getName());
    private ServletContext context;
    private String contextPrefix;
    private Class<?> searchClass;
    private String classPathPrefix;

    public OverridingTemplateLoader(ServletContext context, String contextPrefix, Class<?> searchClass,
            String classPathPrefix) {
        this.context = context;
        this.contextPrefix = contextPrefix;
        this.searchClass = searchClass;
        this.classPathPrefix = classPathPrefix;
    }

    public Object findTemplateSource(String name) throws IOException {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Searching in ServletContext for [" + contextPrefix + name + "]");
        }

        URL url = context.getResource(contextPrefix + name);
        if (url == null) {
            // Check the classpath last
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Not found in ServletContext, searching in classpath for [" +
                    classPathPrefix + name + "]");
            }

            url = searchClass.getResource(classPathPrefix + name);
        }

        return url;
    }

    public long getLastModified(Object templateSource) {
        URL url = (URL) templateSource;
        String urlStr = url.toString();
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Trying to determine last modified for URL [" + urlStr + "]");
        }

        if (urlStr.startsWith("file://") && urlStr.indexOf("#") < 0) {
            // Looks like a normal urlStr
            File file = new File(urlStr.substring("file://".length()));
            return file.lastModified();
        }

        return -1;
    }

    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return new InputStreamReader(((URL) templateSource).openStream(), encoding);
    }

    public void closeTemplateSource(Object templateSource) throws IOException {
        // Do nothing
    }
}