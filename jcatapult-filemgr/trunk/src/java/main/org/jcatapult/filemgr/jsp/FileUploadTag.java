/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */

package org.jcatapult.filemgr.jsp;

import java.util.Map;
import java.util.HashMap;
import java.io.StringWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.JspException;

import org.jcatapult.guice.GuiceContainer;
import org.jcatapult.freemarker.OverridingTemplateLoader;
import org.jcatapult.container.ContainerResolver;
import org.jcatapult.servlet.ServletObjectsHolder;

import static net.java.util.CollectionTools.map;

import freemarker.template.Template;
import freemarker.template.Configuration;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import com.google.inject.Inject;

/**
 * User: jhumphrey
 * Date: Mar 12, 2008
 */
public class FileUploadTag extends BodyTagSupport implements DynamicAttributes {

    private String name;
    private String label;
    private String fileURI;
    private String deleteURI;
    private Map<String, Object> dynamicAttrs = new HashMap<String, Object>();

    private Configuration freemarkerConfig;

    @Inject
    public void setConfiguration(Configuration freemarkerConfig, org.apache.commons.configuration.Configuration appConfig) {

        String contextPrefix = appConfig.getString("jcatapult.taglib.templates.context.prefix", "/WEB-INF/jcatapult/taglib/templates/");
        String classPathPrefix = appConfig.getString("jcatapult.taglib.templates.classPath.prefix", "/jcatapult/taglib/templates/");

        TemplateLoader loader = new OverridingTemplateLoader(ServletObjectsHolder.getServletContext(), contextPrefix,
            this.getClass(), classPathPrefix);

        this.freemarkerConfig = freemarkerConfig;
        freemarkerConfig.setTemplateLoader(loader);
    }

    /**
     * {@inheritDoc}
     */
    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        dynamicAttrs.put(localName, value);
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            Map<Object, Object> parameters = map("dynamicAttrs", dynamicAttrs, "fileURI", fileURI, "name", name,
                "label", label, "deleteURI", deleteURI);

            Template template = freemarkerConfig.getTemplate("file-upload.ftl");

            StringWriter writer = new StringWriter();
            template.process(parameters, writer);
            this.pageContext.getOut().print(writer.toString());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return EVAL_PAGE;
    }

    /**
     * <p>
     * This method injects the current JSP tag lib class using Guice in order to reveal services and
     * other classes from the Guice Injector. This method should always be called if it is overridden
     * by sub-classes. If it is not called, the sub-class will need to inject itself.
     * </p>
     *
     * @return  The result of calling super.doStartTag()
     * @throws  JspException If the super.doStartTag() throws it.
     */
    @Override
    public int doStartTag() throws JspException {
        // Inject this instance
        GuiceContainer.getInjector().injectMembers(this);
        return super.doStartTag();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFileURI() {
        return fileURI;
    }

    public void setFileURI(String fileURI) {
        this.fileURI = fileURI;
    }

    public String getDeleteURI() {
        return deleteURI;
    }

    public void setDeleteURI(String deleteURI) {
        this.deleteURI = deleteURI;
    }
}
