/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package com.texturemedia.catapult.scaffolding.crud;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.texturemedia.catapult.scaffolding.ScaffoldingHelper;
import com.texturemedia.catapult.scaffolding.lang.Type;
import freemarker.template.TemplateException;
import static net.java.util.CollectionTools.mapNV;

/**
 * <p>
 * This class tests the CRUD scaffolder.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class CrudTest {
    @Test
    public void testCrud() throws IOException, TemplateException {
        Type type = ScaffoldingHelper.getType("com.texturemedia.catapult.scaffolding.Bean");
        Map<String, Object> params = mapNV("actionPackage", "com.example.actions.ns.bean",
            "servicePackage", "com.example.services", "url", "/ns", "type", type);
        File actionsDir = new File("target/src/java/main/com/example/actions/ns");
        actionsDir.mkdirs();

        File scriptDir = new File("src/scaffolder/main/crud");
        String scriptPath = scriptDir.getAbsolutePath();
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/delete.ftl",
            new File(actionsDir, "Delete.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/edit.ftl",
            new File(actionsDir, "Edit.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/index.ftl",
            new File(actionsDir, "Index.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/prepare.ftl",
            new File(actionsDir, "Prepare.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/save.ftl",
            new File(actionsDir, "Save.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/validation.ftl",
            new File(actionsDir, "Save-validation.xml"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/messages.ftl",
            new File(actionsDir, "Save.properties"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/actions/package.ftl",
            new File(actionsDir, "package.properties"), true, params);

        File servicesDir = new File("target/src/java/main/com/example/services");
        servicesDir.mkdirs();
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/service/service.ftl",
            new File(servicesDir, "BeanService.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/service/service-impl.ftl",
            new File(servicesDir, "BeanServiceImpl.java"), true, params);

        File webDir = new File("target/web/WEB-INF/content/ns");
        webDir.mkdirs();
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/jsps/add.ftl",
            new File(webDir, "add.jsp"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/jsps/edit.ftl",
            new File(webDir, "edit.jsp"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/jsps/index.ftl",
            new File(webDir, "index.jsp"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/jsps/form.ftl",
            new File(webDir, "form.jsp"), true, params);

        // Make the directory for the action unit tests
        File actionTestsDir = new File("target/src/java/test/com/example/actions/ns");
        actionTestsDir.mkdirs();

        // Create the action unit tests
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/index.ftl",
            new File(actionTestsDir, "IndexTest.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/edit.ftl",
            new File(actionTestsDir, "EditTest.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/save.ftl",
            new File(actionTestsDir, "SaveTest.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/delete.ftl",
            new File(actionTestsDir, "DeleteTest.java"), true, params);
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/prepare.ftl",
            new File(actionTestsDir, "PrepareTest.java"), true, params);

        // Make the directory for the service unit tests
        File serviceTestsDir = new File("target/src/java/test/com/example/service");
        serviceTestsDir.mkdirs();

        // Create the service unit tests
        ScaffoldingHelper.executeFreemarkerTemplate(scriptPath, "/tests/service.ftl",
            new File(serviceTestsDir, "BeanServiceImplTest.java"), true, params);
    }
}