<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head><title>Guest Book | Entries</title></head>
<body>
<s:form action="delete" method="POST" theme="simple">
  <table id="listing">
    <tr>
      <th id="name-header"><a href="index?sortProperty=name">Name</a></th>
      <th id="comment-header"><a href="index?sortProperty=comment">Comment</a></th>
      <th id="insertDate-header"><a href="index?sortProperty=insertDate">Date</a></th>
      <th id="delete-header">Delete</th>
    </tr>
    <c:forEach items="${guestBookEntries}" var="guestBookEntry" varStatus="status">
      <tr>
        <td class="${status.index % 2 == 0 ? 'even' : 'odd'} name-row"><a href="edit?id=${guestBookEntry.id}">${guestBookEntry.name}</a></td>
        <td class="${status.index % 2 == 0 ? 'even' : 'odd'} comment-row">${guestBookEntry.comment}</td>
        <td class="${status.index % 2 == 0 ? 'even' : 'odd'} insertDate-row">${guestBookEntry.insertDate}</td>
        <td class="${status.index % 2 == 0 ? 'even' : 'odd'} delete-row"><s:checkbox name="ids" fieldValue="%{#attr.guestBookEntry.id}"/></td>
      </tr>
    </c:forEach>
    <c:if test="${empty guestBookEntries}">
      <tr>
        <td colspan="4" class="empty-row">No guestBookEntries on file</td>
      </tr>
    </c:if>
  </table>
  <div id="listing-controls">
    <a href="add"><button>ADD</button></a>
    <s:submit type="button" value="DELETE"/>
  </div>
  <c:set var="totalPages" value="${(totalCount / numberPerPage) + (totalCount % numberPerPage > 0 ? 1 : 0)}"/>
  <c:if test="${totalPages >= 2 && !showAll}">
    <div id="pagination-controls">
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
  <c:if test="${totalCount > 25}">
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