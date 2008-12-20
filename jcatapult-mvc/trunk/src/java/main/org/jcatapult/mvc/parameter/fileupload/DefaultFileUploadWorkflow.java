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
package org.jcatapult.mvc.parameter.fileupload;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jcatapult.config.Configuration;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.parameter.el.ExpressionEvaluator;
import org.jcatapult.mvc.parameter.fileupload.annotation.FileUpload;
import org.jcatapult.servlet.WorkflowChain;

import com.google.inject.Inject;
import static net.java.lang.ObjectTools.*;
import net.java.util.IteratorEnumeration;
import net.java.util.Pair;

/**
 * <p>
 * This class parses the incoming request for files and then sets those files
 * onto the action.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultFileUploadWorkflow implements FileUploadWorkflow {
    public final String[] DEFAULT_TYPES = {"text/plain", "text/xml", "text/rtf", "text/richtext", "text/html", "text/css",
        "image/ jpeg", "image/ gif", "image/ png", "image/pjpeg", "image/tiff",
        "video/dv", "video/h261", "video/h262", "video/h263", "video/h264", "video/jpeg", "video/mp4", "video/mpeg", "video/mpv", "video/ogg", "video/quicktime", "video/x-flv",
        "application/msword", "application/pdf", "application/msword", "application/msexcel", "application/mspowerpoint"};
    private final HttpServletRequest request;
    private final ActionInvocationStore actionInvocationStore;
    private final MessageStore messageStore;
    private final ExpressionEvaluator expressionEvaluator;

    private final long maxSize;
    private final String[] contentTypes;

    @Inject
    public DefaultFileUploadWorkflow(HttpServletRequest request, ActionInvocationStore actionInvocationStore,
            MessageStore messageStore, ExpressionEvaluator expressionEvaluator, Configuration configuration) {
        this.request = request;
        this.messageStore = messageStore;
        this.expressionEvaluator = expressionEvaluator;
        this.actionInvocationStore = actionInvocationStore;

        String[] types = configuration.getStringArray("jcatapult.mvc.file-upload.allowed-content-types");
        if (types.length == 0) {
            types = DEFAULT_TYPES;
        }
        this.contentTypes = types;
        this.maxSize = configuration.getLong("jcatapult.mvc.file-upload.max-size", 1024000);
    }

    /**
     * Handles the incoming file uploads.
     *
     * @param   chain The workflow chain.
     */
    @SuppressWarnings("unchecked")
    public void perform(WorkflowChain chain) throws IOException, ServletException {
        Map<String, FileInfo> files = null;
        if (ServletFileUpload.isMultipartContent(request)) {
            ActionInvocation actionInvocation = actionInvocationStore.getCurrent();
            Object action = actionInvocation.action();
            
            Pair<Map<String, FileInfo>, Map<String, String[]>> pair = handleFiles();
            files = pair.first;
            final Map<String, String[]> parameters = pair.second;

            // Set the files into the action
            for (Iterator<String> i = files.keySet().iterator(); i.hasNext(); ) {
                String key = i.next();
                FileInfo info = files.get(key);

                FileUpload fileUpload = expressionEvaluator.getAnnotation(FileUpload.class, key, action);
                if (fileUpload != null && !smallEnough(info, fileUpload) || !smallEnough(info)) {
                    messageStore.addFileUploadSizeError(key, actionInvocation.uri(), info.getFile().length());
                    info.deleteTempFile();
                    i.remove();
                } else if (fileUpload != null && !validContentType(info, fileUpload) || !validContentType(info)) {
                    messageStore.addFileUploadContentTypeError(key, actionInvocation.uri(), info.getContentType());
                    info.deleteTempFile();
                    i.remove();
                } else {
                    expressionEvaluator.setValue(key, action, info);
                }
            }

            HttpServletRequestWrapper wrapper = (HttpServletRequestWrapper) request;
            HttpServletRequest previous = (HttpServletRequest) wrapper.getRequest();
            HttpServletRequest newRequest = new HttpServletRequestWrapper(previous) {
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
            };
            wrapper.setRequest(newRequest);
        }

        try {
            chain.continueWorkflow();
        } finally {
            // Clean up files
            if (files != null) {
                for (FileInfo fileInfo : files.values()) {
                    fileInfo.deleteTempFile();
                }
            }
        }
    }

    /**
     * Does nothing.
     */
    public void destroy() {
    }

    protected Pair<Map<String, FileInfo>, Map<String, String[]>> handleFiles() {
        Map<String, FileInfo> files = new HashMap<String, FileInfo>();
        Map<String, List<String>> params = new HashMap<String, List<String>>();

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List items = upload.parseRequest(request);
            for (Object item : items) {
                FileItem fileItem = (FileItem) item;
                String name = fileItem.getFieldName();
                if (fileItem.isFormField()) {
                    String value = fileItem.getString();
                    List<String> list = params.get(name);
                    if (list == null) {
                        list = new ArrayList<String>();
                        params.put(name, list);
                    }

                    list.add(value);
                } else {
                    String fileName = fileItem.getName();
                    String contentType = fileItem.getContentType();
                    File file = File.createTempFile("jcatapult", "fileupload");
                    fileItem.write(file);
                    files.put(name, new FileInfo(file, fileName, contentType));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to handle file uploads", e);
        }

        Map<String, String[]> finalParams = new HashMap<String, String[]>();
        for (String key : params.keySet()) {
            finalParams.put(key, params.get(key).toArray(new String[params.get(key).size()]));
        }
        
        return Pair.p(files, finalParams);
    }

    /**
     * Checks the size of the given file against the annotation.
     *
     * @param   info The file info.
     * @param   fileUpload The annotation.
     * @return  True if the file is okay, false if it is too big.
     */
    private boolean smallEnough(FileInfo info, FileUpload fileUpload) {
        return fileUpload.maxSize() == -1 || info.file.length() <= fileUpload.maxSize();
    }

    /**
     * Checks the size of the given file against the global settings.
     *
     * @param   info The file info.
     * @return  True if the file is okay, false if it is too big.
     */
    private boolean smallEnough(FileInfo info) {
        return info.file.length() <= maxSize;
    }

    /**
     * Checks the content type of the given file against the annotation.
     *
     * @param   info The file info.
     * @param   fileUpload The annotation.
     * @return  True if the file is okay, false if it is too big.
     */
    private boolean validContentType(FileInfo info, FileUpload fileUpload) {
        return fileUpload.contentTypes().length == 0 || arrayContains(fileUpload.contentTypes(), info.contentType);
    }

    /**
     * Checks the content type of the global settings.
     *
     * @param   info The file info.
     * @return  True if the file is okay, false if it is too big.
     */
    private boolean validContentType(FileInfo info) {
        return arrayContains(contentTypes, info.contentType);
    }
}