/*
 * Copyright 2007 (c) by Texture Media, Inc.
 *
 * This software is confidential and proprietary to
 * Texture Media, Inc. It may not be reproduced,
 * published or disclosed to others without company
 * authorization.
 */
package org.jcatapult.scaffold;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import static java.util.Arrays.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.jcatapult.scaffold.lang.ClassType;
import org.jcatapult.scaffold.lang.HasAnnotationMethod;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jline.ANSIBuffer;
import jline.Completor;
import jline.ConsoleReader;

/**
 * <p>
 * This helpers for scaffolders.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class ScaffoldingHelper {
    /**
     * @param   className The name of the class to locate.
     * @return  This returns the Type for the class name supplied. If that Type could
     *          not be found, null is returned.
     */
    public static ClassType getType(String className) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, asList("-proc:only"),
            asList(className), null);
        ScaffoldingAPTProcessor processor = new ScaffoldingAPTProcessor(className);
        task.setProcessors(asList(processor));
        task.call();

        return processor.getType();
    }

    /**
     * Executes the given freemarker template file, passing to the file the Map of parameters.
     *
     * @param   dir The template directory.
     * @param   template The template file name (i.e. foo.ftl). This should be either a fully qualified
     *          path to the template file or relative from the current running directory.
     * @param   file The file to place the output.
     * @param   overwrite Determines if an existing file should be overwritten or not.
     * @param   parameters The Map of parameters.
     * @throws  IOException If the FreeMarker configuration could not be loaded or the template could
     *          not be loaded.
     * @throws  TemplateException If the template execution resulted in any type of error.
     */
    @SuppressWarnings("unchecked")
    public static void executeFreemarkerTemplate(String dir, String template, File file, boolean overwrite,
        Map parameters)
    throws IOException, TemplateException {
        File templateFile = new File(dir, template);
        if (file.exists() && !overwrite) {
            System.out.println("Skipped [" + file.toString() + "] because it already exists");
            return;
        } else if (file.exists() && overwrite) {
            file.delete();
            System.out.println("Overwriting [" + file.toString() + "] from [" + templateFile.toString() + "]");
        } else {
            System.out.println("Creating [" + file.toString() + "] from [" + templateFile.toString() + "]");
        }

        // Setup the loader using the directory of the template
        Configuration configuration = new Configuration();
        configuration.setTemplateLoader(new FileTemplateLoader(new File(dir)));

        // Add the methods
        Map newParams = new HashMap(parameters);
        newParams.put("has_annotation", new HasAnnotationMethod());

        // Now, just grab the template using the plain file name.
        Template ftl = configuration.getTemplate(template);
        FileWriter fw = new FileWriter(file);
        ftl.process(newParams, fw);
        fw.flush();
        fw.close();
    }

    /**
     * Loads up the struts.properties file and looks for the SmartURLs action packages and uses the
     * first one in the list as the default.
     *
     * @return  The action package from the struts.properties file under the <strong>smarturls.action.packages</strong>
     *          key or null if that file or property don't exist.
     */
    public static String getDefaultActionPackage() {
        String defaultActionPackage = null;
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("src/conf/main/struts.properties"));
            String value = props.getProperty("smarturls.action.packages");
            if (value != null) {
                defaultActionPackage = value.split(",")[0].trim();
            }
        } catch (Exception fnfe) {
            // Smother and make sure the user supplies something
        }

        return defaultActionPackage;
    }

    /**
     * Based on the given action package, this method truncates the last package and returns a service
     * package based on that.
     *
     * @param   actionPackage The action package to start from.
     * @return  The service package based on the action package given by the user. If the action package
     *          doesn't have any . characters in it, this returns an empty string.
     */
    public static String getDefaultServicePackage(String actionPackage) {
        int index = actionPackage.lastIndexOf(".");
        if (index < 0) {
            return "";
        }

        return actionPackage.substring(0, index) + ".service";
    }

    /**
     * Calls {@link #ask(String, String, String, String, jline.Completor)} passing in the
     * {@link ClassNameCompletor}.
     *
     * @param   question The question to ask.
     * @param   message The message to give if the question is correctly answered.
     * @param   error The error to display if the question is incorrectly answered (left blank).
     * @param   defaultValue The default value to use (if the user leaves their answer blank.
     * @return  The answer or the default value and never null or empty String.
     * @throws  IOException If the ClassNameCompletor could not lookup the classes.
     */
    public static String ask(String question, String message, String error, String defaultValue)
    throws IOException {
        return ask(question, message, error, defaultValue, new ClassNameCompletor());
    }

    /**
     * Continues to ask the user a question until it gets a good answer. This uses the JLine command line
     * reading interface and currently uses the {@link ClassNameCompletor} for tab completion.
     *
     * @param   question The question to ask.
     * @param   message The message to give if the question is correctly answered.
     * @param   error The error to display if the question is incorrectly answered (left blank).
     * @param   defaultValue The default value to use (if the user leaves their answer blank.
     * @param   completor The JLine completor to use.
     * @return  The answer or the default value and never null or empty String.
     */
    public static String ask(String question, String message, String error, String defaultValue,
            Completor completor) {
        String answer = "";
        while (answer.length() == 0) {
            try {
                ConsoleReader reader = new ConsoleReader();
                reader.addCompletor(completor);
                ANSIBuffer buffer = new ANSIBuffer();
                buffer.blue(question).green(" [" + (defaultValue == null ? "" : defaultValue) + "]").blue(": ");
                answer = reader.readLine(buffer.toString(reader.getTerminal().isANSISupported())).trim();
            } catch (IOException e) {
                System.err.println("Error getting user input");
                e.printStackTrace();
                System.exit(1);
            }

            if (answer.length() == 0 && defaultValue != null) {
                answer = defaultValue;
            }

            if (answer.length() == 0) {
                System.out.println(error);
            } else {
                System.out.println(message + "[" + answer + "]");
            }
        }

        return answer;
    }

    /**
     * Converts the fully qualified class name (including the package name) to a simple name that only
     * contains the class name.
     *
     * @param   qualifiedClassName The fully qualified class name.
     * @return  The simple class name.
     */
    public static String makeSimpleClassName(String qualifiedClassName) {
        return qualifiedClassName.substring(qualifiedClassName.lastIndexOf(".") + 1);
    }

    /**
     * Based on the simple class name, this creates a property name that can be used for variables
     * within classes. For example, FooBar is converted to fooBar.
     *
     * @param   simpleClassName The simple class name, without the package.
     * @return  The property name.
     */
    public static String makePropertyName(String simpleClassName) {
        return "" + Character.toLowerCase(simpleClassName.charAt(0)) + simpleClassName.substring(1);
    }
}