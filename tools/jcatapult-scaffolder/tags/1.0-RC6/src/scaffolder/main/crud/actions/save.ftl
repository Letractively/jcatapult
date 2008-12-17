<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import ${servicePackage}.${type.name}Service;
import ${type.fullName};

/**
 * <p>
 * This class is the action that persists the ${type.pluralName}.
 * </p>
 *
 * @author  Scaffolder
 */
@Results({
    @Result(name = "success", location = "index", type = "redirectAction")
})
public class Save extends BaseAction {
    private static final Logger logger = Logger.getLogger(Save.class.getName());
    private final ${type.name}Service ${type.fieldName}Service;
    private final Configuration configuration;
    private ${type.name} ${type.fieldName};
    <@global.idVariables />

    @Inject
    public Save(${type.name}Service ${type.fieldName}Service, Configuration configuration) {
        this.${type.fieldName}Service = ${type.fieldName}Service;
        this.configuration = configuration;
    }

    public ${type.name} get${type.name}() {
        return ${type.fieldName};
    }


    public void set${type.name}(${type.name} ${type.fieldName}) {
        this.${type.fieldName} = ${type.fieldName};
    }

    <@global.idProperties setters=true />

    @Override
    @Actions({
        @Action(value = "save", results = {@Result(name = "input", location = "add.<#if module>ftl<#else>jsp</#if>")}),
        @Action(value = "update", results = {@Result(name = "input", location = "edit.<#if module>ftl<#else>jsp</#if>")})
    })
    public String execute() {
        ${type.fieldName}Service.persist(${type.fieldName}<@global.idParams/>);
        return SUCCESS;
    }
}