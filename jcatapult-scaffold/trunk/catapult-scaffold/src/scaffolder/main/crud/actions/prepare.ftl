<#import "/global/macros.ftl" as global>
package ${actionPackage};

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.opensymphony.xwork2.Action;
<@global.importFields />
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class prepares the form that adds and edits ${type.pluralName}.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Prepare implements Action {
    private static final Logger logger = Logger.getLogger(Prepare.class.getName());
    private final ${type.name}Service ${type.fieldName}Service;
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
    private List<${field.mainType.name}> ${field.pluralName} = new ArrayList<${field.mainType.name}>();
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
    private List<${field.genericTypes[0].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[0].name}>();
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
    private List<${field.genericTypes[1].name}> ${field.pluralName} = new ArrayList<${field.genericTypes[1].name}>();
  </#if>
</#list>

    @Inject
    public Prepare(${type.name}Service ${type.fieldName}Service) {
        this.${type.fieldName}Service = ${type.fieldName}Service;
    }

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
    public List<${field.mainType.name}> get${field.pluralMethodName}() {
        return ${field.pluralName};
    }

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name != "java.util.Map">
    public List<${field.genericTypes[0].name}> get${field.pluralMethodName}() {
        return ${field.pluralName};
    }

  <#elseif field.hasAnnotation("javax.persistence.ManyToMany") && field.mainType.name == "java.util.Map">
    public List<${field.genericTypes[1].name}> get${field.pluralMethodName}() {
        return ${field.pluralName};
    }

  </#if>
</#list>
    public String execute() {
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne") || field.hasAnnotation("javax.persistence.ManyToMany")>
        ${field.pluralName} = ${type.fieldName}Service.get${field.pluralMethodName}();
  </#if>
</#list>
        return SUCCESS;
    }
}