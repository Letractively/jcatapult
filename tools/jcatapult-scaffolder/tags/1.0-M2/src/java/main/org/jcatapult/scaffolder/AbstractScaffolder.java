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
package org.jcatapult.scaffolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.util.Arrays.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.jcatapult.scaffolder.lang.ClassType;
import org.jcatapult.scaffolder.lang.HasAnnotationMethod;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jline.ANSIBuffer;
import jline.Completor;
import jline.ConsoleReader;

/**
 * <p>
 * This class is an abstract scaffolder implementation that provides a number
 * of good utility methods that scaffolders might find useful.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class AbstractScaffolder implements Scaffolder {
    protected File dir;
    protected boolean overwrite;
    protected Configuration configuration = new Configuration();

    /**
     * {@inheritDoc}
     */
    public void setDir(File dir) {
        this.dir = dir;

        // Setup the loader using the directory of the template
        try {
            configuration.setTemplateLoader(new FileTemplateLoader(dir));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create FreeMarker Configuration object", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    /**
     * This method parses the given Java class into an AST representation using the APT engine shipped
     * with JDK 1.6.
     *
     * @param   className The name of the class to locate.
     * @return  This returns the Type for the class name supplied. If that Type could
     *          not be found, null is returned.
     */
    protected ClassType getType(String className) {
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
     * @param   template The template file name (i.e. foo.ftl). This should be either a fully qualified
     *          path to the template file or relative from the current running directory.
     * @param   output The file to place the output.
     * @param   parameters The Map of parameters.
     * @throws  IOException If the FreeMarker configuration could not be loaded or the template could
     *          not be loaded.
     * @throws  TemplateException If the template execution resulted in any type of error.
     */
    @SuppressWarnings("unchecked")
    protected void executeFreemarkerTemplate(String template, String output, Map parameters)
    throws IOException, TemplateException {
        File outputFile = new File(output);
        File templateFile = new File(dir, template);
        if (outputFile.exists() && !overwrite) {
            System.out.println("Skipped [" + outputFile.toString() + "] because it already exists");
            return;
        } else if (outputFile.exists() && overwrite) {
            outputFile.delete();
            System.out.println("Overwriting [" + outputFile.toString() + "] from [" + templateFile.toString() + "]");
        } else {
            System.out.println("Creating [" + outputFile.toString() + "] from [" + templateFile.toString() + "]");
        }

        // Add the methods
        Map newParams = new HashMap(parameters);
        newParams.put("has_annotation", new HasAnnotationMethod());

        // Now, just grab the template using the plain file name.
        Template ftl = configuration.getTemplate(template);
        FileWriter fw = new FileWriter(outputFile);
        ftl.process(newParams, fw);
        fw.flush();
        fw.close();
    }

    /**
     * This method attempts to find the first package in the project's main source tree (src/java/main)
     * that contains the given token. For example, if the token is <code>foo</code> and there is a
     * package named <code>com.example.foo</code>, this method will return that package.
     *
     * @param   token The token to match.
     * @return  The package or null if the token doesn't match.
     */
    protected String getFirstPackage(String token) {
        Queue<File> queue = new LinkedList<File>();
        File main = new File("src/java/main");
        queue.offer(main);
        while (queue.peek() != null) {
            File file = queue.remove();
            if (file.getName().equals(token)) {
                return file.getAbsolutePath().substring(main.getAbsolutePath().length() + 1).replace("/", ".").
                    replace("\\\\", ".");
            }

            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    queue.offer(f);
                }
            }
        }

        return null;
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
    protected String ask(String question, String message, String error, String defaultValue)
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
    protected String ask(String question, String message, String error, String defaultValue,
            Completor completor) {
        String answer = "";
        while (answer.length() == 0) {
            try {
                ConsoleReader reader = new ConsoleReader();
                reader.addCompletor(completor);
                ANSIBuffer buffer = new ANSIBuffer();
                buffer.bold(question).blue(" [" + (defaultValue == null ? "" : defaultValue) + "]").bold(": ");
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
    protected String makeSimpleClassName(String qualifiedClassName) {
        return qualifiedClassName.substring(qualifiedClassName.lastIndexOf(".") + 1);
    }

    /**
     * Based on the simple class name, this creates a property name that can be used for variables
     * within classes. For example, FooBar is converted to fooBar.
     *
     * @param   simpleClassName The simple class name, without the package.
     * @return  The property name.
     */
    protected String makePropertyName(String simpleClassName) {
        return "" + Character.toLowerCase(simpleClassName.charAt(0)) + simpleClassName.substring(1);
    }
}