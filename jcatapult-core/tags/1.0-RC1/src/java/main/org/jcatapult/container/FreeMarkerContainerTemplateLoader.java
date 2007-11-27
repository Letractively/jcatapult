/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
package org.jcatapult.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import freemarker.cache.TemplateLoader;

/**
 * <p>
 * This class is a free marker template loader that uses the {@link ContainerResolver}
 * interface to find the templates. This is a good drop in replacement for the
 * WebAppTemplateLoader that ships with FreeMarker.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerContainerTemplateLoader implements TemplateLoader {
    private final ContainerResolver containerResolver;
    private final String path;

    /**
     * Creates a resource template cache that will use the specified container resolver to load the
     * resources. It will use the base path of <code>"/"</code> meaning templates will be resolved
     * relative to the containers root location.
     *
     * @param   containerResolver The container resolver to use to find the files.
     */
    public FreeMarkerContainerTemplateLoader(ContainerResolver containerResolver) {
        this(containerResolver, "/");
    }

    /**
     * Creates a resource template cache that will use the specified container resolver to load the
     * resources. It will use the base path given as the root relative to the container root location.
     *
     * @param   containerResolver The container resolver to use to find the files.
     * @param   path The base path to template resources.
     */
    public FreeMarkerContainerTemplateLoader(ContainerResolver containerResolver, String path) {
        if (containerResolver == null) {
            throw new IllegalArgumentException("servletContext == null");
        }

        if (path == null) {
            throw new IllegalArgumentException("path == null");
        }

        path = path.replace('\\', '/');
        if (!path.endsWith("/")) {
            path += "/";
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        this.path = path;
        this.containerResolver = containerResolver;
    }

    public Object findTemplateSource(String name) throws IOException {
        String fullPath = path + name;
        // First try to open as plain file (to bypass servlet container resource caches).
        try {
            String realPath = containerResolver.getRealPath(fullPath);
            if (realPath != null) {
                File file = new File(realPath);
                if (!file.isFile()) {
                    return null;
                }

                if (file.canRead()) {
                    return file;
                }
            }
        } catch (SecurityException e) {
            ;// ignore
        }

        // If it fails, try to open it with servletContext.getResource.
        URL url = containerResolver.getResource(fullPath);
        return url == null ? null : new URLTemplateSource(url);
    }

    public long getLastModified(Object templateSource) {
        if (templateSource instanceof File) {
            return ((File) templateSource).lastModified();
        } else {
            return ((URLTemplateSource) templateSource).lastModified();
        }
    }

    public Reader getReader(Object templateSource, String encoding)
    throws IOException {
        if (templateSource instanceof File) {
            return new InputStreamReader(new FileInputStream((File) templateSource), encoding);
        } else {
            return new InputStreamReader(((URLTemplateSource) templateSource).getInputStream(), encoding);
        }
    }

    public void closeTemplateSource(Object templateSource) throws IOException {
        if (templateSource instanceof File) {
            // Do nothing.
        } else {
            ((URLTemplateSource) templateSource).close();
        }
    }
}

class URLTemplateSource {
    private final URL url;
    private URLConnection conn;
    private InputStream inputStream;

    URLTemplateSource(URL url) throws IOException {
        this.url = url;
        this.conn = url.openConnection();
    }

    public boolean equals(Object o) {
        return (o instanceof URLTemplateSource) && url.equals(((URLTemplateSource) o).url);
    }

    public int hashCode() {
        return url.hashCode();
    }

    public String toString() {
        return url.toString();
    }

    long lastModified() {
        return conn.getLastModified();
    }

    InputStream getInputStream() throws IOException {
        inputStream = conn.getInputStream();
        return inputStream;
    }

    void close() throws IOException {
        try {
          if (inputStream != null) {
              inputStream.close();
          }
        } finally {
          inputStream = null;
          conn = null;
        }
    }
}
