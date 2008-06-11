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
        <td class="${g.jspEL("status.index % 2 == 0 ? 'even' : 'odd'")} ${field.name}-row"><a href="edit?id=${g.jspEL(prefix + localType.fieldName + '.id')}">${g.jspEL(prefix + localType.fieldName + '.' + field.name)}</a></td>
        <#assign linked=true />
      <#else>
        <td class="${g.jspEL("status.index % 2 == 0 ? 'even' : 'odd'")} ${field.name}-row">${g.jspEL(prefix +  localType.fieldName + '.' + field.name)}</td>
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
  <table id="listing">
    <tr>
      <@headers type ""/>
      <th id="delete-header">Delete</th>
    </tr>
    <c:forEach items="${g.jspEL(type.pluralFieldName)}" var="${type.fieldName}" varStatus="status">
      <tr>
        <@values type ""/>
        <td class="${g.jspEL("status.index % 2 == 0 ? 'even' : 'odd'")} delete-row"><s:checkbox name="ids" fieldValue="%{#attr.${type.fieldName}.id}"/></td>
      </tr>
    </c:forEach>
    <c:if test="${g.jspEL('empty ' + type.pluralFieldName)}">
      <tr>
        <td colspan="4" class="empty-row">No ${type.pluralFieldName} on file</td>
      </tr>
    </c:if>
  </table>
  <div id="listing-controls">
    <a href="add"><button>ADD AN ${type.fieldName?upper_case}</button></a>
    <s:submit type="button" value="DELETE"/>
  </div>
  <c:set var="totalPages" value="${g.jspEL('(totalCount / numberPerPage) + (totalCount % numberPerPage > 0 ? 1 : 0)')}"/>
  <c:if test="${g.jspEL('totalPages >= 2 && !showAll')}">
    <div id="pagination-controls">
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
  <c:if test="${g.jspEL('totalCount > 25')}">
    <div id="number-per-page">
      Number per page
      <a href="index?numberPerPage=25">25</a> |
      <a href="index?numberPerPage=100">100</a> |
      <a href="index?showAll=true">Show all</a><br/>
    </div>
  </c:if>
</s:form>
</body>
</html>