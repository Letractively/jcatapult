<#import "/global/macros.ftl" as g/>
<#macro fieldErrors localType prefix>
  <#list localType.allFields as field>
    <#if !field.static && !field.final && field.name != "id" && field.mainType.simpleType && g.required(field)>
missing.${prefix}${localType.fieldName}.${field.name}=Please supply the ${field.plainEnglishName} for the ${type.name}.
    <#elseif !field.static && !field.final && !field.mainType.primitive && field.mainType.hasAnnotation("javax.persistence.Embeddable")>
      <@fieldErrors field.mainType prefix + localType.fieldName + "." />
    </#if>
  </#list>
</#macro>
<@fieldErrors type ""/>