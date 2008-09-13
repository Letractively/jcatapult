<#import "/global/macros.ftl" as global>
package ${actionPackage};

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.validation.annotation.Valid;

import com.google.inject.Inject;

import ${type.fullName};

/**
 * <p>
 * This class is only required to localize the form and that is
 * why it is empty.
 * </p>
 *
 * @author  Scaffolder
 */
@Action
@Redirect(uri = "${uri}/")
public class Add extends Prepare {
    @Valid
    public ${type.name} ${type.fieldName};
    <@global.idVariables />

    /**
     * Renders the form with a new empty ${type.name}.
     *
     * @return  The code {@code input}.
     */
    public String get() {
        return "input";
    }

    /**
     * Adds the ${type.name}.
     *
     * @return  The code {@code success}.
     */
    public String post() {
        service.persist(${type.fieldName}<@global.idParams/>);
        return "success";
    }
}