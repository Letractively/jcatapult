<#import "/global/macros.ftl" as global>
package ${actionPackage};

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;

import com.google.inject.Inject;

import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class is the action that deletes one or more ${type.name}(s)
 * </p>
 *
 * @author  Scaffolder
 */
@Action
@Redirect(uri = "${uri}/")
public class Delete {
    private final ${type.name}Service ${type.fieldName}Service;
    public int[] ids;

    @Inject
    public Delete(${type.name}Service ${type.fieldName}Service) {
        this.${type.fieldName}Service = ${type.fieldName}Service;
    }

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public String post() {
        if (ids != null && ids.length > 0) {
            ${type.fieldName}Service.deleteMany(ids);
        }

        return "success";
    }
}