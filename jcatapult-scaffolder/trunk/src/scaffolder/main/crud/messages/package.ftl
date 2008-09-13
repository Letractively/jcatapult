<#import "/global/macros.ftl" as g/>
<#macro labels localType prefix>
  <#list localType.allFields as field>
    <#if !field.static && !field.final && field.name != "id" &&
            (field.mainType.fullName == "java.lang.String" || field.mainType.fullName == "boolean" ||
             field.mainType.fullName == "java.lang.Boolean")>
${prefix}${localType.fieldName}.${field.name}=${field.plainEnglishName}
    <#elseif !field.static && !field.final && !field.mainType.primitive && field.mainType.hasAnnotation("javax.persistence.Embeddable")>
      <@labels field.mainType prefix + localType.fieldName + "." />
    </#if>
  </#list>
</#macro>
#
# Labels for the form
#
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
${field.name}ID=${field.plainEnglishName}
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany")>
${field.name}IDs=${field.plainEnglishName}
  </#if>
</#list>
<@labels type ""/>

#
# Errors
#
<#macro fieldErrors localType prefix>
  <#list localType.allFields as field>
    <#if field.mainType.hasAnnotation("org.jcatapult.mvc.validation.annotation.Required")>
${prefix}${localType.fieldName}.${field.name}.required=Please supply the ${field.plainEnglishName} for the ${type.name}.
    <#elseif field.mainType.hasAnnotation("javax.persistence.Embeddable")>
      <@fieldErrors field.mainType prefix + localType.fieldName + "." />
    </#if>
  </#list>
</#macro>
<@fieldErrors type ""/>