<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.ArrayList;
import java.util.List;

import com.texturemedia.catapult.domain.CatapultBean;
import org.texturemedia.smarturls.Result;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionSupport;
import ${type.fullName};
<@global.importFields />
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class fetches an existing ${type.name} for editing.
 * </p>
 *
 * @author  Scaffolder
 */
@Result(name = "error", location = "index.jsp")
public class Edit extends ActionSupport {
    private final ${type.name}Service ${type.fieldName}Service;
    private Integer id;
    private ${type.name} ${type.fieldName};
    <@global.idVariables />

    @Inject
    public Edit(${type.name}Service ${type.fieldName}Service) {
        this.${type.fieldName}Service = ${type.fieldName}Service;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ${type.name} get${type.name}() {
        return ${type.fieldName};
    }

    <@global.idProperties setters=false/>

    @Override
    public String execute() {
        ${type.fieldName} = ${type.fieldName}Service.getById(id);
        if (${type.fieldName} == null) {
            addActionError("That ${type.name} has been deleted.");
            return ERROR;
        }

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        ${field.name}Id = ${type.fieldName}.get${field.methodName}().getId();

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany")>
        List<Integer> ${field.name}Ids = new ArrayList<Integer>();
        for (CatapultBean bean : ${type.fieldName}.get${field.methodName}()) {
            ${field.name}Ids.add(bean.getId());
        }
        this.${field.name}Ids = ${field.name}Ids.toArray(new Integer[${field.name}Ids.size()]);

  </#if>
</#list>
        return SUCCESS;
    }
}