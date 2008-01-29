<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head><title>Bean | Index</title></head>
<body>
<s:form action="delete" method="POST" theme="simple">
  <table>
    <tr>
      <th><a href="index?sortProperty=age">Age</a></th>
      <th><a href="index?sortProperty=name">Name</a></th>
      <th><a href="index?sortProperty=inner.inner.second">Second</a></th>
      <th><a href="index?sortProperty=inner.inner.first">First</a></th>
      <th><a href="index?sortProperty=inner.lastName">Last Name</a></th>
      <th><a href="index?sortProperty=inner.firstName">First Name</a></th>
      <th><a href="index?sortProperty=optional">Optional</a></th>
      <th><a href="index?sortProperty=flag">Flag</a></th>
      <th>Delete</th>
    </tr>
    <c:forEach items="${beans}" var="bean" varStatus="status">
      <tr>
        <td><a href="edit?id=${bean.id}">${bean.age}</a></td>
        <td>${bean.name}</td>
        <td>${bean.inner.innerInner.second}</td>
        <td>${bean.inner.innerInner.first}</td>
        <td>${bean.inner.lastName}</td>
        <td>${bean.inner.firstName}</td>
        <td>${bean.optional}</td>
        <td>${bean.flag}</td>
        <td><s:checkbox name="ids" fieldValue="${bean.id}"/></td>
      </tr>
    </c:forEach>
    <c:if test="${empty beans}">
      <tr>
        <td colspan="4">No beans on file</td>
      </tr>
    </c:if>
    <tr>
      <td colspan="3"><a href="add"><button>ADD AN BEAN</button></a></td>
      <td><s:submit type="button" value="DELETE"/></td>
    </tr>
  </table>
  <c:set var="totalPages" value="${(totalCount / numberPerPage) + (totalCount % numberPerPage > 0 ? 1 : 0)}"/>
  <c:if test="${totalPages >= 2 && !showAll}">
    <div id="pagination_controls">
      <c:if test="${page > 1}">
        <a href="index?page=${page - 1}&numberPerPage=${numberPerPage}">Prev</a> |
      </c:if>
      <c:if test="${page == 1}">
        Prev |
      </c:if>
      <c:forEach begin="${page - 5 < 1 ? 1 : page - 5}" end="${page - 1}" step="1" var="i">
        <a href="index?page=${i}&numberPerPage=${numberPerPage}">${i}</a> |
      </c:forEach>
      ${page} |
      <c:forEach begin="${page + 1}" end="${page + 5 > totalPages ? totalPages : page + 5}" step="1" var="i">
        <a href="index?page=${i}&numberPerPage=${numberPerPage}">${i}</a> |
      </c:forEach>
      <c:if test="${totalCount > (page * numberPerPage)}">
        <a href="index?page=${page + 1}&numberPerPage=${numberPerPage}">Next</a>
      </c:if>
      <c:if test="${totalCount <= (page * numberPerPage)}">
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