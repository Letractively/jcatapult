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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head><title>${type.name} | Index</title></head>
<body>
<s:form action="delete" method="POST" theme="simple">
  <table>
    <tr>
      <@headers type ""/>
      <th>Delete</th>
    </tr>
    <c:forEach items="${g.jspEL(type.pluralFieldName)}" var="${type.fieldName}" varStatus="status">
      <tr>
        <@values type ""/>
        <td><s:checkbox name="ids" fieldValue="%{#attr.${type.fieldName}.id}"/></td>
      </tr>
    </c:forEach>
    <c:if test="${g.jspEL('empty ' + type.pluralFieldName)}">
      <tr>
        <td colspan="4">No ${type.pluralFieldName} on file</td>
      </tr>
    </c:if>
    <tr>
      <td colspan="3"><a href="add"><button>ADD AN ${type.fieldName?upper_case}</button></a></td>
      <td><s:submit type="button" value="DELETE"/></td>
    </tr>
  </table>
  <c:set var="totalPages" value="${g.jspEL('(totalCount / numberPerPage) + (totalCount % numberPerPage > 0 ? 1 : 0)')}"/>
  <c:if test="${g.jspEL('totalPages >= 2 && !showAll')}">
    <div id="pagination_controls">
      <c:if test="${g.jspEL('page > 1')}">
        <a href="index?page=${g.jspEL('page - 1')}&numberPerPage=${g.jspEL('numberPerPage')}">Prev</a> |
      </c:if>
      <c:if test="${g.jspEL('page == 1')}">
        Prev |
      </c:if>
      <c:forEach begin="${g.jspEL('page - 5 < 1 ? 1 : page - 5')}" end="${g.jspEL('page - 1')}" step="1" var="i">
        <a href="index?page=${g.jspEL('i')}&numberPerPage=${g.jspEL('numberPerPage')}">${g.jspEL('i')}</a> |
      </c:forEach>
      ${g.jspEL('page')} |
      <c:forEach begin="${g.jspEL('page + 1')}" end="${g.jspEL('page + 5 > totalPages ? totalPages : page + 5')}" step="1" var="i">
        <a href="index?page=${g.jspEL('i')}&numberPerPage=${g.jspEL('numberPerPage')}">${g.jspEL('i')}</a> |
      </c:forEach>
      <c:if test="${g.jspEL('totalCount > (page * numberPerPage)')}">
        <a href="index?page=${g.jspEL('page + 1')}&numberPerPage=${g.jspEL('numberPerPage')}">Next</a>
      </c:if>
      <c:if test="${g.jspEL('totalCount <= (page * numberPerPage)')}">
        Next
      </c:if>
    </div>
  </c:if>
  <div id="number_per_page">
    Number per page
    <a href="index?numberPerPage=5">5</a> |
    <a href="index?numberPerPage=10">10</a> |
    <a href="index?numberPerPage=25">25</a> |
    <a href="index?numberPerPage=50">50</a> |
    <a href="index?numberPerPage=100">100</a> |
    <a href="index?showAll=true">Show all</a><br/>
  </div>
</s:form>
</body>
</html>