<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.List;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;

import com.google.inject.Inject;

import ${type.fullName};
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class is an action that lists out and sorts the ${type.pluralName}.
 * </p>
 *
 * @author  Scaffolder
 */
@Action
public class Index {
    private final ${type.name}Service ${type.fieldName}Service;

    public List<${type.name}> ${type.pluralFieldName};

    @Inject
    public Index(${type.name}Service ${type.fieldName}Service) {
        this.${type.fieldName}Service = ${type.fieldName}Service;
    }

    /**
     * Loads the list.
     *
     * @return  Always {@code success}.
     */
    public String get() {
        ${type.pluralFieldName} = ${type.fieldName}Service.find();
        return "success";
    }
}