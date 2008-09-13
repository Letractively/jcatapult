<#import "/global/macros.ftl" as g/>
<#macro headers localType prefix>
  <#list localType.allFields as field>
    <#if !field.static && !field.final && field.name != "id" && field.mainType.simpleType &&
            (field.mainType.primitive || !field.hasAnnotation("javax.persistence.Transient"))>
      <th id="${field.name}-header"><a href="index?sortProperty=${prefix}${field.name}">${field.plainEnglishName}</a></th>
    <#elseif !field.static && !field.final && !field.mainType.primitive && field.mainType.hasAnnotation("javax.persistence.Embeddable")>
      <@headers field.mainType prefix + field.name + "." />
    </#if>
  </#list>
</#macro>
<#macro values localType prefix>
  <#assign linked = (prefix != "") />
  <#list localType.allFields as field>
    <#if !field.static && !field.final && field.name != "id" && field.mainType.simpleType &&
            (field.mainType.primitive || !field.hasAnnotation("javax.persistence.Transient"))>
      <#if !linked>
        <td class="[#if ${type.fieldName}_index % 2 == 0]even[#else]odd[/#if] ${field.name}-row"><a href="edit?id=${g.jspEL(prefix + localType.fieldName + '.id')}">${g.jspEL(prefix + localType.fieldName + '.' + field.name)}</a></td>
        <#assign linked=true />
      <#else>
        <td class="[#if ${type.fieldName}_index % 2 == 0]even[#else]odd[/#if] ${field.name}-row">${g.jspEL(prefix +  localType.fieldName + '.' + field.name)}</td>
      </#if>
    <#elseif !field.static && !field.final && !field.mainType.primitive && field.mainType.hasAnnotation("javax.persistence.Embeddable")>
      <@values field.mainType prefix + localType.fieldName + "." />
    </#if>
  </#list>
</#macro>
${r"[#ftl]"}
<html>
<head><title>${type.name} | Index</title></head>
<body>
[@jc.form action="delete" method="POST"]
  <table id="listing">
    <tr>
      <@headers type ""/>
      <th id="delete-header">Delete</th>
    </tr>
    [#list ${type.pluralFieldName} as ${type.fieldName}]
      <tr>
        <@values type ""/>
        <td class="[#if ${type.fieldName}_index % 2 == 0]even[#else]odd[/#if] delete-row">[@jc.checkbox name="ids" value="${g.jspEL(type.fieldName + '.id')}"/]</td>
      </tr>
    [/#list]

    [#if ${type.pluralFieldName}?size == 0]
      <tr>
        <td colspan="4" class="empty-row">No ${type.pluralFieldName} on file</td>
      </tr>
    [/#if]
  </table>
  <div id="listing-controls">
    <a href="add">Add</a>
    [@jc.submit name="delete"/]
  </div>
[/@jc.form]
</body>
</html>