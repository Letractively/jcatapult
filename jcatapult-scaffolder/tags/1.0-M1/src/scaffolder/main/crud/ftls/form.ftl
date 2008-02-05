<#import "/global/macros.ftl" as g/>
<#macro inputs localType prefix>
  <#list localType.allFields as field>
    <#if !field.static && !field.final && field.name != "id" && field.mainType.simpleType &&
            field.mainType.fullName != "boolean" && field.mainType.fullName != "java.lang.Boolean">
  [@s.textfield key="${prefix}${localType.fieldName}.${field.name}" required="${g.required(field)?string}"/]
    <#elseif !field.static && !field.final && field.name != "id" && (field.mainType.fullName == "boolean" || field.mainType.fullName == "java.lang.Boolean")>
  [@s.checkbox key="${prefix}${localType.fieldName}.${field.name}" required="${g.required(field)?string}"/]
    <#elseif !field.static && !field.final && !field.mainType.primitive && field.mainType.hasAnnotation("javax.persistence.Embeddable")>
      <@inputs field.mainType prefix + localType.fieldName + "." />
    </#if>
  </#list>
</#macro>
<#macro listValue field>
  <#assign prop = g.firstPropertyName(field)/>
  <#if prop != "">
    listValue="${prop}"<#t/>
  </#if>
</#macro>
${r"[#ftl]"}
<h2>${type.pluralName}</h2>
[@s.form action="${r"${actionType}"}" method="POST" theme="semantic"]
  <h3>${type.name}</h3>
  <div class="notice">
    Notice here.
  </div>
  <!-- Save off the information for updates and deletes -->
  [@s.hidden name="ids" value="%{${type.fieldName}.id}"/]
  [@s.hidden name="${type.fieldName}.id"/]

  <@inputs type ""/>

  [@s.action name="prepare" id="prepare"/]
<#list type.allFields as field>
  <#if field.hasAnnotation("javax.persistence.ManyToOne")>
  [@s.select list="%{#prepare.${field.pluralName}}" key="${field.name}Id" listKey="id" <@listValue field/> required="true"/]
  <#elseif field.hasAnnotation("javax.persistence.ManyToMany")>
  [@s.checkboxlist list="%{#prepare.${field.pluralName}}" key="${field.name}Ids" listKey="id" <@listValue field/> required="true"/]
  </#if>
</#list>

  [@s.submit value="Save"/]
  [#if actionType =="update"]
    [@s.submit action="delete" value="Delete"/]
  [/#if]
  [@s.submit name="redirectAction:index" value="Cancel"/]
[/@s.form]