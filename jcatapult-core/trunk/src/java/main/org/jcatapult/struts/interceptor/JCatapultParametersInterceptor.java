/*
 * Copyright (c) 2001-2006, JCatapult.org, All Rights Reserved
 */
package org.jcatapult.struts.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.ParameterNameAware;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * <p>
 * This is an interceptor that supports the notion of attributes for parameters.
 * This is useful because Struts makes it difficult to collapsed multiple
 * form fields into a single type, such as a phone number or a Money object
 * that requires a currency. This interceptor allows parameters to have any
 * number of attributes, which are also HTTP request parameters, but they are
 * assoicated with another parameters.
 * </p>
 *
 * <p>
 * The way that this interceptor separates between parameters and attributes
 * is using the @ (at) symbol. This symbol marks an HTTP request parameter
 * as an attribute. Let's say that we want to add the currency code attribute
 * to a parameter named <strong>user.accountBalance</strong>. The HTTP request
 * would need an additional parameter named:
 * </p>
 *
 * <pre>
 * user.accountBalance@currencyCode
 * </pre>
 *
 * <p>
 * This attribute would then be pulled into this interceptor and put on the
 * ValueStack context under the key <strong>user.accountBalance@currencyCode</strong>.
 * By placing it on the ValueStack context, type converters and other classes
 * will have access to it.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class JCatapultParametersInterceptor extends ParametersInterceptor {
    private static final Logger logger = Logger.getLogger(JCatapultParametersInterceptor.class.getName());
    public static final String ATTRIBUTES = "__attributes";
    static boolean devMode = false;

    @Inject(value = "devMode", required = false)
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    protected void setParameters(Object action, ValueStack stack, Map parameters) {
        ParameterNameAware parameterNameAware = (action instanceof ParameterNameAware)
            ? (ParameterNameAware) action : null;

        Map<String, Object> params;
        if (isOrdered()) {
            params = new TreeMap<String, Object>(getOrderedComparator());
            params.putAll(parameters);
        } else {
            params = new TreeMap<String, Object>(parameters);
        }

        // Make the map of parameters to attributes of the parameters
        Map<String, Struct> structs = new HashMap<String, Struct>();
        for (String name : params.keySet()) {
            int index = name.indexOf("@");
            String parameter = (index > 0) ? name.substring(0, index) : name;
            boolean acceptableName = acceptableName(parameter) &&
                (parameterNameAware == null || parameterNameAware.acceptableParameterName(parameter));
            if (!acceptableName) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Skipping parameter [" + name + "]");
                }
                continue;
            }

            Struct s = structs.get(parameter);
            if (s == null) {
                s = new Struct();
                structs.put(parameter, s);
            }

            if (index > 0) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Adding attribute [" + name + "] to parameter [" + parameter + "]");
                }
                s.attributes.put(name, ((String[]) parameters.get(name))[0]);
            } else {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Adding parameter value for parameter [" + parameter + "]");
                }
                s.parameter = parameters.get(parameter);
            }
        }

        for (String key : structs.keySet()) {
            Struct s = structs.get(key);
            Object value = s.parameter;

            // Add the attributes to the context
            stack.getContext().put(ATTRIBUTES, s.attributes);

            try {
                stack.setValue(key, value);
            } catch (RuntimeException e) {
                if (devMode) {
                    String developerNotification = LocalizedTextUtil.findText(ParametersInterceptor.class,
                        "devmode.notification", ActionContext.getContext().getLocale(), "Developer Notification:\n{0}",
                        new Object[]{e.getMessage()});
                    logger.log(Level.SEVERE, developerNotification, e);
                    if (action instanceof ValidationAware) {
                        ((ValidationAware) action).addActionMessage(developerNotification);
                    }
                } else {
                    logger.severe("ParametersInterceptor - [setParameters]: Unexpected Exception caught setting [" +
                        key + "] on [" + action.getClass() + "] " + e.getMessage());
                }
            }
        }
    }

    public static class Struct {
        Map<String, Object> attributes = new HashMap<String, Object>();
        Object parameter;
    }
}