<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.ArrayList;
import java.util.List;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.validation.annotation.Valid;
import org.jcatapult.persistence.domain.Identifiable;

import com.google.inject.Inject;

import ${type.fullName};
<@global.importFields />

/**
 * <p>
 * This class fetches an existing ${type.name} for editing.
 * </p>
 *
 * @author  Scaffolder
 */
@Action("{id}")
@Redirect(uri = "${uri}/")
public class Edit extends Prepare {
    @Valid
    public ${type.name} ${type.fieldName};
    public int id;
    <@global.idVariables />

    /**
     * Renders the edit form.
     *
     * @return  The {@code input} code if the ${type.name} was found for the ID. Otherwise, {@code error}.
     */
    public String get() {
        ${type.fieldName} = service.findById(id);
        if (${type.fieldName} == null) {
            messageStore.addActionError(MessageScope.REQUEST, "missing");
            return "error";
        }

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
        ${field.name}ID = ${type.fieldName}.get${field.methodName}().getId();

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany")>
        List<Integer> ${field.name}IDs = new ArrayList<Integer>();
        for (Identifiable identifiable : ${type.fieldName}.get${field.methodName}()) {
            ${field.name}IDs.add(identifiable.getId());
        }
        this.${field.name}IDs = ${field.name}IDs.toArray(new Integer[${field.name}IDs.size()]);

  </#if>
</#list>
        return "input";
    }

    /**
     * Handles the form submission.
     *
     * @return  The {@code success} code if the ${type.name} was updated.
     */
    public String post() {
        service.persist(${type.fieldName}<@global.idParams/>);
        return "success";
    }
}