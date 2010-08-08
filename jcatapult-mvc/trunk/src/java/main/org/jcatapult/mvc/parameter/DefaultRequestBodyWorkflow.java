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
package org.jcatapult.mvc.parameter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import net.java.util.IteratorEnumeration;
import net.java.util.Pair;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jcatapult.mvc.parameter.fileupload.FileInfo;
import org.jcatapult.mvc.util.RequestKeys;
import org.jcatapult.servlet.WorkflowChain;

/**
 * <p>
 * This class parses the incoming request for parameters and files. If there are any files
 * they are set into the action by this class.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@SuppressWarnings("unchecked")
public class DefaultRequestBodyWorkflow implements RequestBodyWorkflow {
    private final HttpServletRequest request;

    @Inject
    public DefaultRequestBodyWorkflow(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Handles the incoming file uploads.
     *
     * @param   chain The workflow chain.
     */
    public void perform(WorkflowChain chain) throws IOException, ServletException {
        Map<String, List<FileInfo>> files = null;
        if (multipart(request)) {
            Pair<Map<String, List<FileInfo>>, Map<String, String[]>> pair = handleFiles();
            files = pair.first;
            request.setAttribute(RequestKeys.FILE_ATTRIBUTE, files);

            // Next, fake the request with the parameters from the multi-part
            Map<String, String[]> parameters = pair.second;
            HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) request;
            HttpServletRequest previous = (HttpServletRequest) wrapper.getRequest();
            HttpServletRequest newRequest = new ParameterHttpServletRequestWrapper(previous, parameters);
            wrapper.setRequest(newRequest);
        } else if (hasParametersInBody(request)) {
            // Parse the body and put them in a new request
            Map<String, List<String>> parameters = parse(request.getInputStream(), request.getCharacterEncoding());
            HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) request;
            HttpServletRequest previous = (HttpServletRequest) wrapper.getRequest();
            HttpServletRequest newRequest = new ParameterHttpServletRequestWrapper(previous, convert(parameters));
            wrapper.setRequest(newRequest);
        }

        try {
            chain.continueWorkflow();
        } finally {
            // Clean up files
            if (files != null) {
                for (String key : files.keySet()) {
                    List<FileInfo> list = files.get(key);
                    for (FileInfo info : list) {
                        info.deleteTempFile();
                    }
                }
            }
        }
    }

    protected Pair<Map<String, List<FileInfo>>, Map<String, String[]>> handleFiles() {
        Map<String, List<FileInfo>> files = new HashMap<String, List<FileInfo>>();
        Map<String, List<String>> params = new HashMap<String, List<String>>();

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item : items) {
                String name = item.getFieldName();
                if (item.isFormField()) {
                    String value = item.getString();
                    List<String> list = params.get(name);
                    if (list == null) {
                        list = new ArrayList<String>();
                        params.put(name, list);
                    }

                    list.add(value);
                } else {
                    String fileName = item.getName();

                    // Handle lame ass IE issues with file names
                    if (fileName.contains(":\\")) {
                        int index = fileName.lastIndexOf("\\");
                        fileName = fileName.substring(index + 1);
                    }

                    String contentType = item.getContentType();
                    File file = File.createTempFile("jcatapult", "fileupload");
                    item.write(file);

                    // Handle when the user doesn't provide a file at all
                    if (file.length() == 0 || fileName == null || contentType == null) {
                        continue;
                    }

                    List<FileInfo> list = files.get(name);
                    if (list == null) {
                        list = new ArrayList<FileInfo>();
                        files.put(name, list);
                    }

                    list.add(new FileInfo(file, fileName, contentType));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to handle file uploads", e);
        }

        // We can now safely get the parameters from the request (on the URI)
        Map<String, String[]> requestParameters = request.getParameterMap();
        for (String name : requestParameters.keySet()) {
            String[] paramsList = requestParameters.get(name);
            if (paramsList != null) {
                for (String param : paramsList) {
                    List<String> list = params.get(name);
                    if (list == null) {
                        list = new ArrayList<String>();
                        params.put(name, list);
                    }

                    list.add(param);
                }
            }
        }

        Map<String, String[]> finalParams = new HashMap<String, String[]>();
        for (String key : params.keySet()) {
            finalParams.put(key, params.get(key).toArray(new String[params.get(key).size()]));
        }

        return Pair.p(files, finalParams);
    }

    /**
     * Determines if the request contains multipart body.
     *
     * @param   request The HttpServletRequest to check.
     * @return  True if the body multipart data, false otherwise.
     */
    private boolean multipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith("multipart/");
    }

    /**
     * Determines if the request contains parameters in the HTTP body and it should be parsed.
     *
     * @param   request The HttpServletRequest to check.
     * @return  True if the body contains parameters, false otherwise.
     */
    private boolean hasParametersInBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith("application/x-www-form-urlencoded");
    }

    /**
     * Parses the HTTP request body for URL encoded parameters.
     *
     * @param   inputStream The input stream to read from.
     * @param   encoding The encoding header.
     * @return  The parameter map.
     * @throws  IOException If the read failed.
     */
    private Map<String, List<String>> parse(InputStream inputStream, String encoding) throws IOException {
        if (encoding == null) {
            encoding = "UTF-8";
        }

        Map<String, List<String>> params = new HashMap<String, List<String>>();
        byte[] str = new byte[1024];
        int length = 0;
        int c;
        String key = null;
        while ((c = inputStream.read()) != -1) {
            if (c == '=') {
                if (length == 0 || key != null) {
                    throw new IOException("Invalid HTTP URLEncoded request body");
                }

                key = URLDecoder.decode(new String(str, 0, length, encoding), "UTF-8");
                length = 0;
            } else if (c == '&') {
                addParam(params, key, str, length, encoding);
                key = null;
                length = 0;
            } else {
                str[length++] = (byte) c;
                if (length == str.length) {
                    byte[] newStr = new byte[str.length * 2];
                    System.arraycopy(str, 0, newStr, 0, str.length);
                    str = newStr;
                }
            }
        }

        addParam(params, key, str, length, encoding);

        return params;
    }

    private void addParam(Map<String, List<String>> params, String key, byte[] str, int length, String encoding) throws IOException {
        if (key == null) {
            throw new IOException("Invalid HTTP URLEncoded request body");
        }

        String value;
        if (length == 0) {
            value = "";
        } else {
            value = URLDecoder.decode(new String(str, 0, length, encoding), "UTF-8");
        }

        List<String> list = params.get(key);
        if (list == null) {
            list = new ArrayList<String>();
            params.put(key, list);
        }

        list.add(value);
    }

    private Map<String, String[]> convert(Map<String, List<String>> parameters) {
        Map<String, String[]> params = new HashMap<String, String[]>();
        for (String key : parameters.keySet()) {
            List<String> values = parameters.get(key);
            params.put(key, values.toArray(new String[values.size()]));
        }

        return params;
    }

    private static class ParameterHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String[]> parameters;

        public ParameterHttpServletRequestWrapper(HttpServletRequest previous, Map<String, String[]> parameters) {
            super(previous);
            this.parameters = Collections.unmodifiableMap(parameters);
        }

        @Override
        public String getParameter(String s) {
            return (parameters.get(s) != null) ? parameters.get(s)[0] : null;
        }

        @Override
        public Map getParameterMap() {
            return parameters;
        }

        @Override
        public Enumeration getParameterNames() {
            return new IteratorEnumeration(parameters.keySet().iterator());
        }

        @Override
        public String[] getParameterValues(String s) {
            return parameters.get(s);
        }
    }
}
