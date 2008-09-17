<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.ArrayList;
import java.util.List;

import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.result.form.annotation.FormPrepareMethod;

import com.google.inject.Inject;

<@global.importFields />
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class prepares the form that adds and edits ${type.pluralName}.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public abstract class Prepare {
    protected ${type.name}Service service;
    public MessageStore messageStore;

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
    public List<${field.mainType.name}> ${field.pluralName} = new ArrayList<${field.mainType.name}>();
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
    public List<${field.genericTypes[0].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[0].name}>();
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
    public List<${field.genericTypes[1].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[1].name}>();
  </#if>
</#list>

    @Inject
    public void setServices(${type.name}Service service, MessageStore messageStore) {
        this.service = service;
        this.messageStore = messageStore;
    }

    @FormPrepareMethod
    public void prepare() {
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne") || field.hasAnnotation("javax.persistence.ManyToMany")>
        ${field.pluralName} = service.get${field.pluralMethodName}();
  </#if>
</#list>
    }
}