package ${actionPackage};

import org.texturemedia.smarturls.Result;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionSupport;
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class is the action that deletes one or more ${type.name}(s)
 * </p>
 *
 * @author Scaffolder
 */
@Result(name = "success", location = "index", type = "redirect-action")
public class Delete extends ActionSupport {
    private final ${type.name}Service ${type.fieldName}Service;
    private int[] ids;

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

    @Override
    public String execute() {
        if (ids != null && ids.length > 0) {
            ${type.fieldName}Service.deleteMany(ids);
        }

        return SUCCESS;
    }
}