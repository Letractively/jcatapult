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
${field.name}Id=${field.plainEnglishName}
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany")>
${field.name}Ids=${field.plainEnglishName}
  </#if>
</#list>
<@labels type ""/>
