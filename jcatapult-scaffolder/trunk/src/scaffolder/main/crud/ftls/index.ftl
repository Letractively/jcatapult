<#import "/global/macros.ftl" as g/>
<#macro headers localType prefix>
  <#list localType.allFields as field>
    <#if !field.static && !field.final && field.name != "id" && field.mainType.simpleType &&
            (field.mainType.primitive || !field.hasAnnotation("javax.persistence.Transient"))>
      <th><a href="index?sortProperty=${prefix}${field.name}">${field.plainEnglishName}</a></th>
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
        <td><a href="edit?id=${g.jspEL(prefix + localType.fieldName + '.id')}">${g.jspEL(prefix + localType.fieldName + '.' + field.name)}</a></td>
        <#assign linked=true />
      <#else>
        <td>${g.jspEL(prefix +  localType.fieldName + '.' + field.name)}</td>
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
[@s.form action="delete" method="POST" theme="simple"]
  <table>
    <tr>
      <@headers type ""/>
      <th>Delete</th>
    </tr>
    [#list ${type.pluralFieldName} as ${type.fieldName}]
      <tr>
        <@values type ""/>
        <td>[@s.checkbox name="ids" fieldValue="${g.jspEL(type.fieldName + '.id')}"/]</td>
      </tr>
    [/#list]

    [#if ${type.pluralFieldName}?size == 0]
      <tr>
        <td colspan="4">No ${type.pluralFieldName} on file</td>
      </tr>
    [/#if]
    <tr>
      <td colspan="3"><a href="add"><button>ADD AN ${type.fieldName?upper_case}</button></a></td>
      <td>[@s.submit type="button" value="DELETE"/]</td>
    </tr>
  </table>
  [#if totalCount % numberPerPage > 0]
    [#assign extra = 1]
  [#else]
    [#assign extra = 0]
  [/#if]
  [#assign totalPages = (totalCount / numberPerPage) + extra]
  [#if totalPages > 1 && !showAll]
    <div id="pagination_controls">
      [#if page > 1]
        <a href="index?page=${g.jspEL('page - 1')}&numberPerPage=${g.jspEL('numberPerPage')}">Prev</a> |
      [/#if]
      [#if page == 1]
        Prev |
      [/#if]

      [#if page - 5 < 1]
        [#assign start = 1]
      [#else]
        [#assign start = page - 5]
      [/#if]

      [#assign end = page - 1]
      [#if end > start]
        [#list start..end as i]
          <a href="index?page=${g.jspEL('i')}&numberPerPage=${g.jspEL('numberPerPage')}">${g.jspEL('i')}</a> |
        [/#list]
      [/#if]
      ${g.jspEL('page')} |

      [#assign start = page + 1]
      [#if page + 5 > totalPages]
        [#assign end = totalPages]
      [#else]
        [#assign end = page + 5]
      [/#if]
      [#if end > start]
        [#list start..end as i]
          <a href="index?page=${g.jspEL('i')}&numberPerPage=${g.jspEL('numberPerPage')}">${g.jspEL('i')}</a> |
        [/#list]
      [/#if]

      [#if totalCount > (page * numberPerPage)]
        <a href="index?page=${g.jspEL('page + 1')}&numberPerPage=${g.jspEL('numberPerPage')}">Next</a>
      [/#if]
      [#if totalCount <= (page * numberPerPage)]
        Next
      [/#if]
    </div>
  [/#if]
  <div id="number_per_page">
    Number per page
    <a href="index?numberPerPage=25">25</a> |
    <a href="index?numberPerPage=100">100</a> |
    <a href="index?showAll=true">Show all</a><br/>
  </div>
[/@s.form]
</body>
</html>