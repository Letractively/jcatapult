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
package org.jcatapult.mvc.result.control;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.jcatapult.freemarker.FreeMarkerService;
import org.jcatapult.locale.annotation.CurrentLocale;
import org.jcatapult.mvc.action.ActionInvocation;
import org.jcatapult.mvc.action.ActionInvocationStore;
import org.jcatapult.mvc.result.form.control.AppendAttributesMethod;

import com.google.inject.Inject;
import freemarker.core.Environment;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>
 * This class an abstract Control implementation that is useful for creating new
 * controls that might need access to things such as the request, the action
 * invocation and attributes.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractControl implements Control {
    protected Locale locale;
    protected FreeMarkerService freeMarkerService;
    protected HttpServletRequest request;
    protected ActionInvocation actionInvocation;
    protected Object action;
    protected Object root;

    @Inject
    public void setServices(@CurrentLocale Locale locale, HttpServletRequest request,
            ActionInvocationStore actionInvocationStore, FreeMarkerService freeMarkerService) {
        this.locale = locale;
        this.request = request;
        this.freeMarkerService = freeMarkerService;
        this.actionInvocation = actionInvocationStore.getCurrent();
        this.action = this.actionInvocation.action();
    }

    /**
     * <p>
     * Implements the controls renderStart method that is called directly by the JSP taglibs. This
     * method is the main render point for the control and it uses the {@link FreeMarkerService} to
     * render the control. Sub-classes need to implement a number of methods in order to setup the
     * Map that is passed to FreeMarker as well as determine the name of the template.
     * </p>
     *
     * @param   writer The writer to output to.
     * @param   attributes The attributes.
     * @param   dynamicAttributes The dynamic attributes from the tag. Dynamic attributes start with
     *          an underscore.
     */
    public void renderStart(Writer writer, Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
        addAdditionalAttributes(attributes, dynamicAttributes);
        root = makeRoot(makeParameters(attributes, dynamicAttributes));

        if (startTemplateName() != null) {
            String templateName = "/WEB-INF/control-templates/" + startTemplateName();
            freeMarkerService.render(writer, templateName, root, locale);
        }
    }

    /**
     * <p>
     * Implements the controls renderEnd method that is called directly by the JSP taglibs. This
     * method is the main render point for the control and it uses the {@link FreeMarkerService} to
     * render the control. Sub-classes need to implement a number of methods in order to setup the
     * Map that is passed to FreeMarker as well as determine the name of the template.
     * </p>
     *
     * @param   writer The writer to output to.
     */
    public void renderEnd(Writer writer) {
        if (endTemplateName() != null) {
            String templateName = "/WEB-INF/control-templates/" + endTemplateName();
            freeMarkerService.render(writer, templateName, root, locale);
        }
    }

    /**
     * <p>
     * Creats the parameters Map that is the root node used by the FreeMarker template when rendering.
     * This places these values in the root map:
     * </p>
     *
     * <ul>
     * <li>attributes - The attributes</li>
     * <li>dynamic_attributes - The dynamic attributes</li>
     * <li>append_attributes - A FreeMarker method that appends attributes ({@link AppendAttributesMethod})</li>
     * </ul>
     *
     * @param   attributes The attributes from the tag.
     * @param   dynamicAttributes The dynamic attributes from the tag. Dynamic attributes start with
     *          an underscore.
     * @return  The Parameters Map.
     */
    protected Map<String, Object> makeParameters(Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("attributes", attributes);
        parameters.put("dynamic_attributes", dynamicAttributes);
        parameters.put("append_attributes", new AppendAttributesMethod());
        return parameters;
    }

    /**
     * Converts the given parameters into a FreeMarker root node. This can be overridden by sub-classes
     * to convert the Map or wrap it. This method simply returns the given Map.
     *
     * @param   parameters The parameters.
     * @return  The root.
     */
    protected Object makeRoot(Map<String, Object> parameters) {
        return parameters;
    }

    /**
     * Chains to the {@link #renderStart(Writer, Map, Map)} method and the {@link Control#renderEnd(java.io.Writer)}
     *
     * @param   env The FreeMarker environment.
     * @param   params The parameters passed to this control in the FTL file.
     * @param   loopVars Loop variables (not really used).
     * @param   body The body of the directive.
     */
    @SuppressWarnings("unchecked")
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
    throws IOException, TemplateException {
        DefaultObjectWrapper objectWrapper = new DefaultObjectWrapper();
        Map<String, String> parameterAttributes = new HashMap<String, String>();
        for (Object o : params.keySet()) {
            String key = (String) o;
            if (key.startsWith("_")) {
                parameterAttributes.put(key.substring(1), params.get(key).toString());
            }
        }

        Map<String, Object> map = new HashMap<String, Object>(params.size());
        for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object value = entry.getValue();
            if (value != null) {
                map.put((String) entry.getKey(), objectWrapper.unwrap((TemplateModel) value));
            }
        }

        renderStart(env.getOut(), map, parameterAttributes);
        if (body != null) {
            body.render(env.getOut());
        }
        renderEnd(env.getOut());
    }

    /**
     * Sub-classes can implement this method to add additional attributes. This is primarily used
     * by control tags to determine values, checked states, selected options, etc.
     *
     * @param   attributes The attributes.
     * @param   dynamicAttributes The dynamic attributes from the tag. Dynamic attributes start with
     *          an underscore.
     */
    protected void addAdditionalAttributes(Map<String, Object> attributes, Map<String, String> dynamicAttributes) {
    }

    /**
     * Determines the bundle name using the attribute first, then the request attribute named <code>
     * jcatapultControlBundle</code> and then finally the action class name.
     *
     * @param   attributes The attributes.
     * @return  The bundle name or null.
     */
    protected String determineBundleName(Map<String, Object> attributes) {
        String bundleName = null;
        if (attributes.get("bundle") != null) {
            bundleName = (String) attributes.remove("bundle");
        } else if (request.getAttribute("jcatapultControlBundle") != null) {
            bundleName = (String) request.getAttribute("jcatapultControlBundle");
        } else if (action != null) {
            bundleName = action.getClass().getName();
        }

        return bundleName;
    }

    /**
     * @return  The name of the FreeMarker template that this control renders when it starts.
     */
    protected abstract String startTemplateName();

    /**
     * @return  The name of the FreeMarker template that this control renders when it ends.
     */
    protected abstract String endTemplateName();
}