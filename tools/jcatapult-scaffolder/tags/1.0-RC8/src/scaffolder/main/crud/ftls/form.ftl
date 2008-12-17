<#import "/global/macros.ftl" as g/>
<#macro inputs localType prefix>
  <#list localType.allFields as field>
    <#if !field.static && !field.final && field.name != "id" && field.mainType.simpleType &&
            field.mainType.fullName != "boolean" && field.mainType.fullName != "java.lang.Boolean">
  [@jc.text name="${prefix}${localType.fieldName}.${field.name}" required=${g.required(field)?string}/]
    <#elseif !field.static && !field.final && field.name != "id" && (field.mainType.fullName == "boolean" || field.mainType.fullName == "java.lang.Boolean")>
  [@jc.checkbox name="${prefix}${localType.fieldName}.${field.name}" required=${g.required(field)?string}/]
    <#elseif !field.static && !field.final && !field.mainType.primitive && field.mainType.hasAnnotation("javax.persistence.Embeddable")>
      <@inputs field.mainType prefix + localType.fieldName + "." />
    </#if>
  </#list>
</#macro>
<#macro listValue field>
  <#assign prop = g.firstPropertyName(field)/>
  <#if prop != "">
    textExpr="${prop}"<#t/>
  </#if>
</#macro>
${r"[#ftl]"}
<h2>[#if actionType == 'update']Updating[#else]Adding[/#if] a ${type.name}</h2>
[@jc.form action=actionType method="POST"]
  <div id="form-notice">
    Notice here.
  </div>
  <!-- Save off the information for edits -->
  [@jc.hidden name="${type.fieldName}.id"/]

  <@inputs type ""/>

<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
  [@jc.select items=${field.pluralName} name="${field.name}ID" valueExpr="id" <@listValue field/> required=true/]
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany")>
  [@jc.checkboxlist items=${field.pluralName} name="${field.name}IDs" valueExpr="id" <@listValue field/> required=true/]
  </#if>
</#list>

  <div id="form-controls">
    [@jc.submit name="save"/]
    <a href="${uri}/">Cancel</a>
  </div>
[/@jc.form]