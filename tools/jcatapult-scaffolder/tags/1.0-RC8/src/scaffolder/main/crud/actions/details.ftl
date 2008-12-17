<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.ArrayList;
import java.util.List;

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.action.result.annotation.Redirect;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.validation.annotation.Valid;
import org.jcatapult.persistence.domain.Identifiable;

import com.google.inject.Inject;

import ${type.fullName};
import ${servicePackage}.${type.name}Service;
<@global.importFields />

/**
 * <p>
 * This class fetches an existing ${type.name} for viewing.
 * </p>
 *
 * @author  Scaffolder
 */
@Action("{id}")
@Redirect(code = "error", uri = "${uri}/")
public class Details {
    private final ${type.name}Service service;
    private final MessageStore messageStore;
    public ${type.name} ${type.fieldName};
    public int id;

    @Inject
    public Details(${type.name}Service service, MessageStore messageStore) {
        this.service = service;
        this.messageStore = messageStore;
    }

    /**
     * Fetch the ${type.name} for viewing.
     *
     * @return  The {@code success} code if the ${type.name} was found for the ID. Otherwise, {@code error}.
     */
    public String get() {
        ${type.fieldName} = service.findById(id);
        if (${type.fieldName} == null) {
            messageStore.addActionError(MessageScope.FLASH, "missing");
            return "error";
        }

        return "success";
    }
}