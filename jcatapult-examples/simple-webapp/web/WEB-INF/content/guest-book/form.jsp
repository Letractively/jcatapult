<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h2>${param['actionType'] == 'update' ? 'Updating' : 'Adding'} a GuestBookEntry</h2>
<c:set var="actionType" value="${param['actionType']}"/>
<s:form action="%{#attr.actionType}" method="POST">
  <div id="form-notice">
    Notice here.
  </div>
  <!-- Save off the information for updates and deletes -->
  <s:hidden name="ids" value="%{guestBookEntry.id}"/>
  <s:hidden name="guestBookEntry.id"/>

  <s:textfield key="guestBookEntry.name" required="true"/>
  <s:textfield key="guestBookEntry.comment" required="true"/>
  <s:textfield key="guestBookEntry.updateUser" required="true"/>
  <s:textfield key="guestBookEntry.insertUser" required="true"/>

  <s:action name="prepare" id="prepare"/>

  <div id="form-controls">
    <s:submit value="Save"/>
    <c:if test="${param['actionType'] == 'update'}">
      <s:submit action="delete" value="Delete"/>
    </c:if>
    <s:submit name="redirect:index" value="Cancel"/>
  </div>
</s:form>