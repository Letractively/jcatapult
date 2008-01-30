<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.logging.Logger;

import org.texturemedia.smarturls.ActionNames;
import org.texturemedia.smarturls.ActionName;
import org.texturemedia.smarturls.Results;
import org.texturemedia.smarturls.Result;
import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionSupport;
import ${servicePackage}.${type.name}Service;
import ${type.fullName};

/**
 * <p>
 * This class is the action that persists the ${type.pluralName}.
 * </p>
 *
 * @author  Scaffolder
 */
@ActionNames({
    @ActionName(name = "save"),
    @ActionName(name = "update")
})
@Results({
    @Result(name = "success", location = "index", type = "redirect-action"),
    @Result(action = "save", name = "input", location = "add.jsp"),
    @Result(action = "update", name = "input", location = "edit.jsp")
})
public class Save extends ActionSupport {
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
    public String execute() {
        ${type.fieldName}Service.persist(${type.fieldName}<@global.idParams/>);
        return SUCCESS;
    }
}